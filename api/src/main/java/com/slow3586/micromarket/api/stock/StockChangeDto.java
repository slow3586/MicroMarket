package com.slow3586.micromarket.api.stock;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Value
public class StockChangeDto implements Serializable {
    UUID id;
    UUID productId;
    UUID orderId;
    int value;
}