package com.example.backendtraining.dto;

import com.example.backendtraining.Data_DBconnection.model.PaymentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    @NotEmpty(message = "Order must contain items")
    @Valid
    private List<OrderItemRequest> items;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;
}
