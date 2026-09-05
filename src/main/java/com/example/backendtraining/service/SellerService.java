package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SellerService {
    @Autowired
    SellerRepo sellerRepo;

    public List<Seller> getSellers() {
        return sellerRepo.findAll();
    }

    public Seller getSellerById(int id) {
        return sellerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
    }

    public void updateSeller(Seller seller) {
        getSellerById(seller.getId());
        sellerRepo.save(seller);
    }

    public void deleteSeller(int id) {
        getSellerById(id);
        sellerRepo.deleteById(id);
    }
}
