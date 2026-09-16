package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.PaymentStatus;
import com.example.backendtraining.Data_DBconnection.model.PaymentType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {

    public PaymentStatus process(PaymentType type, double amount) {
        if (type == PaymentType.CASH) {
            return PaymentStatus.PENDING;
        }
        if (type == PaymentType.CREDIT) {
            if (amount <= 0) {
                return PaymentStatus.FAILED;
            }
            return PaymentStatus.PAID;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported payment type");
    }
}
