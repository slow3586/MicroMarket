package com.slow3586.micromarket.stockservice.repository;

import com.slow3586.micromarket.api.stock.StockConfig;
import com.slow3586.micromarket.stockservice.entity.StockUpdateOrder;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockUpdateOrderRepository extends JpaRepository<StockUpdateOrder, UUID> {
    @Query("""
        select coalesce(sum(s.value), 0) from stock_update_order s
        where s.productId = :productId
        """)
    long sumAllByProductId(UUID productId);

    Optional<StockUpdateOrder> findByOrderId(UUID orderId);
    Optional<StockUpdateOrder> findByOrderIdAndStatus(@NotNull UUID orderId, @NotNull StockConfig.StockUpdateOrder.Status status);

    boolean existsByOrderId(UUID id);
}
