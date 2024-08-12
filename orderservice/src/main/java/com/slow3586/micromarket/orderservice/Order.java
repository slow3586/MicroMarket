package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.audit.AuditEntityListener;
import com.slow3586.micromarket.api.order.OrderConfig;
import com.slow3586.micromarket.api.spring.DefaultEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity(name = "order_")
@Table(name = "order_",
    indexes = {
        @Index(columnList = "status, createdAt"),
        @Index(columnList = "id, status"),
        @Index(columnList = "buyerId"),
        @Index(columnList = "status"),
        @Index(columnList = "createdAt"),
    })
@EntityListeners(AuditEntityListener.class)
public class Order extends DefaultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    @NotNull
    UUID productId;
    @NotNull
    UUID buyerId;
    @Min(1)
    @Max(999999)
    int quantity;
    @NotNull
    @Enumerated(EnumType.STRING)
    OrderConfig.Status status;
    @Enumerated(EnumType.STRING)
    OrderConfig.Error error;

    @CreatedDate
    Instant createdAt;
    Instant activatedAt;
}
