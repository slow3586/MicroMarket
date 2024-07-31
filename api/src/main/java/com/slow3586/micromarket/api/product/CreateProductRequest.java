package com.slow3586.micromarket.api.product;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateProductRequest {
    String name;
    int price;
}
