package com.example.backendtraining.Data_DBconnection.repository;

import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.model.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer> {
    List<Seller> findByStatus(SellerStatus status);
    List<Seller> findByStatusAndDeletedFalse(SellerStatus status);
    List<Seller> findByDeletedFalse();
    Optional<Seller> findByEmail(String email);
}
