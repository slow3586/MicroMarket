package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface OrderRepository extends JpaRepository<Order, UUID> {
}
