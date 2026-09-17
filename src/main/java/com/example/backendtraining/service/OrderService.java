package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.Data_DBconnection.model.DriverStatus;
import com.example.backendtraining.Data_DBconnection.model.Order;
import com.example.backendtraining.Data_DBconnection.model.OrderItem;
import com.example.backendtraining.Data_DBconnection.model.OrderStatus;
import com.example.backendtraining.Data_DBconnection.model.OrderStatusHistory;
import com.example.backendtraining.Data_DBconnection.model.PaymentStatus;
import com.example.backendtraining.Data_DBconnection.model.PaymentType;
import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.Data_DBconnection.model.Role;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.model.User;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import com.example.backendtraining.Data_DBconnection.repository.DriverRepo;
import com.example.backendtraining.Data_DBconnection.repository.OrderRepo;
import com.example.backendtraining.Data_DBconnection.repository.OrderStatusHistoryRepo;
import com.example.backendtraining.Data_DBconnection.repository.ProductRepo;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import com.example.backendtraining.Data_DBconnection.repository.UserRepo;
import com.example.backendtraining.dto.OrderItemRequest;
import com.example.backendtraining.dto.OrderRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;
    private final SellerRepo sellerRepo;
    private final DriverRepo driverRepo;
    private final OrderStatusHistoryRepo orderStatusHistoryRepo;
    private final EmailService emailService;
    private final PaymentService paymentService;

    public OrderService(OrderRepo orderRepo, ProductRepo productRepo, CustomerRepo customerRepo,
                        UserRepo userRepo, SellerRepo sellerRepo, DriverRepo driverRepo,
                        OrderStatusHistoryRepo orderStatusHistoryRepo, EmailService emailService,
                        PaymentService paymentService) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.userRepo = userRepo;
        this.sellerRepo = sellerRepo;
        this.driverRepo = driverRepo;
        this.orderStatusHistoryRepo = orderStatusHistoryRepo;
        this.emailService = emailService;
        this.paymentService = paymentService;
    }

    @Transactional(readOnly = true) //"I'm only reading data. I'm not intending to modify the database."
    public List<Order> getOrders() {
        User user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return orderRepo.findAllWithDetails();
        }
        if (user.getRole() == Role.SELLER) {
            return orderRepo.findBySellerId(currentSeller().getId());
        }
        if (user.getRole() == Role.DRIVER) {
            return orderRepo.findByDriver(currentDriver());
        }
        return orderRepo.findByCustomer(currentCustomer());
    }

    @Transactional(readOnly = true)
    public Order getOrderById(int id) {
        Order order = findOrder(id);
        requireCanView(order);
        return order;
    }

    @Transactional
    @CacheEvict(cacheNames = "productCatalog", allEntries = true)
    public Order addOrder(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain items");
        }

        Customer customer = currentCustomer();
        Order order = new Order();
        order.setCustomer(customer);
        order.setTrackNumber((int) (System.currentTimeMillis() % 1_000_000_000));

        List<OrderItemRequest> items = new ArrayList<>(request.getItems());
        // Lock products in id order so two orders cannot deadlock.
        items.sort((a, b) -> Integer.compare(a.getProductId(), b.getProductId()));

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : items) {
            if (itemRequest.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
            }
            // Pessimistic lock so two customers cannot oversell the same stock.
            Product product = productRepo.findByIdForUpdate(itemRequest.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            if (product.isDeleted()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for " + product.getName());
            }
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepo.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            orderItems.add(item);
        }
        order.setOrderItems(orderItems);
        order.setPaymentType(request.getPaymentType());

        double amount = orderItems.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
        PaymentStatus paymentStatus = paymentService.process(request.getPaymentType(), amount);
        if (paymentStatus == PaymentStatus.FAILED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment failed");
        }
        order.setPaymentStatus(paymentStatus);

        Order saved = orderRepo.save(order);
        recordStatusChange(saved, null, OrderStatus.PLACED);
        return findOrder(saved.getId());
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getOrderHistory(int id) {
        Order order = findOrder(id);
        requireCanView(order);
        return orderStatusHistoryRepo.findByOrderIdOrderByChangedAtAsc(id);
    }

    @Transactional
    public Order acceptOrder(int id) {
        Order order = findOrder(id);
        requireCanUpdateStatus(order);
        if (order.getOrderStatus() != OrderStatus.PLACED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PLACED orders can be accepted");
        }
        return applyStatus(order, OrderStatus.ACCEPTED);
    }

    @Transactional
    public Order shipOrder(int id) {
        Order order = findOrder(id);
        requireCanUpdateStatus(order);
        if (order.getOrderStatus() != OrderStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACCEPTED orders can be shipped");
        }
        return applyStatus(order, OrderStatus.SHIPPED);
    }

    @Transactional
    public Order assignDriver(int orderId, int driverId) {
        Order order = findOrder(orderId);
        requireCanUpdateStatus(order);
        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only SHIPPED orders can be assigned a driver");
        }
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
        if (driver.isDeleted() || driver.getStatus() != DriverStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver is not approved");
        }
        order.setDriver(driver);
        return orderRepo.save(order);
    }

    @Transactional
    public Order deliverOrder(int id) {
        Order order = findOrder(id);
        requireCanUpdateStatus(order);
        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only SHIPPED orders can be delivered");
        }
        return applyStatus(order, OrderStatus.DELIVERED);
    }

    @Transactional
    public Order collectPayment(int id) {
        Order order = findOrder(id);
        requireCanCollectPayment(order);
        if (order.getPaymentType() != PaymentType.CASH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only cash orders are collected on delivery");
        }
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cash can only be collected after delivery");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment is already collected");
        }
        order.setPaymentStatus(PaymentStatus.PAID);
        return orderRepo.save(order);
    }

    @Transactional
    @CacheEvict(cacheNames = "productCatalog", allEntries = true)
    public Order cancelOrder(int id) {
        Order order = findOrder(id);
        User user = currentUser();
        if (user.getRole() == Role.CUSTOMER) {
            requireOwner(order);
        } else if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the customer or admin can cancel");
        }
        if (order.getOrderStatus() != OrderStatus.PLACED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PLACED orders can be cancelled");
        }
        restoreStock(order);
        return applyStatus(order, OrderStatus.CANCELLED);
    }

    private Order applyStatus(Order order, OrderStatus newStatus) {
        OrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);
        Order saved = orderRepo.save(order);
        recordStatusChange(saved, oldStatus, newStatus);
        return findOrder(saved.getId());
    }

    private void recordStatusChange(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(currentUser());
        orderStatusHistoryRepo.save(history);

        if (order.getCustomer() != null) {
            emailService.sendOrderStatusUpdate(
                    order.getCustomer().getEmail(),
                    order.getId(),
                    oldStatus,
                    newStatus
            );
        }
    }

    private void restoreStock(Order order) {
        if (order.getOrderItems() == null) {
            return;
        }
        List<OrderItem> items = new ArrayList<>(order.getOrderItems());
        // Lock products in id order so cancel cannot deadlock with addOrder.
        items.sort((a, b) -> Integer.compare(
                a.getProduct() != null ? a.getProduct().getId() : 0,
                b.getProduct() != null ? b.getProduct().getId() : 0));
        for (OrderItem item : items) {
            if (item.getProduct() == null) {
                continue;
            }
            // Pessimistic lock so restore stock cannot race with a new order.
            Product product = productRepo.findByIdForUpdate(item.getProduct().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            product.setStock(product.getStock() + item.getQuantity());
            productRepo.save(product);
        }
    }

    private Order findOrder(int id) {
        return orderRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private void requireCanView(Order order) {
        User user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (user.getRole() == Role.SELLER) {
            if (!containsSellerProduct(order, currentSeller())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view orders that include your products");
            }
            return;
        }
        if (user.getRole() == Role.DRIVER) {
            requireAssignedDriver(order);
            return;
        }
        requireOwner(order);
    }

    private void requireCanUpdateStatus(Order order) {
        User user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (user.getRole() == Role.SELLER && containsSellerProduct(order, currentSeller())) {
            return;
        }
        if (user.getRole() == Role.DRIVER) {
            requireAssignedDriver(order);
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin, the product seller, or the assigned driver can update status");
    }

    private void requireCanCollectPayment(Order order) {
        User user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (user.getRole() == Role.DRIVER) {
            requireAssignedDriver(order);
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned driver or admin can collect cash");
    }

    private boolean containsSellerProduct(Order order, Seller seller) {
        if (order.getOrderItems() == null) {
            return false;
        }
        return order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct() != null
                        && item.getProduct().getSeller() != null
                        && item.getProduct().getSeller().getId() == seller.getId());
    }

    private void requireOwner(Order order) {
        Customer customer = currentCustomer();
        if (order.getCustomer() == null || order.getCustomer().getId() != customer.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own orders");
        }
    }

    private Customer currentCustomer() {
        return customerRepo.findByEmail(currentEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can place orders"));
    }

    private void requireAssignedDriver(Order order) {
        Driver driver = currentDriver();
        if (order.getDriver() == null || order.getDriver().getId() != driver.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access orders assigned to you");
        }
    }

    private Seller currentSeller() {
        return sellerRepo.findByEmail(currentEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not found"));
    }

    private Driver currentDriver() {
        return driverRepo.findByEmail(currentEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Driver not found"));
    }

    private User currentUser() {
        return userRepo.findByEmail(currentEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private String currentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        return authentication.getName();
    }
}
