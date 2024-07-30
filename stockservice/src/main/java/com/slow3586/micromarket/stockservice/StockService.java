package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.api.stock.AddStockRequest;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class StockService {
    StockChangeRepository stockChangeRepository;
    StockChangeMapper stockChangeMapper;

    public StockChangeDto addStock(AddStockRequest request) {
        final StockChange stockChange = stockChangeRepository.save(
            new StockChange()
                .setValue(request.getValue())
                .setProductId(request.getProductId()));

        return stockChangeMapper.toDto(stockChange);
    }

    public OrderTransaction processNewOrder(OrderTransaction order) {
        order.getOrderItemList().forEach(item -> {
            UUID productId = item.getProduct().getId();
            List<StockChange> balanceChangeList = stockChangeRepository.findAllByProductId(productId);
            int stock = balanceChangeList.stream()
                .mapToInt(StockChange::getValue)
                .sum();
            int total = order.getOrderItemList().stream()
                .mapToInt(OrderTransaction.OrderItemDto::getQuantity)
                .sum();

            if (stock < total) {
                throw new IllegalStateException("Not enough stock");
            }

            final StockChange stockChange = stockChangeRepository.save(new StockChange()
                .setProductId(productId)
                .setOrderId(order.getId())
                .setValue(-total));

            item.setStockChange(stockChangeMapper.toDto(stockChange));
        });

        return order;
    }

    public long getProductStock(UUID productId) {
        return stockChangeRepository.sumAllByProductId(productId);
    }
}
