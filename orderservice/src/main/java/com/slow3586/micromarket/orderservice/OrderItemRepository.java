package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
