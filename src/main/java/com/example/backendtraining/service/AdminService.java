package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.model.SellerStatus;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {

    private final SellerRepo sellerRepo;

    public AdminService(SellerRepo sellerRepo) {
        this.sellerRepo = sellerRepo;
    }

    public List<Seller> getPendingSellers() {
        return sellerRepo.findByStatus(SellerStatus.PENDING);
    }

    public Seller acceptSeller(int id) {
        return updateStatus(id, SellerStatus.ACCEPTED);
    }

    public Seller rejectSeller(int id) {
        return updateStatus(id, SellerStatus.REJECTED);
    }

    private Seller updateStatus(int id, SellerStatus status) {
        Seller seller = sellerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        seller.setStatus(status);
        return sellerRepo.save(seller);
    }
}
