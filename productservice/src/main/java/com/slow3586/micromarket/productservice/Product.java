package com.slow3586.micromarket.productservice;

import com.slow3586.micromarket.api.audit.AuditEntityListener;
import com.slow3586.micromarket.api.spring.DefaultEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity(name = "product")
@EntityListeners(AuditEntityListener.class)
public class Product extends DefaultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    UUID sellerId;
    String name;
    int price;
}
