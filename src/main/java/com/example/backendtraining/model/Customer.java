package com.example.backendtraining.model;

import jakarta.persistence.Entity;

@Entity
public class Customer extends User{
    private String address;
    private int phone;
}
