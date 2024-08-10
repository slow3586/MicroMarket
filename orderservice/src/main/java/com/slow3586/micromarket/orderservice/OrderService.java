package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.audit.AuditDisabled;
import com.slow3586.micromarket.api.delivery.DeliveryClient;
import com.slow3586.micromarket.api.order.CreateOrderRequest;
import com.slow3586.micromarket.api.order.OrderConfig;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.stock.StockClient;
import com.slow3586.micromarket.api.user.UserClient;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    ProductClient productClient;
    UserClient userClient;
    DeliveryClient deliveryClient;
    StockClient stockClient;

    @Cacheable(value = OrderConfig.TOPIC, key = "#orderId", cacheManager = "orderCacheManager")
    public OrderDto getOrderById(UUID orderId) {
        return orderRepository
            .findById(orderId)
            .map(orderMapper::toDto)
            .orElseThrow();
    }

    public OrderDto createOrder(CreateOrderRequest request) {
        final Order order = orderRepository.save(new Order()
            .setBuyerId(SecurityUtils.getPrincipalId())
            .setProductId(request.getProductId())
            .setQuantity(request.getQuantity())
            .setStatus(OrderConfig.Status.CREATED));

        return orderMapper.toDto(order);
    }

    public OrderDto updateOrderCancelled(UUID orderId) {
        return orderRepository
            .findById(orderId)
            .map(o -> o.setStatus(OrderConfig.Status.CANCELLED))
            .map(orderMapper::toDto)
            .orElseThrow();
    }

    @Async
    @Scheduled(cron = "*/10 * * * * *")
    @AuditDisabled
    public void verify() {
        orderRepository.queryForAwaitingBalanceTimeoutCheck()
            .forEach(order ->
                kafkaTemplate.executeInTransaction((operations) ->
                    operations.send(OrderConfig.TOPIC,
                        order.getId(),
                        orderMapper.toDto(
                            order.setStatus(OrderConfig.Status.CANCELLED)
                                .setError("AWAITING_BALANCE_TIMEOUT")))));
        orderRepository.queryForAwaitingBalanceTimeoutCheck()
            .forEach(order ->
                kafkaTemplate.executeInTransaction((operations) ->
                    operations.send(OrderConfig.TOPIC,
                        order.getId(),
                        orderMapper.toDto(
                            order.setStatus(OrderConfig.Status.CANCELLED)
                                .setError("AWAITING_DELIVERY_TIMEOUT")))));
    }
}
