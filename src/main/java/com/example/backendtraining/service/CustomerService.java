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
        return cusRepo.findByDeletedFalse();
    }

    public Customer getCustomerById(int id) {
        Customer customer = cusRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        if (customer.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return customer;
    }

    public void updateCustomer(Customer cus) {
        Customer existing = getCustomerById(cus.getId());
        cus.setDeleted(existing.isDeleted());
        cusRepo.save(cus);
    }

    public void deleteCustomer(int id) {
        Customer customer = getCustomerById(id);
        customer.setDeleted(true);
        cusRepo.save(customer);
    }
}
