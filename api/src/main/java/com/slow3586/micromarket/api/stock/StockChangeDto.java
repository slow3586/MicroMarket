package com.slow3586.micromarket.api.stock;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class StockChangeDto implements Serializable {
    UUID id;
    UUID productId;
    UUID orderId;
    int value;
}