package com.example.backendtraining.dto;

import com.example.backendtraining.Data_DBconnection.model.ProductType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private ProductType productType;
    private double price;
    private int stock;
}
