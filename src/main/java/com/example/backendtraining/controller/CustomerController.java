package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Customer getCustomerById(@PathVariable int id){
        return cusService.getCustomerById(id);
    }

    @PostMapping("")
    public void addCustomers(@RequestBody Customer cus){
        cusService.addCustomers(cus);
    }

    @PutMapping("")
    public void updateCustomer(@RequestBody Customer cus){
        cusService.updateCustomer(cus);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable int id){
        cusService.deleteCustomer(id);
    }
}
