package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Order;
import com.example.backendtraining.Data_DBconnection.model.OrderItem;
import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.Data_DBconnection.model.Role;
import com.example.backendtraining.Data_DBconnection.model.User;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import com.example.backendtraining.Data_DBconnection.repository.OrderRepo;
import com.example.backendtraining.Data_DBconnection.repository.ProductRepo;
import com.example.backendtraining.Data_DBconnection.repository.UserRepo;
import com.example.backendtraining.dto.OrderItemRequest;
import com.example.backendtraining.dto.OrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;

    public OrderService(OrderRepo orderRepo, ProductRepo productRepo, CustomerRepo customerRepo, UserRepo userRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.userRepo = userRepo;
    }

    public List<Order> getOrders() {
        User user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return orderRepo.findAll();
        }
        Customer customer = currentCustomer();
        return orderRepo.findByCustomer(customer);
    }

    public Order getOrderById(int id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        requireOwnerOrAdmin(order);
        return order;
    }

    public Order addOrder(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain items");
        }

        Customer customer = currentCustomer();
        Order order = new Order();
        order.setCustomer(customer);
        order.setTrackNumber((int) (System.currentTimeMillis() % 1_000_000_000));

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            if (itemRequest.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
            }
            Product product = productRepo.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

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

    private void requireOwnerOrAdmin(Order order) {
        User user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        Customer customer = currentCustomer();
        if (order.getCustomer() == null || order.getCustomer().getId() != customer.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own orders");
        }
    }

    private Customer currentCustomer() {
        return customerRepo.findByEmail(currentEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can place orders"));
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
