package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Order;
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

    @PostMapping("")
    public Order addOrder(@RequestBody OrderRequest request) {
        return orderService.addOrder(request);
    }
}
