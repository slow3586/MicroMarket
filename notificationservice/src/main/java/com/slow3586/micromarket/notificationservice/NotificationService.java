package com.slow3586.micromarket.notificationservice;


import com.slow3586.micromarket.api.notification.NotificationDto;
import com.slow3586.micromarket.api.order.OrderClient;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    OrderClient orderClient;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.CREATED,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderCreated(OrderDto order) {
        notificationRepository.save(new Notification()
            .setUserId(order.getBuyer().getId())
            .setText("Вы сделали заказ.")
            .setCreatedAt(Instant.now()));
    }

    @KafkaListener(topics = OrderTopics.Payment.RESERVED,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderPaymentReserved(OrderDto order) {
        notificationRepository.save(new Notification()
            .setUserId(order.getProduct().getSeller().getId())
            .setText("Покупатель оплатил заказ.")
            .setCreatedAt(Instant.now()));
        notificationRepository.save(new Notification()
            .setUserId(order.getBuyer().getId())
            .setText("Вы оплатили заказ.")
            .setCreatedAt(Instant.now()));
    }

    @KafkaListener(topics = OrderTopics.Delivery.SENT,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderDeliverySent(OrderDto order) {
        notificationRepository.save(new Notification()
            .setUserId(order.getBuyer().getId())
            .setText("Продавец отправил Ваш заказ.")
            .setCreatedAt(Instant.now()));
    }

    @KafkaListener(topics = OrderTopics.Delivery.RECEIVED,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderDeliveryReceived(OrderDto order) {
        notificationRepository.save(new Notification()
            .setUserId(order.getProduct().getSeller().getId())
            .setText("Посылка пришла получателю.")
            .setCreatedAt(Instant.now()));
    }

    public List<NotificationDto> getUserNotifications(UUID userId) {
        return notificationRepository.findAllByUserId(userId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }
}
