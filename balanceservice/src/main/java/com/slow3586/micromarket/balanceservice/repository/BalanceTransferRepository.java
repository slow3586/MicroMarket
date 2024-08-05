package com.slow3586.micromarket.balanceservice.repository;

import com.slow3586.micromarket.balanceservice.entity.BalanceTransfer;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BalanceTransferRepository extends JpaRepository<BalanceTransfer, UUID> {
    @Query("""
        select coalesce(sum(b.value), 0) from balance_transfer b
        where b.receiverId = :userId
        and b.status = 'COMPLETED'
        """)
    long sumAllPositiveByUserId(@NotNull UUID userId);
    @Query("""
        select coalesce(sum(b.value), 0) from balance_transfer b
        where b.senderId = :userId
        and (b.status = 'RESERVED' or b.status = 'COMPLETED')
        """)
    long sumAllNegativeByUserId(@NotNull UUID userId);
    @Query("""
        select b from balance_transfer b
        where b.status = 'AWAITING'
        and b.senderId = :userId
        """)
    List<BalanceTransfer> findAllAwaitingByUserId(@NotNull UUID userId);

    List<BalanceTransfer> findAllByOrderId(UUID orderId);

    @Query("""
        select b from balance_transfer b
        where b.senderId = :userId or b.receiverId = :userId
        """)
    List<BalanceTransfer> findAllByUserId(UUID userId);

    boolean existsByOrderId(UUID id);
}
