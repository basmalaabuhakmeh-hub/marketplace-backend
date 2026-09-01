package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue
    private int id;
    private String name;

    @Enumerated(EnumType.STRING)
    private ProductType ProductType;
    private double price;

    public Product() {
    }

    public Product(int id, String name, ProductType productType, double price) {
        this.id = id;
        this.name = name;
        ProductType = productType;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getProductType() {
        return ProductType;
    }

    public void setProductType(ProductType productType) {
        ProductType = productType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
