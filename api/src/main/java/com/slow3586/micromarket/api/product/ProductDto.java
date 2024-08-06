package com.slow3586.micromarket.api.product;

import com.slow3586.micromarket.api.user.UserDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class ProductDto {
    UUID id;
    UserDto seller;
    String name;
    int price;
    Instant createdAt;
}