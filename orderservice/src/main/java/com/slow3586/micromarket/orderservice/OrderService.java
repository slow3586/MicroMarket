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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
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
        final Order order = orderRepository
            .findByBuyerIdAndProductId(
                SecurityUtils.getPrincipalId(),
                request.getProductId()
            ).orElseGet(() ->
                orderRepository.save(
                    new Order()
                        .setBuyerId(SecurityUtils.getPrincipalId())
                        .setProductId(request.getProductId())
                        .setQuantity(0)
                        .setStatus(OrderConfig.Status.TEMPLATE)));

        order.setQuantity(order.getQuantity() + request.getQuantity());

        return orderMapper.toDto(order);
    }

    public void updateOrderActive(UUID orderId) {
        final Order order = orderRepository
            .findById(orderId)
            .orElseThrow();

        if (order.getStatus() != OrderConfig.Status.TEMPLATE) {
            throw new IllegalStateException("Bad status");
        }

        order.setStatus(OrderConfig.Status.ACTIVATED);
    }

    public void updateOrderCancelled(UUID orderId) {
        final Order order = orderRepository
            .findById(orderId)
            .orElseThrow();

        if (order.getStatus() == OrderConfig.Status.COMPLETED) {
            throw new IllegalStateException("Bad status");
        }

        order.setStatus(OrderConfig.Status.CANCELLED);
    }

    @Async
    @Scheduled(cron = "*/10 * * * * *")
    @AuditDisabled
    public void verify() {
        orderRepository.findAllByStatusAndCreatedAtBeforeOrderByCreatedAt(
                OrderConfig.Status.PAYMENT_AWAITING,
                Instant.now().minus(10, ChronoUnit.SECONDS))
            .forEach(order -> order
                .setStatus(OrderConfig.Status.CANCELLED)
                .setError(OrderConfig.Error.PAYMENT_TIMEOUT));

        orderRepository.findAllByStatusAndCreatedAtBeforeOrderByCreatedAt(
                OrderConfig.Status.DELIVERY_AWAITING,
                Instant.now().minus(1, ChronoUnit.DAYS))
            .forEach(order -> order
                .setStatus(OrderConfig.Status.CANCELLED)
                .setError(OrderConfig.Error.DELIVERY_TIMEOUT));
    }
}
