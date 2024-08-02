package com.slow3586.micromarket.balanceservice.repository;

import com.slow3586.micromarket.balanceservice.entity.BalanceReplenish;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BalanceReplenishRepository extends JpaRepository<BalanceReplenish, UUID> {
    @Query("""
        select coalesce(sum(b.value), 0) from balance_replenish b
        """)
    long sumAllByUserId(@NotNull UUID userId);
}
