package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Driver;
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
        return driverRepo.findByDeletedFalse();
    }

    public Driver getDriverById(int id) {
        Driver driver = driverRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
        if (driver.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found");
        }
        return driver;
    }

    public void updateDriver(Driver driver) {
        Driver existing = getDriverById(driver.getId());
        driver.setDeleted(existing.isDeleted());
        driverRepo.save(driver);
    }

    public void deleteDriver(int id) {
        Driver driver = getDriverById(id);
        driver.setDeleted(true);
        driverRepo.save(driver);
    }
}
