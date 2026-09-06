package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int quantity;
    private double priceAtPurchase;
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItem() {
    }

    public OrderItem(int id, int quantity, double priceAtPurchase, Order order, Product product) {
        this.id = id;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.order = order;
        this.product = product;
    }
}
