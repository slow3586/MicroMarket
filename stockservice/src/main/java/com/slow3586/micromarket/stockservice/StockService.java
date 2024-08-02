package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import com.slow3586.micromarket.api.stock.StockTopics;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class StockService {
    StockChangeRepository stockChangeRepository;
    StockChangeMapper stockChangeMapper;
    ProductClient productClient;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public StockChangeDto updateStock(UpdateStockRequest request) {
        final UUID userId = SecurityUtils.getPrincipalId();
        final ProductDto product = productClient.getProduct(request.getProductId());
        if (!userId.equals(product.getSeller().getId())) {
            throw new AccessDeniedException("У пользователя нет доступа к этому товару");
        }

        return this.saveStock(
            new StockChange()
                .setStatus("ORDER_RESERVED")
                .setValue(request.getValue())
                .setProductId(request.getProductId()));
    }

    @KafkaListener(topics = OrderTopics.Transaction.USER,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderCreated(OrderDto order) {
        final UUID productId = order.getProduct().getId();
        final List<StockChange> balanceChangeList =
            stockChangeRepository.findAllByProductId(productId);
        final int stock = balanceChangeList.stream()
            .mapToInt(StockChange::getValue)
            .sum();

        if (stock < order.getQuantity()) {
            throw new IllegalStateException("Not enough stock");
        }

        final StockChangeDto stockChange = this.saveStock(
            new StockChange()
                .setStatus("ORDER_RESERVED")
                .setProductId(productId)
                .setOrderId(order.getId())
                .setValue(-order.getQuantity()));

        kafkaTemplate.send(
            OrderTopics.Transaction.STOCK,
            order.getId(),
            order.setStockChange(stockChange));
    }

    @KafkaListener(topics = {OrderTopics.Transaction.ERROR},
        errorHandler = "loggingKafkaListenerErrorHandler")
    public void processOrderError(OrderDto order) {
        stockChangeRepository.findAllByOrderId(order.getId())
            .stream()
            .filter(stockChange -> "ORDER_RESERVED".equals(stockChange.getStatus()))
            .forEach(stockChange -> stockChange.setStatus("ORDER_CANCELLED"));
    }

    @KafkaListener(topics = {OrderTopics.Transaction.Delivery.SENT},
        errorHandler = "loggingKafkaListenerErrorHandler")
    public void processOrderDeliverySent(OrderDto order) {
        stockChangeRepository.findAllByOrderId(order.getId())
            .stream()
            .filter(stockChange -> "ORDER_RESERVED".equals(stockChange.getStatus()))
            .forEach(stockChange -> stockChange.setStatus("ORDER_SENT"));
    }

    protected StockChangeDto saveStock(StockChange stockChange) {
        final StockChange stockChangeSaved =
            stockChangeRepository.save(stockChange);

        final StockChangeDto stockChangeDto = stockChangeMapper.toDto(stockChangeSaved);

        kafkaTemplate.send(
            StockTopics.TABLE,
            stockChangeSaved.getId(),
            stockChangeDto);

        return stockChangeDto;
    }

    public long getProductStock(UUID productId) {
        return stockChangeRepository.sumAllByProductId(productId);
    }
}
