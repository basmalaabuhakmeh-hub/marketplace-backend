package com.example.backendtraining.Data_DBconnection.repository;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.Data_DBconnection.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findByCustomer(Customer customer);
    List<Order> findByCustomerId(int customerId);
    List<Order> findByDriver(Driver driver);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems i WHERE i.product.seller.id = :sellerId")
    List<Order> findBySellerId(@Param("sellerId") int sellerId);
}
