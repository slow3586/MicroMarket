package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.order.NewOrderRequest;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.orderservice.entity.Order;
import com.slow3586.micromarket.orderservice.entity.OrderItem;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

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

    public Order newOrder(NewOrderRequest newOrderRequest) {
        return orderRepository.save(
            new Order()
                .setBuyerId(newOrderRequest.getBuyerId())
                .setStatus("NEW")
                .setCreatedAt(Instant.now())
                .setOrderItemList(newOrderRequest.getOrderRequestItemList()
                    .stream()
                    .map(i -> new OrderItem()
                        .setQuantity(i.getQuantity())
                        .setSellerId(newOrderRequest.getSellerId())
                        .setStatus("NEW")
                        .setProductId(i.getProductId()))
                    .toList()));
    }
}
