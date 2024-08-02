package com.slow3586.micromarket.stockservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockChangeRepository extends JpaRepository<StockChange, UUID> {
    @Query("""
        select coalesce(sum(s.value), 0) from stock_change s
        """)
    long sumAllByProductId(UUID productId);

    List<StockChange> findAllByProductId(UUID productId);
    Optional<StockChange> findByOrderId(UUID orderId);
}
