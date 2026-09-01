package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private ProductType ProductType;

    public Product() {
    }

    public Product(int id, String name, ProductType productType) {
        this.id = id;
        this.name = name;
        ProductType = productType;
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
}
