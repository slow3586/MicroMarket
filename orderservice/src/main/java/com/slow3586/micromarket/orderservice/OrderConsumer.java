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
            .map(entity -> order.setStatus("ERROR")
                .setError(order.getError()))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Payment.AWAITING,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderPaymentAwaiting(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("PAYMENT_AWAITING"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Payment.RESERVED,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderPaymentReserved(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("PAYMENT_RESERVED"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Delivery.AWAITING,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderDeliveryAwaiting(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("DELIVERY_AWAITING"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Delivery.SENT,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderDeliverySent(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("DELIVERY_SENT"))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Transaction.Delivery.RECEIVED,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderDeliveryReceived(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus("DELIVERY_RECEIVED"))
            .orElseThrow();
    }
}
