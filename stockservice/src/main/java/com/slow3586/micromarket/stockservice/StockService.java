package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import com.slow3586.micromarket.api.stock.UpdateStockRequest;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public StockChangeDto updateStock(UpdateStockRequest request) {
        final UUID userId = SecurityUtils.getPrincipalId();
        final ProductDto productById = productClient.getProduct(request.getProductId());
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

    @KafkaListener(topics = OrderTopics.Transaction.USER,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processNewOrder(OrderDto order) {
        final UUID productId = order.getProduct().getId();
        final List<StockChange> balanceChangeList =
            stockChangeRepository.findAllByProductId(productId);
        final int stock = balanceChangeList.stream()
            .mapToInt(StockChange::getValue)
            .sum();

        if (stock < order.getQuantity()) {
            throw new IllegalStateException("Not enough stock");
        }

        final StockChange stockChange = stockChangeRepository.save(new StockChange()
            .setProductId(productId)
            .setOrderId(order.getId())
            .setValue(-order.getQuantity()));

        kafkaTemplate.send(
            OrderTopics.Transaction.STOCK,
            order.getId(),
            order.setStockChange(stockChangeMapper.toDto(stockChange)));
    }

    public long getProductStock(UUID productId) {
        return stockChangeRepository.sumAllByProductId(productId);
    }
}
