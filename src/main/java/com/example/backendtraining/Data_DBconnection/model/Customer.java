package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.Entity;

@Entity
public class Customer extends User{
    private String address;
    private int phone;
}
