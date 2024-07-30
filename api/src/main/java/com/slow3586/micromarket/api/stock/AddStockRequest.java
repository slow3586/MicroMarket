package com.slow3586.micromarket.api.stock;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class AddStockRequest {
    UUID productId;
    int value;
}
