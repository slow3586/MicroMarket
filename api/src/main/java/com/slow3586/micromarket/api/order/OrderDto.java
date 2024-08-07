package com.slow3586.micromarket.api.order;

import com.slow3586.micromarket.api.balance.BalanceUpdateOrderDto;
import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.StockUpdateOrderDto;
import com.slow3586.micromarket.api.user.UserDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class OrderDto implements Serializable {
    UUID id;
    UserDto buyer;
    ProductDto product;
    int quantity;
    OrderConfig.Status status;
    String error;
    Instant createdAt;

    StockUpdateOrderDto stockChange;
    BalanceUpdateOrderDto balanceTransfer;
    DeliveryDto delivery;
}