package com.slow3586.micromarket.balanceservice.repository;

import com.slow3586.micromarket.balanceservice.entity.BalanceUpdateOrder;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceUpdateOrderRepository extends JpaRepository<BalanceUpdateOrder, UUID> {
    @Query("""
        select coalesce(sum(b.value), 0) from balance_update_order b
        where b.receiverId = :userId
        and b.status = COMPLETED
        """)
    long sumAllPositiveByUserId(@NotNull UUID userId);

    @Query("""
        select coalesce(sum(b.value), 0) from balance_update_order b
        where b.senderId = :userId
        and (b.status = RESERVED or b.status = COMPLETED)
        """)
    long sumAllNegativeByUserId(@NotNull UUID userId);

    @Query("""
        select b from balance_update_order b
        where b.senderId = :userId
        and b.status = AWAITING
        """)
    List<BalanceUpdateOrder> findAllAwaitingByUserId(@NotNull UUID userId);

    Optional<BalanceUpdateOrder> findByOrderId(@NotNull UUID orderId);

    @Query("""
        select b from balance_update_order b
        where b.senderId = :userId or b.receiverId = :userId
        """)
    List<BalanceUpdateOrder> findAllByUserId(@NotNull UUID userId);

    boolean existsByOrderId(UUID id);
}
