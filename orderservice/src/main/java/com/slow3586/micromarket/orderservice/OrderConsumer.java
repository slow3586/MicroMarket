package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.order.NewOrderRequest;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.orderservice.entity.Order;
import com.slow3586.micromarket.orderservice.repository.OrderItemRepository;
import com.slow3586.micromarket.orderservice.repository.OrderRepository;
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
