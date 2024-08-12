package com.slow3586.micromarket.deliveryservice;

import com.slow3586.micromarket.api.audit.AuditEntityListener;
import com.slow3586.micromarket.api.delivery.DeliveryConfig;
import com.slow3586.micromarket.api.spring.DefaultEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
@Entity(name = "delivery")
@Table(indexes = {
    @Index(columnList = "orderId"),
    @Index(columnList = "status"),
    @Index(columnList = "createdAt"),
    @Index(columnList = "sentAt"),
    @Index(columnList = "receivedAt"),
})
@EntityListeners(AuditEntityListener.class)
public class Delivery extends DefaultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    @NotNull
    @Column(unique = true)
    UUID orderId;
    @NotNull
    @Enumerated(EnumType.STRING)
    DeliveryConfig.Status status;
    @CreatedDate
    Instant createdAt;
    Instant sentAt;
    Instant receivedAt;
}
