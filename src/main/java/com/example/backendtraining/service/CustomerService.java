package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepo cusRepo;

    public List<Customer> getCustomers() {
        return cusRepo.findAll();
    }

    public Customer getCustomerById(int id) {
        return cusRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    public void updateCustomer(Customer cus) {
        getCustomerById(cus.getId());
        cusRepo.save(cus);
    }

    public void deleteCustomer(int id) {
        getCustomerById(id);
        cusRepo.deleteById(id);
    }
}
