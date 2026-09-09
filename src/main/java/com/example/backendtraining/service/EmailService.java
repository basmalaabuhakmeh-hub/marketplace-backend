package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final RestTemplate restTemplate;

    @Value("${otp.service.url}")
    private String otpServiceUrl;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendOrderStatusUpdate(String to, int orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        if (to == null || to.isBlank() || newStatus == null) {
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("to", to);
        body.put("subject", "Order #" + orderId + " update");
        body.put("text", messageFor(oldStatus, newStatus, orderId));

        try {
            restTemplate.postForEntity(otpServiceUrl + "/email/send", body, String.class);
        } catch (Exception e) {
            System.out.println("Could not send order email to " + to + " (" + e.getMessage() + ")");
        }
    }

    private String messageFor(OrderStatus oldStatus, OrderStatus newStatus, int orderId) {
        if (newStatus == OrderStatus.ACCEPTED) {
            return "Your order has been accepted.";
        }
        if (newStatus == OrderStatus.SHIPPED) {
            return "Your order has been shipped.";
        }
        if (newStatus == OrderStatus.DELIVERED) {
            return "Your order has been delivered.";
        }
        if (newStatus == OrderStatus.CANCELLED) {
            return "Your order has been cancelled.";
        }
        if (newStatus == OrderStatus.PLACED) {
            return "Your order has been placed.";
        }
        return "Order #" + orderId + " changed from " + oldStatus + " to " + newStatus + ".";
    }
}
