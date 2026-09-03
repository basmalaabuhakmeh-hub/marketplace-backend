package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Customer extends User{
    private String address;
    private String phone;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
    public Customer() {
        this.setRole(Role.CUSTOMER);
    }

    public Customer(String address, String phone, List<Order> orders) {
        this.address = address;
        this.phone = phone;
        this.orders = orders;
        this.setRole(Role.CUSTOMER);
    }

    public Customer(int id, String name, String email, String password, String address, String phone, List<Order> orders) {
        super(id, name, Role.CUSTOMER, email, password);
        this.address = address;
        this.phone = phone;
        this.orders = orders;
    }

    public Customer(String address, String phone) {
        this.address = address;
        this.phone = phone;
        this.setRole(Role.CUSTOMER);
    }

}
