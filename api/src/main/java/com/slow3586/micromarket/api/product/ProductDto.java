package com.slow3586.micromarket.api.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class ProductDto implements Serializable {
    UUID id;
    UUID sellerId;
    String name;
    int price;
}