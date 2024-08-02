package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderDto;
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
public class OrderConsumer {
    OrderRepository orderRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    OrderService orderService;

    @KafkaListener(topics = {OrderTopics.Transaction.ERROR},
        errorHandler = "loggingKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderError(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("ERROR"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Awaiting.PAYMENT,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderAwaitingPayment(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("AWAITING_BALANCE"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Awaiting.DELIVERY,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderAwaitingConfirmation(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("AWAITING_DELIVERY"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.DELIVERY,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderConfirmation(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("COMPLETED"))
            .orElseThrow();
    }
}
