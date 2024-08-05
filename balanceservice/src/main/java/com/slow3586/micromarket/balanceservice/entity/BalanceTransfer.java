package com.slow3586.micromarket.balanceservice.entity;

import com.slow3586.micromarket.api.audit.AuditEntityListener;
import com.slow3586.micromarket.api.spring.DefaultEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
@Entity(name = "balance_transfer")
@EntityListeners(AuditEntityListener.class)
public class BalanceTransfer extends DefaultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    @NotNull
    UUID senderId;
    @NotNull
    UUID receiverId;
    @NotNull
    UUID orderId;
    @Min(1)
    @Max(999999)
    int value;
    @NotBlank
    String status;
    @NotNull
    @CreationTimestamp
    Instant createdAt;
}
