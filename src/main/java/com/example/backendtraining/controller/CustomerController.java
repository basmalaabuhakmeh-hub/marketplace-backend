package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.service.CustomerService;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/customers")
@RestController
public class CustomerController {

    private final CustomerService cusService;

    public CustomerController(CustomerService cusService) {
        this.cusService = cusService;
    }

    @GetMapping("")
    public List<Customer> getCustomers(){
        return cusService.getCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable @Min(value = 1, message = "Customer id must be greater than 0") int id){
        return cusService.getCustomerById(id);
    }

    @PutMapping("")
    public void updateCustomer(@RequestBody Customer cus){
        cusService.updateCustomer(cus);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable @Min(value = 1, message = "Customer id must be greater than 0") int id){
        cusService.deleteCustomer(id);
    }
}
