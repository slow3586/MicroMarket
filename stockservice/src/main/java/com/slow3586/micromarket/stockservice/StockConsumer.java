package com.slow3586.micromarket.stockservice;

import com.slow3586.micromarket.api.delivery.DeliveryConfig;
import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.order.OrderConfig;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.stock.StockConfig;
import com.slow3586.micromarket.stockservice.entity.StockUpdateOrder;
import com.slow3586.micromarket.stockservice.mapper.StockUpdateMapper;
import com.slow3586.micromarket.stockservice.mapper.StockUpdateOrderMapper;
import com.slow3586.micromarket.stockservice.repository.StockUpdateOrderRepository;
import com.slow3586.micromarket.stockservice.repository.StockUpdateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class StockConsumer {
    StockUpdateRepository stockUpdateRepository;
    StockUpdateMapper stockUpdateMapper;
    StockUpdateOrderRepository stockUpdateOrderRepository;
    StockUpdateOrderMapper stockUpdateOrderMapper;
    StockService stockService;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderConfig.TOPIC)
    protected void processOrder(final OrderDto order) {
        if (OrderConfig.Status.CREATED.equals(order.getStatus())) {
            this.processOrderCreated(order);
        } else if (OrderConfig.Status.CANCELLED.equals(order.getStatus())) {
            this.processOrderError(order);
        }
    }

    protected void processOrderCreated(final OrderDto order) {
        if (stockUpdateOrderRepository.existsByOrderId(order.getId())) {
            return;
        }

        final long stock = stockService.getStockSumByProductId(order.getProduct().getId());

        if (stock < order.getQuantity()) {
            throw new IllegalStateException("Недостаточно товара");
        }

        final StockUpdateOrder stockUpdateOrder =
            this.stockUpdateOrderRepository.save(
                new StockUpdateOrder()
                    .setStatus(StockConfig.UpdateOrder.Status.RESERVED)
                    .setProductId(order.getProduct().getId())
                    .setOrderId(order.getId())
                    .setValue(order.getQuantity()));

        kafkaTemplate.send(
            StockConfig.UpdateOrder.TOPIC,
            stockUpdateOrder.getId(),
            stockUpdateOrderMapper.toDto(stockUpdateOrder));
    }

    protected void processOrderError(OrderDto order) {
        stockUpdateOrderRepository.findByOrderIdAndStatus(
                order.getId(), StockConfig.UpdateOrder.Status.RESERVED)
            .ifPresent(stockUpdateOrder -> stockUpdateOrder.setStatus(StockConfig.UpdateOrder.Status.CANCELLED));
    }

    @KafkaListener(topics = DeliveryConfig.TOPIC)
    protected void processDeliverySent(DeliveryDto delivery) {
        if (DeliveryConfig.Status.SENT.equals(delivery.getStatus())) {
            stockUpdateOrderRepository.findByOrderIdAndStatus(
                delivery.getOrder().getId(),
                StockConfig.UpdateOrder.Status.RESERVED
            ).ifPresent(stockUpdateOrder -> stockUpdateOrder.setStatus(StockConfig.UpdateOrder.Status.COMPLETED));
        }
    }
}
