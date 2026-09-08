package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.repository.ProductRepo;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SellerService {
    @Autowired
    SellerRepo sellerRepo;
    @Autowired
    ProductRepo productRepo;

    public List<Seller> getSellers() {
        return sellerRepo.findByDeletedFalse();
    }

    public Seller getSellerById(int id) {
        Seller seller = sellerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        if (seller.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found");
        }
        return seller;
    }

    public void updateSeller(Seller seller) {
        Seller existing = getSellerById(seller.getId());
        seller.setDeleted(existing.isDeleted());
        sellerRepo.save(seller);
    }

    @Transactional
    public void deleteSeller(int id) {
        Seller seller = getSellerById(id);
        seller.setDeleted(true);
        sellerRepo.save(seller);
        for (Product product : productRepo.findBySeller(seller)) {
            product.setDeleted(true);
            productRepo.save(product);
        }
    }
}
