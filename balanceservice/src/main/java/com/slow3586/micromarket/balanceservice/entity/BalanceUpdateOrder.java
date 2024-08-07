package com.slow3586.micromarket.balanceservice.entity;

import com.slow3586.micromarket.api.audit.AuditEntityListener;
import com.slow3586.micromarket.api.balance.BalanceConfig;
import com.slow3586.micromarket.api.spring.DefaultEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity(name = "balance_update_order")
@EntityListeners(AuditEntityListener.class)
public class BalanceUpdateOrder extends DefaultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    @NotNull
    @Column(unique = true)
    UUID orderId;
    @NotNull
    UUID senderId;
    @NotNull
    UUID receiverId;
    @Min(1)
    @Max(999999)
    int value;
    @NotNull
    @Enumerated(EnumType.STRING)
    BalanceConfig.BalanceUpdateOrder.Status status;
    @CreationTimestamp
    Instant createdAt;
}
