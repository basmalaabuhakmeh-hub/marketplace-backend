package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Order;
import com.example.backendtraining.Data_DBconnection.model.OrderStatusHistory;
import com.example.backendtraining.dto.OrderRequest;
import com.example.backendtraining.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable @Min(value = 1, message = "Order id must be greater than 0") int id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/{id}/history")
    public List<OrderStatusHistory> getOrderHistory(
            @PathVariable @Min(value = 1, message = "Order id must be greater than 0") int id) {
        return orderService.getOrderHistory(id);
    }

    @PostMapping("")
    public Order addOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.addOrder(request);
    }

    @PutMapping("/{id}/accept")
    public Order acceptOrder(@PathVariable @Min(value = 1, message = "Order id must be greater than 0") int id) {
        return orderService.acceptOrder(id);
    }

    @PutMapping("/{id}/ship")
    public Order shipOrder(@PathVariable @Min(value = 1, message = "Order id must be greater than 0") int id) {
        return orderService.shipOrder(id);
    }

    @PutMapping("/{id}/assign/{driverId}")
    public Order assignDriver(
            @PathVariable @Min(value = 1, message = "Order id must be greater than 0") int id,
            @PathVariable @Min(value = 1, message = "Driver id must be greater than 0") int driverId) {
        return orderService.assignDriver(id, driverId);
    }

    @PutMapping("/{id}/deliver")
    public Order deliverOrder(@PathVariable @Min(value = 1, message = "Order id must be greater than 0") int id) {
        return orderService.deliverOrder(id);
    }

    @PutMapping("/{id}/pay")
    public Order collectPayment(@PathVariable @Min(value = 1, message = "Order id must be greater than 0") int id) {
        return orderService.collectPayment(id);
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable @Min(value = 1, message = "Order id must be greater than 0") int id) {
        return orderService.cancelOrder(id);
    }
}
