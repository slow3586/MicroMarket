package com.slow3586.micromarket.api.order;

import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.StockChangeDto;
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
    OrderTopics.Status status;
    String error;
    Instant createdAt;

    StockChangeDto stockChange;
    BalanceTransferDto balanceTransfer;
    DeliveryDto delivery;
}