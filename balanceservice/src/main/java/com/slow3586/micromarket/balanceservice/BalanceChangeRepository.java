package com.slow3586.micromarket.balanceservice;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface BalanceChangeRepository extends JpaRepository<BalanceChange, UUID> {
    List<BalanceChange> findAllByUserId(@NotNull UUID userId);
}
