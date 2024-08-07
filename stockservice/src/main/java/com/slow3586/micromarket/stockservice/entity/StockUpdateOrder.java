package com.slow3586.micromarket.stockservice.entity;

import com.slow3586.micromarket.api.audit.AuditEntityListener;
import com.slow3586.micromarket.api.spring.DefaultEntity;
import com.slow3586.micromarket.api.stock.StockConfig;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity(name = "stock_update_order")
@EntityListeners(AuditEntityListener.class)
public class StockUpdateOrder extends DefaultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    @NotNull
    UUID productId;
    @NotNull
    @Column(unique = true)
    UUID orderId;
    @NotNull
    int value;
    @NotNull
    @Enumerated(EnumType.STRING)
    StockConfig.UpdateOrder.Status status;
    @CreationTimestamp
    Instant createdAt;
}
