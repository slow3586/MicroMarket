package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.UpdateStockRequest;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
    ProductClient productClient;

    public StockChangeDto updateStock(UpdateStockRequest request) {
        final UUID userId = SecurityUtils.getPrincipalId();
        final ProductDto productById = productClient.findProductById(request.getProductId());
        if (!userId.equals(productById.getSellerId())) {
            throw new AccessDeniedException("У пользователя нет доступа к этому товару");
        }

        final StockChange stockChange =
            stockChangeRepository.save(
                new StockChange()
                    .setValue(request.getValue())
                    .setProductId(request.getProductId()));

        return stockChangeMapper.toDto(stockChange);
    }

    public OrderTransaction processNewOrder(OrderTransaction order) {
        order.setOrderItemList(order.getOrderItemList()
            .stream()
            .map(item -> {
                final UUID productId = item.getProduct().getId();
                final List<StockChange> balanceChangeList =
                    stockChangeRepository.findAllByProductId(productId);
                final int stock = balanceChangeList.stream()
                    .mapToInt(StockChange::getValue)
                    .sum();

                if (stock < item.getQuantity()) {
                    throw new IllegalStateException("Not enough stock");
                }

                final StockChange stockChange = stockChangeRepository.save(new StockChange()
                    .setProductId(productId)
                    .setOrderId(order.getId())
                    .setValue(-item.getQuantity()));

                return item.setStockChange(stockChangeMapper.toDto(stockChange));
            }).toList());

        return order;
    }

    public long getProductStock(UUID productId) {
        return stockChangeRepository.sumAllByProductId(productId);
    }
}
