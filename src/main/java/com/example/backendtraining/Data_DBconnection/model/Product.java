package com.example.backendtraining.Data_DBconnection.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NamedEntityGraph(name = "Product.withSeller", attributeNodes = @NamedAttributeNode("seller"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Enumerated(EnumType.STRING)
    private ProductType productType;
    private double price;
    private int stock;

    @JsonIgnore
    private boolean deleted = false;

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
