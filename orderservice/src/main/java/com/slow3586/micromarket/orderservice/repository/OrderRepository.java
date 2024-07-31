package com.slow3586.micromarket.orderservice.repository;

import com.slow3586.micromarket.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(value = """
        select o from order o\
        where (o.status = 'NEW' AND o.created_at + INTERVAL '1 minute' < NOW())\
                or (o.status = 'AWAITING_PAYMENT' AND o.created_at + INTERVAL '15 minutes' < NOW())\
                or (o.status = 'AWAITING_CONFIRMATION' AND o.created_at + INTERVAL '1 day' < NOW())\
                order by o.created_at limit 100)""", nativeQuery = true)
    List<Order> findBadOrders();
}
