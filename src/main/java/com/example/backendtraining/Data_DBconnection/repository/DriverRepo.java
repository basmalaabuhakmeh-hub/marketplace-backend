package com.example.backendtraining.Data_DBconnection.repository;

import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.Data_DBconnection.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepo extends JpaRepository<Driver, Integer> {
    Optional<Driver> findByEmail(String email);
    List<Driver> findByStatus(DriverStatus status);
    List<Driver> findByStatusAndDeletedFalse(DriverStatus status);
    List<Driver> findByDeletedFalse();
}
