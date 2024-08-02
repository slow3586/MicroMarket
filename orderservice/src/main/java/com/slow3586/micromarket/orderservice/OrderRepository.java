package com.slow3586.micromarket.orderservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(value = """
        select * from "order" o \
        where (o.status = 'INITIALIZATION_AWAITING' AND o.created_at + INTERVAL '1 minute' < NOW()) \
                or (o.status = 'PAYMENT_AWAITING' AND o.created_at + INTERVAL '15 minutes' < NOW()) \
                or (o.status = 'CONFIRMATION_AWAITING' AND o.created_at + INTERVAL '1 day' < NOW()) \
                order by o.created_at limit 100""",
        nativeQuery = true)
    List<Order> findBadOrders();
}
