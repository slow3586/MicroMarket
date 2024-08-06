package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.audit.AuditDisabled;
import com.slow3586.micromarket.api.delivery.DeliveryClient;
import com.slow3586.micromarket.api.order.CreateOrderRequest;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.StockClient;
import com.slow3586.micromarket.api.user.UserClient;
import com.slow3586.micromarket.api.user.UserDto;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

    public OrderDto getOrder(UUID orderId) {
        final OrderDto orderDto = orderRepository
            .findById(orderId)
            .map(orderMapper::toDto)
            .orElseThrow();

        orderDto.setBuyer(userClient.getUserById(orderDto.getBuyer().getId()));
        orderDto.setProduct(productClient.getProductById(orderDto.getProduct().getId()));

        return orderDto;
    }

    public OrderDto createOrder(CreateOrderRequest request) {
        final Order order = orderRepository.save(
            new Order()
                .setBuyerId(SecurityUtils.getPrincipalId())
                .setProductId(request.getProductId())
                .setQuantity(request.getQuantity())
                .setStatus(OrderTopics.Status.INITIALIZATION_AWAITING)
                .setCreatedAt(Instant.now()));

        kafkaTemplate.executeInTransaction((kafkaOperations) ->
            kafkaOperations.send(OrderTopics.CREATED,
                order.getId(),
                new OrderDto()
                    .setId(order.getId())
                    .setBuyer(new UserDto().setId(order.getBuyerId()))
                    .setProduct(new ProductDto().setId(order.getProductId()))
                    .setQuantity(order.getQuantity())
                    .setStatus(order.getStatus())
                    .setCreatedAt(Instant.now())));

        return orderMapper.toDto(order);
    }

    public OrderDto updateOrderCancelled(UUID orderId) {
        OrderDto orderDto = orderRepository
            .findById(orderId)
            .map(o -> o.setStatus(OrderTopics.Status.CANCELLED))
            .map(orderMapper::toDto)
            .orElseThrow();

        kafkaTemplate.executeInTransaction((kafkaOperations) ->
            kafkaOperations.send(OrderTopics.ERROR,
                orderDto.getId(),
                orderDto));

        return orderDto;
    }

    @Async
    @Scheduled(cron = "*/10 * * * * *")
    @AuditDisabled
    public void verify() {
        orderRepository.findBadOrders()
            .forEach(order ->
                kafkaTemplate.executeInTransaction((operations) ->
                    operations.send(OrderTopics.ERROR,
                        order.getId(),
                        orderMapper.toDto(
                            order.setStatus(OrderTopics.Status.ERROR)
                                .setError("TIMEOUT")))));
    }
}
