package com.slow3586.micromarket.api.stock;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class StockUpdateOrderDto implements Serializable {
    UUID id;
    UUID productId;
    UUID orderId;
    int value;
    Instant createdAt;
}