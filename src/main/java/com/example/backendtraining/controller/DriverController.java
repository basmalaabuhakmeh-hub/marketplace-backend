package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.service.CustomerService;
import com.example.backendtraining.service.DriverService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/drivers")
@RestController
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("")
    public List<Driver> getDrivers(){
        return driverService.getDrivers();
    }

    @GetMapping("/{id}")
    public Driver getDriverById(@PathVariable int id){
        return driverService.getDriverById(id);
    }

    @PutMapping("")
    public void updateDriver(@RequestBody Driver driver){
        driverService.updateDriver(driver);
    }

    @DeleteMapping("/{id}")
    public void deleteDriver(@PathVariable int id){
        driverService.deleteDriver(id);
    }
}
