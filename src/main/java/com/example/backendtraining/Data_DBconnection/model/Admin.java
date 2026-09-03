package com.example.backendtraining.Data_DBconnection.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Admin extends User{
    public Admin() {
        this.setRole(Role.ADMIN);
    }

    public Admin(int id, String name, String email, String password) {
        super(id, name, Role.ADMIN, email, password);
    }
}
