package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.order.OrderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(value = """
        select * from "order" o \
        where (o.status = 'AWAITING_PAYMENT' AND o.created_at + INTERVAL '10 minute' < NOW())
        order by o.created_at limit 100""",
        nativeQuery = true)
    List<Order> queryForAwaitingBalanceTimeoutCheck();
    @Query(value = """
        select * from "order" o \
        where (o.status = 'AWAITING_DELIVERY' AND o.created_at + INTERVAL '1 hour' < NOW())
        order by o.created_at limit 100""",
        nativeQuery = true)
    List<Order> queryForAwaitingDeliveryTimeoutCheck();
    Optional<Order> findByIdAndStatus(UUID id, OrderConfig.Status status);
}
