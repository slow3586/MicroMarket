package com.slow3586.micromarket.api.product;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ProductQuery {
    UUID sellerId;
    String name;
    long priceMin;
    long priceMax;
    Instant startDate;
    Instant endDate;
}
