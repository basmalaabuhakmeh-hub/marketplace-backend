package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue
    private int id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Enumerated(EnumType.STRING)
    private ProductType productType;
    private double price;
    private int stock;

    public Product() {
    }

    public Product(int id, String name, Seller seller, ProductType productType, double price) {
        this.id = id;
        this.name = name;
        this.seller = seller;
        this.productType = productType;
        this.price = price;
    }
}
