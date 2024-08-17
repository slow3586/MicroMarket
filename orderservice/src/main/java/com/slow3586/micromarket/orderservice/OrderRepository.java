package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.order.OrderConfig;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByStatusAndCreatedAtBeforeOrderByCreatedAt(
        @NotNull OrderConfig.Status status,
        @NotNull Instant createdAt);

    Optional<Order> findByIdAndStatus(UUID id, OrderConfig.Status status);

    Optional<Order> findByBuyerIdAndProductIdAndStatus(
        UUID principalId,
        UUID productId,
        OrderConfig.Status status);
}
