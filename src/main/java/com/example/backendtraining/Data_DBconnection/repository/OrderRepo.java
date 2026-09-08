package com.example.backendtraining.Data_DBconnection.repository;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.Data_DBconnection.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {

    @EntityGraph("Order.details")
    @Query("SELECT DISTINCT o FROM Order o")
    List<Order> findAllWithDetails();

    @EntityGraph("Order.details")
    @Query("SELECT DISTINCT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") int id);

    @EntityGraph("Order.details")
    @Query("SELECT DISTINCT o FROM Order o WHERE o.customer = :customer")
    List<Order> findByCustomer(@Param("customer") Customer customer);

    List<Order> findByCustomerId(int customerId);

    @EntityGraph("Order.details")
    @Query("SELECT DISTINCT o FROM Order o WHERE o.driver = :driver")
    List<Order> findByDriver(@Param("driver") Driver driver);

    // Subquery so JOIN FETCH of orderItems is not filtered to one seller's lines.
    @EntityGraph("Order.details")
    @Query("SELECT DISTINCT o FROM Order o WHERE o.id IN (SELECT i.order.id FROM OrderItem i WHERE i.product.seller.id = :sellerId)")
    List<Order> findBySellerId(@Param("sellerId") int sellerId);
}
