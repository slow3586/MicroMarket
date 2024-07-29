package com.slow3586.micromarket.stockservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface StockChangeRepository extends JpaRepository<StockChange, UUID> {
    List<StockChange> findAllByProductId(UUID productId);
}
