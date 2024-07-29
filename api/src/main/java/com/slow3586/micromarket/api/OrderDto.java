package com.slow3586.micromarket.api;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class OrderDto implements Serializable {
    UUID id;
    UserDto buyer;
    UserDto seller;
    String status;
    List<OrderItemDto> orderItemList;
    String error;

    @Data
    @Accessors(chain = true)
    public static class OrderItemDto implements Serializable {
        UUID id;
        ProductDto product;
        int quantity;
    }
}