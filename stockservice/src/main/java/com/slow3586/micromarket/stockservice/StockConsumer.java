package com.slow3586.micromarket.stockservice;

import com.slow3586.micromarket.api.delivery.DeliveryConfig;
import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.order.OrderConfig;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.stock.StockConfig;
import com.slow3586.micromarket.api.stock.StockUpdateDto;
import com.slow3586.micromarket.api.stock.StockUpdateOrderDto;
import com.slow3586.micromarket.stockservice.entity.StockUpdateOrder;
import com.slow3586.micromarket.stockservice.mapper.StockUpdateMapper;
import com.slow3586.micromarket.stockservice.mapper.StockUpdateOrderMapper;
import com.slow3586.micromarket.stockservice.repository.StockUpdateOrderRepository;
import com.slow3586.micromarket.stockservice.repository.StockUpdateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @KafkaListener(topics = OrderConfig.TOPIC, properties = OrderConfig.TOPIC_TYPE)
    public void processOrder(final OrderDto order) {
        if (OrderConfig.Status.ACTIVATED.equals(order.getStatus())) {
            this.processOrderActivated(order);
        } else if (OrderConfig.Status.CANCELLED.equals(order.getStatus())) {
            this.processOrderCancelled(order);
        }
    }

    public void processOrderActivated(final OrderDto order) {
        if (stockUpdateOrderRepository.existsByOrderId(order.getId())) {
            return;
        }

        final long stock = stockService.getStockSumByProductId(order.getProductId());

        if (stock < order.getQuantity()) {
            throw new IllegalStateException("Недостаточно товара");
        }

        this.stockUpdateOrderRepository.save(
            new StockUpdateOrder()
                .setStatus(StockConfig.StockUpdateOrder.Status.RESERVED)
                .setProductId(order.getProductId())
                .setOrderId(order.getId())
                .setValue(order.getQuantity()));
    }

    public void processOrderCancelled(final OrderDto order) {
        stockUpdateOrderRepository.findByOrderId(order.getId())
            .ifPresent(stockUpdateOrder -> stockUpdateOrder.setStatus(StockConfig.StockUpdateOrder.Status.CANCELLED));
    }

    @KafkaListener(topics = StockConfig.StockUpdate.TOPIC, properties = StockConfig.StockUpdate.TOPIC_TYPE)
    @CacheEvict(value = StockConfig.CACHE_GETSTOCKSUMBYPRODUCTID, key = "#stockUpdate.productId")
    public void processStockUpdate(final StockUpdateDto stockUpdate) {
    }

    @KafkaListener(topics = StockConfig.StockUpdateOrder.TOPIC, properties = StockConfig.StockUpdateOrder.TOPIC_TYPE)
    @CacheEvict(value = StockConfig.StockUpdateOrder.CACHE_ORDERID, key = "#stockUpdateOrder.orderId")
    public void processStockUpdateOrder(final StockUpdateOrderDto stockUpdateOrder) {
    }

    @KafkaListener(topics = DeliveryConfig.TOPIC, properties = DeliveryConfig.TOPIC_TYPE)
    public void processDeliverySent(final DeliveryDto delivery) {
        if (DeliveryConfig.Status.RECEIVED.equals(delivery.getStatus())) {
            stockUpdateOrderRepository.findByOrderIdAndStatus(
                delivery.getOrderId(),
                StockConfig.StockUpdateOrder.Status.RESERVED
            ).ifPresent(stockUpdateOrder -> stockUpdateOrder.setStatus(StockConfig.StockUpdateOrder.Status.COMPLETED));
        }
    }
}
