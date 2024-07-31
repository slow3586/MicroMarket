package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.order.NewOrderRequest;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.user.UserDto;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import com.slow3586.micromarket.orderservice.entity.Order;
import com.slow3586.micromarket.orderservice.entity.OrderItem;
import com.slow3586.micromarket.orderservice.repository.OrderItemRepository;
import com.slow3586.micromarket.orderservice.repository.OrderRepository;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    private final OrderItemRepository orderItemRepository;

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

    @Transactional(transactionManager = "transactionManager")
    public UUID newOrder(NewOrderRequest request) {
        final Order order = orderRepository.save(
            new Order()
                .setBuyerId(SecurityUtils.getPrincipalId())
                .setStatus("NEW")
                .setCreatedAt(Instant.now()));

        final List<OrderItem> orderItemList =
            request.getOrderRequestItemList()
                .stream()
                .map(i -> orderItemRepository.save(new OrderItem()
                    .setOrder(order)
                    .setQuantity(i.getQuantity())
                    .setStatus("NEW")
                    .setProductId(i.getProductId())))
                .toList();

        kafkaTemplate.executeInTransaction((kafkaOperations) ->
            kafkaOperations.send(OrderTopics.Transaction.NEW,
                order.getId(),
                new OrderTransaction()
                    .setId(order.getId())
                    .setBuyer(new UserDto().setId(order.getBuyerId()))
                    .setStatus(order.getStatus())
                    .setOrderItemList(orderItemList
                        .stream()
                        .map(item ->
                            new OrderTransaction.OrderItemDto()
                                .setId(item.getId())
                                .setQuantity(item.getQuantity())
                                .setProduct(new ProductDto().setId(item.getProductId())))
                        .toList())));

        return order.getId();
    }
}
