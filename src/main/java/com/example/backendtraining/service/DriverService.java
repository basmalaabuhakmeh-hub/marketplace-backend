package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import com.example.backendtraining.Data_DBconnection.repository.DriverRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DriverService {
    @Autowired
    DriverRepo driverRepo;

    public List<Driver> getDrivers() {
        return driverRepo.findAll();
    }

    public Driver getDriverById(int id) {
        return driverRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
    }

    public void updateDriver(Driver driver) {
        getDriverById(driver.getId());
        driverRepo.save(driver);
    }

    public void deleteDriver(int id) {
        getDriverById(id);
        driverRepo.deleteById(id);
    }
}
