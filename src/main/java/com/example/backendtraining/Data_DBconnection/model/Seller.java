package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Setter
@Getter
@Entity
public class Seller extends User {

    @JsonIgnore
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private List<Product> products;
    @Enumerated(EnumType.STRING)
    private SellerStatus status = SellerStatus.PENDING;
    private String businessName;

    public Seller() {
        this.setRole(Role.SELLER);
    }


    public Seller(String businessName, List<Product> products) {
        this.businessName = businessName;
        this.products = products;
        this.setRole(Role.SELLER);
    }

    public Seller(int id, String name, String email, String password, String businessName, List<Product> products) {
        super(id, name, Role.SELLER, email, password);
        this.businessName = businessName;
        this.products = products;
    }
}
