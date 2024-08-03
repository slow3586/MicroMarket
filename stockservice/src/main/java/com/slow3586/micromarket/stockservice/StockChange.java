package com.slow3586.micromarket.stockservice;

import com.slow3586.micromarket.api.audit.AuditEntityListener;
import com.slow3586.micromarket.api.spring.DefaultEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity(name = "stock_change")
@EntityListeners(AuditEntityListener.class)
public class StockChange extends DefaultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    @NotNull
    UUID productId;
    UUID orderId;
    @NotNull
    int value;
    @NotBlank
    String status;
}
