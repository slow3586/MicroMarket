package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.balance.BalanceTopics;
import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.order.NewOrderRequest;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.orderservice.entity.Order;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderConsumer {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    OrderService orderService;

    @KafkaListener(topics = OrderTopics.Request.REQUEST_CREATE,
        errorHandler = "defaultKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.Request.REQUEST_CREATE_RESPONSE)
    public UUID newOrder(@Valid NewOrderRequest newOrderRequest) {
        Order order = orderService.newOrder(newOrderRequest);

        kafkaTemplate.send(OrderTopics.Transaction.NEW,
            order.getId(),
            order);

        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.Request.REQUEST_CANCEL,
        errorHandler = "defaultKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.Request.REQUEST_CANCEL_RESPONSE)
    public UUID cancelOrder(UUID orderId) {
        final Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " not found"));
        if (!order.getStatus().equals("AWAITING_PAYMENT")) {
            throw new IllegalStateException("Order status is not in AWAITING_PAYMENT");
        }

        order.setStatus("CANCELLED");

        kafkaTemplate.send(OrderTopics.Status.COMPLETED,
            order.getId(),
            order);

        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.Request.REQUEST_COMPLETED,
        errorHandler = "defaultKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.Request.REQUEST_COMPLETED_RESPONSE)
    public UUID completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("Order with id " + orderId + " doesn't exist."));
        if (!order.getStatus().equals("PAID")) {
            throw new IllegalStateException("Order status is not in PAID");
        }
        order = orderRepository.save(order.setStatus("COMPLETED"));
        kafkaTemplate.send(OrderTopics.Status.COMPLETED,
            order.getId(),
            order);

        return order.getId();
    }

    @KafkaListener(topics = {OrderTopics.Transaction.ERROR},
        errorHandler = "loggingKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderError(OrderTransaction order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("ERROR"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Awaiting.BALANCE,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderAwaitingPayment(OrderTransaction order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("AWAITING_BALANCE"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Awaiting.CONFIRMATION,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderAwaitingConfirmation(OrderTransaction order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("AWAITING_CONFIRMATION"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.BALANCE,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderConfirmation(OrderTransaction order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("AWAITING_DELIVERY"))
            .orElseThrow();
    }
}
