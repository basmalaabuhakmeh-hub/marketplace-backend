package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Order;
import com.example.backendtraining.Data_DBconnection.model.OrderStatusHistory;
import com.example.backendtraining.dto.OrderRequest;
import com.example.backendtraining.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Order getOrderById(@PathVariable int id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/{id}/history")
    public List<OrderStatusHistory> getOrderHistory(@PathVariable int id) {
        return orderService.getOrderHistory(id);
    }

    @PostMapping("")
    public Order addOrder(@RequestBody OrderRequest request) {
        return orderService.addOrder(request);
    }

    @PutMapping("/{id}/accept")
    public Order acceptOrder(@PathVariable int id) {
        return orderService.acceptOrder(id);
    }

    @PutMapping("/{id}/ship")
    public Order shipOrder(@PathVariable int id) {
        return orderService.shipOrder(id);
    }

    @PutMapping("/{id}/assign/{driverId}")
    public Order assignDriver(@PathVariable int id, @PathVariable int driverId) {
        return orderService.assignDriver(id, driverId);
    }

    @PutMapping("/{id}/deliver")
    public Order deliverOrder(@PathVariable int id) {
        return orderService.deliverOrder(id);
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable int id) {
        return orderService.cancelOrder(id);
    }
}
