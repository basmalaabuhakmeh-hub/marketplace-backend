package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerController {

    private final CustomerService cusService;

    public CustomerController(CustomerService cusService) {
        this.cusService = cusService;
    }

    @GetMapping("/customers")
    public List<Customer> getCustomers(){
        return cusService.getCustomers();
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable int id){
        return cusService.getCustomerById(id);
    }

    @PostMapping("/customers")
    public void addCustomers(@RequestBody Customer cus){
        cusService.addCustomers(cus);
    }

    @PutMapping("/customers")
    public void updateCustomer(@RequestBody Customer cus){
        cusService.updateCustomer(cus);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable int id){
        cusService.deleteCustomer(id);
    }
}
