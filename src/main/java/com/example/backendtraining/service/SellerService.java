package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerService {
    @Autowired
    SellerRepo sellerRepo;


    public List<Seller> getSellers() {
        return sellerRepo.findAll();
    }


    public Seller getSellerById(int id) {
        return sellerRepo.findById(id).orElse(new Seller(null, null));
    }


    public void addSeller(Seller seller) {
        sellerRepo.save(seller);
    }


    public void updateSeller(Seller seller) {
        sellerRepo.save(seller);
    }

    public void deleteSeller(int id) {
        sellerRepo.deleteById(id);
    }
}
