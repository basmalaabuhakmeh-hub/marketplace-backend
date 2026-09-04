package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepo cusRepo;


    public List<Customer> getCustomers() {
        return cusRepo.findAll();
    }


    public Customer getCustomerById(int id) {
        return cusRepo.findById(id).orElse(new Customer(null, null));
    }


    public void addCustomer(Customer cus) {
        cusRepo.save(cus);
    }


    public void updateCustomer(Customer cus) {
        cusRepo.save(cus);
    }

    public void deleteCustomer(int id) {
        cusRepo.deleteById(id);
    }
}
