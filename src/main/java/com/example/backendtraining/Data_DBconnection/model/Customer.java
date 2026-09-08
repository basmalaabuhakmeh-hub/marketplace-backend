package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Setter
@Getter
@Entity
public class Customer extends User{
    private String address;
    private String phoneNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orders;
    public Customer() {
        this.setRole(Role.CUSTOMER);
    }

    public Customer(String address, String phoneNumber, List<Order> orders) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.orders = orders;
        this.setRole(Role.CUSTOMER);
    }

    public Customer(int id, String name, String email, String password, String address, String phoneNumber, List<Order> orders) {
        super(id, name, Role.CUSTOMER, email, password);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.orders = orders;
    }

    public Customer(String address, String phoneNumber) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.setRole(Role.CUSTOMER);
    }

}
