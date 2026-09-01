package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Customer extends User{
    private String address;
    private String phone;

    public Customer() {
    }

    public Customer(String address, String phone) {
        this.address = address;
        this.phone = phone;
    }

}
