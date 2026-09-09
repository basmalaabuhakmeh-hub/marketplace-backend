package com.example.backendtraining.Data_DBconnection.repository;

import com.example.backendtraining.Data_DBconnection.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepo extends JpaRepository<OrderStatusHistory, Integer> {

    @EntityGraph(attributePaths = "changedBy")
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(int orderId);
}
