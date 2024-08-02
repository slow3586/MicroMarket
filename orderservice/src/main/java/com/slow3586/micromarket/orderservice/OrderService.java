package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.order.CreateOrderRequest;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
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
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    ProductClient productClient;
    UserClient userClient;

    public OrderDto getOrder(UUID orderId) {
        final OrderDto orderDto = orderRepository
            .findById(orderId)
            .map(orderMapper::toDto)
            .orElseThrow();

        orderDto.setBuyer(userClient.getUser(orderDto.getBuyer().getId()));
        orderDto.setSeller(userClient.getUser(orderDto.getSeller().getId()));
        orderDto.setProduct(productClient.getProduct(orderDto.getProduct().getId()));

        return orderDto;
    }

    @Transactional(transactionManager = "transactionManager")
    public UUID createOrder(CreateOrderRequest request) {
        final Order order = orderRepository.save(
            new Order()
                .setBuyerId(SecurityUtils.getPrincipalId())
                .setProductId(request.getProductId())
                .setQuantity(request.getQuantity())
                .setStatus("NEW")
                .setCreatedAt(Instant.now()));

        kafkaTemplate.executeInTransaction((kafkaOperations) ->
            kafkaOperations.send(OrderTopics.Transaction.NEW,
                order.getId(),
                new OrderDto()
                    .setId(order.getId())
                    .setBuyer(new UserDto().setId(order.getBuyerId()))
                    .setProduct(new ProductDto().setId(order.getProductId()))
                    .setQuantity(order.getQuantity())
                    .setStatus(order.getStatus())
                    .setCreatedAt(Instant.now())));

        return order.getId();
    }

    @Transactional(transactionManager = "transactionManager")
    public OrderDto updateOrderCancelled(UUID orderId) {
        return orderRepository
            .findById(orderId)
            .map(o -> o.setStatus("CANCELLED"))
            .map(orderMapper::toDto)
            .orElseThrow();
    }

    @Async
    @Scheduled(cron = "* */1 * * * *")
    public void verify() {
        orderRepository.findBadOrders()
            .forEach(order ->
                kafkaTemplate.executeInTransaction((operations) ->
                    operations.send(OrderTopics.Transaction.ERROR,
                        order.getId(),
                        order.setError("TIMEOUT"))));
    }
}
