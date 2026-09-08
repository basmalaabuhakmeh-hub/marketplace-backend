package com.example.backendtraining.Data_DBconnection.repository;

import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    List<Product> findBySeller(Seller seller);
    List<Product> findBySellerId(int sellerId);

    // Pessimistic lock: other transactions wait until this one commits (used in addOrder).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") int id);

    @EntityGraph("Product.withSeller")
    Page<Product> findBySellerAndDeletedFalse(Seller seller, Pageable pageable);

    @EntityGraph("Product.withSeller")
    @Query(value = "SELECT p FROM Product p WHERE p.deleted = false",
            countQuery = "SELECT COUNT(p) FROM Product p WHERE p.deleted = false")
    Page<Product> findAllWithSeller(Pageable pageable);

    @EntityGraph("Product.withSeller")
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithSeller(@Param("id") int id);
}
