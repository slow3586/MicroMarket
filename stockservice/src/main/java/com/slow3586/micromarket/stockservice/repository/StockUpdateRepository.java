package com.slow3586.micromarket.stockservice.repository;

import com.slow3586.micromarket.stockservice.entity.StockUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StockUpdateRepository extends JpaRepository<StockUpdate, UUID> {
    @Query("""
        select coalesce(sum(s.value), 0) from stock_update s
        """)
    long sumAllByProductId(UUID productId);
}
