package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Order;
import com.example.backendtraining.Data_DBconnection.model.OrderItem;
import com.example.backendtraining.Data_DBconnection.model.OrderStatus;
import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.Data_DBconnection.model.Role;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.model.User;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import com.example.backendtraining.Data_DBconnection.repository.OrderRepo;
import com.example.backendtraining.Data_DBconnection.repository.ProductRepo;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import com.example.backendtraining.Data_DBconnection.repository.UserRepo;
import com.example.backendtraining.dto.OrderItemRequest;
import com.example.backendtraining.dto.OrderRequest;
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

    public OrderService(OrderRepo orderRepo, ProductRepo productRepo, CustomerRepo customerRepo,
                        UserRepo userRepo, SellerRepo sellerRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.userRepo = userRepo;
        this.sellerRepo = sellerRepo;
    }

    @Transactional(readOnly = true) //"I'm only reading data. I'm not intending to modify the database."
    public List<Order> getOrders() {
        User user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return orderRepo.findAll();
        }
        if (user.getRole() == Role.SELLER) {
            return orderRepo.findBySellerId(currentSeller().getId());
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
        return orderRepo.save(order);
    }

    @Transactional
    public Order shipOrder(int id) {
        Order order = findOrder(id);
        requireCanUpdateStatus(order);
        if (order.getOrderStatus() != OrderStatus.PLACED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PLACED orders can be shipped");
        }
        order.setOrderStatus(OrderStatus.SHIPPED);
        return orderRepo.save(order);
    }

    @Transactional
    public Order deliverOrder(int id) {
        Order order = findOrder(id);
        requireCanUpdateStatus(order);
        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only SHIPPED orders can be delivered");
        }
        order.setOrderStatus(OrderStatus.DELIVERED);
        return orderRepo.save(order);
    }

    @Transactional
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
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepo.save(order);
    }

    private void restoreStock(Order order) {
        if (order.getOrderItems() == null) {
            return;
        }
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepo.save(product);
            }
        }
    }

    private Order findOrder(int id) {
        return orderRepo.findById(id)
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
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or the product seller can update status");
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

    private Seller currentSeller() {
        return sellerRepo.findByEmail(currentEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not found"));
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
