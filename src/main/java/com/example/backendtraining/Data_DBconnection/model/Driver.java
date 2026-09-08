package com.example.backendtraining.Data_DBconnection.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(indexes = @Index(name = "idx_driver_status", columnList = "status"))
public class Driver extends User{
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.PENDING;

    @JsonIgnore
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Order> orders;

    public Driver() {
        this.setRole(Role.DRIVER);
    }

    public Driver(int id, String name, Role role, String email, String password, String phoneNumber, List<Order> orders) {
        super(id, name, role, email, password);
        this.phoneNumber = phoneNumber;
        this.orders = orders;
        this.setRole(Role.DRIVER);
    }
}
