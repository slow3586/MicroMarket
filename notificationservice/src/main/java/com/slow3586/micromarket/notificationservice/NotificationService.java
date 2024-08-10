package com.slow3586.micromarket.notificationservice;


import com.slow3586.micromarket.api.notification.NotificationDto;
import com.slow3586.micromarket.api.order.OrderClient;
import com.slow3586.micromarket.api.order.OrderConfig;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProductClient productClient;

    @KafkaListener(topics = OrderConfig.TOPIC, properties = OrderConfig.TOPIC_TYPE)
    public void processOrder(OrderDto order) {
        if (OrderConfig.Status.PAYMENT_AWAITING.equals(order.getStatus())) {
            notificationRepository.save(new Notification()
                .setUserId(order.getBuyerId())
                .setText("Вы сделали заказ. Ожидается оплата."));
        } else if (OrderConfig.Status.DELIVERY_AWAITING.equals(order.getStatus())) {
            final ProductDto product = productClient.getProductById(order.getProductId());
            notificationRepository.save(new Notification()
                .setUserId(product.getSellerId())
                .setText("Покупатель оплатил заказ. Необходимо его отправить."));
            notificationRepository.save(new Notification()
                .setUserId(order.getBuyerId())
                .setText("Вы оплатили заказ."));
        }
    }

    public List<NotificationDto> getUserNotifications(UUID userId) {
        return notificationRepository.findAllByUserId(userId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }
}
