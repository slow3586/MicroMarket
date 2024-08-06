package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.StockTopics;
import com.slow3586.micromarket.api.stock.StockUpdateDto;
import com.slow3586.micromarket.api.stock.StockUpdateOrderDto;
import com.slow3586.micromarket.api.stock.StockUpdateRequest;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import com.slow3586.micromarket.stockservice.entity.StockUpdate;
import com.slow3586.micromarket.stockservice.entity.StockUpdateOrder;
import com.slow3586.micromarket.stockservice.mapper.StockUpdateMapper;
import com.slow3586.micromarket.stockservice.mapper.StockUpdateOrderMapper;
import com.slow3586.micromarket.stockservice.repository.StockUpdateOrderRepository;
import com.slow3586.micromarket.stockservice.repository.StockUpdateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class StockService {
    StockUpdateRepository stockUpdateRepository;
    StockUpdateMapper stockUpdateMapper;
    StockUpdateOrderRepository stockUpdateOrderRepository;
    StockUpdateOrderMapper stockUpdateOrderMapper;
    ProductClient productClient;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public StockUpdateDto updateStock(StockUpdateRequest request) {
        final UUID userId = SecurityUtils.getPrincipalId();
        final ProductDto product = productClient.getProductById(request.getProductId());
        if (!userId.equals(product.getSeller().getId())) {
            throw new AccessDeniedException("У пользователя нет доступа к этому товару");
        }

        final StockUpdate entity = stockUpdateRepository.save(
            new StockUpdate()
                .setValue(request.getValue())
                .setProductId(request.getProductId()));

        return stockUpdateMapper.toDto(entity);
    }

    @KafkaListener(topics = OrderTopics.Initialization.USER,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderCreated(OrderDto order) {
        if (stockUpdateOrderRepository.existsByOrderId(order.getId())) {
            return;
        }

        final UUID productId = order.getProduct().getId();
        final long stock = this.getStockSumByProductId(productId);

        if (stock < order.getQuantity()) {
            throw new IllegalStateException("Недостаточно товара");
        }

        final StockUpdateOrderDto stockChange = this.saveStock(
            new StockUpdateOrder()
                .setStatus("ORDER_RESERVED")
                .setProductId(productId)
                .setOrderId(order.getId())
                .setValue(-order.getQuantity()));

        kafkaTemplate.send(
            OrderTopics.Initialization.STOCK,
            order.getId(),
            order.setStockChange(stockChange));
    }

    @KafkaListener(topics = OrderTopics.ERROR)
    public void processOrderError(OrderDto order) {
        stockUpdateOrderRepository.findByOrderId(order.getId())
            .stream()
            .filter(stockUpdateOrder -> "ORDER_RESERVED".equals(stockUpdateOrder.getStatus()))
            .forEach(stockUpdateOrder -> stockUpdateOrder.setStatus("ORDER_CANCELLED"));
    }

    @KafkaListener(topics = OrderTopics.Delivery.SENT)
    public void processOrderDeliverySent(OrderDto order) {
        stockUpdateOrderRepository.findByOrderId(order.getId())
            .stream()
            .filter(stockUpdateOrder -> "ORDER_RESERVED".equals(stockUpdateOrder.getStatus()))
            .forEach(stockUpdateOrder -> stockUpdateOrder.setStatus("ORDER_SENT"));
    }

    protected StockUpdateOrderDto saveStock(StockUpdateOrder stockUpdateOrder) {
        final StockUpdateOrder stockUpdateOrderSaved =
            stockUpdateOrderRepository.save(stockUpdateOrder);

        final StockUpdateOrderDto stockUpdateOrderDto = stockUpdateOrderMapper.toDto(stockUpdateOrderSaved);

        kafkaTemplate.send(
            StockTopics.TABLE,
            stockUpdateOrderSaved.getId(),
            stockUpdateOrderDto);

        return stockUpdateOrderDto;
    }

    @Cacheable(value = "getStockSumByProductId", key = "#productId")
    public long getStockSumByProductId(UUID productId) {
        return stockUpdateRepository.sumAllByProductId(productId)
            - stockUpdateOrderRepository.sumAllByProductId(productId);
    }

    public StockUpdateOrderDto getStockOrderChangeByOrderId(UUID orderId) {
        return stockUpdateOrderRepository.findByOrderId(orderId)
            .map(stockUpdateOrderMapper::toDto)
            .orElseThrow();
    }
}
