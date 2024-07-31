package com.slow3586.micromarket.api.order;

import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import com.slow3586.micromarket.api.user.UserDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class OrderTransaction implements Serializable {
    UUID id;
    UserDto buyer;
    String status;
    String error;
    List<OrderItemDto> orderItemList;

    @Data
    @Accessors(chain = true)
    public static class OrderItemDto implements Serializable {
        UUID id;
        ProductDto product;
        StockChangeDto stockChange;
        UserDto seller;
        BalanceTransferDto balanceTransferDto;
        int quantity;
    }
}