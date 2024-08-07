package com.slow3586.micromarket.balanceservice.repository;

import com.slow3586.micromarket.balanceservice.entity.BalanceUpdate;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BalanceUpdateRepository extends JpaRepository<BalanceUpdate, UUID> {
    @Query("""
        select coalesce(sum(b.value), 0) from balance_update b
        where b.userId = :userId
        """)
    long sumAllByUserId(@NotNull UUID userId);

    List<BalanceUpdate> findAllByUserId(@NotNull UUID userId);
}
