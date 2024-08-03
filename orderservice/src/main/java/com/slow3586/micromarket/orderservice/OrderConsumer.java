package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.order.OrderTopics;
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
@Transactional(transactionManager = "transactionManager")
public class OrderConsumer {
    OrderRepository orderRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    OrderService orderService;

    @KafkaListener(topics = OrderTopics.Payment.AWAITING,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderPaymentAwaiting(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus(OrderTopics.Status.PAYMENT_AWAITING))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Payment.RESERVED,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderPaymentReserved(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus(OrderTopics.Status.PAYMENT_RESERVED))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Delivery.AWAITING,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderDeliveryAwaiting(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus(OrderTopics.Status.DELIVERY_AWAITING))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Delivery.SENT,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderDeliverySent(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus(OrderTopics.Status.DELIVERY_SENT))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.Delivery.RECEIVED,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderDeliveryReceived(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setStatus(OrderTopics.Status.DELIVERY_RECEIVED))
            .orElseThrow();
    }

    @KafkaListener(topics = OrderTopics.ERROR)
    public void processOrderError(OrderDto order) {
        orderRepository.findById(order.getId())
            .map(entity -> order.setStatus(OrderTopics.Status.ERROR)
                .setError(order.getError()))
            .orElseThrow();
    }
}
