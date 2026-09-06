package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.Data_DBconnection.model.DriverStatus;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.model.SellerStatus;
import com.example.backendtraining.Data_DBconnection.repository.DriverRepo;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {

    private final SellerRepo sellerRepo;
    private final DriverRepo driverRepo;

    public AdminService(SellerRepo sellerRepo, DriverRepo driverRepo) {
        this.sellerRepo = sellerRepo;
        this.driverRepo = driverRepo;
    }

    public List<Seller> getPendingSellers() {
        return sellerRepo.findByStatus(SellerStatus.PENDING);
    }

    public Seller acceptSeller(int id) {
        return updateStatus(id, SellerStatus.ACCEPTED);
    }

    public Seller rejectSeller(int id) {
        return updateStatus(id, SellerStatus.REJECTED);
    }

    private Seller updateStatus(int id, SellerStatus status) {
        Seller seller = sellerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        seller.setStatus(status);
        return sellerRepo.save(seller);
    }

    public List<Driver> getPendingDrivers() {
        return driverRepo.findByStatus(DriverStatus.PENDING);
    }

    public Driver acceptDriver(int id) {
        return updateDriverStatus(id, DriverStatus.ACCEPTED);
    }

    public Driver rejectDriver(int id) {
        return updateDriverStatus(id, DriverStatus.REJECTED);
    }

    private Driver updateDriverStatus(int id, DriverStatus status) {
        Driver driver = driverRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
        driver.setStatus(status);
        return driverRepo.save(driver);
    }
}
