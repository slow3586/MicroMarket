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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final ProductClient productClient;

    @Transactional(propagation = Propagation.MANDATORY)
    @KafkaListener(topics = OrderConfig.TOPIC, properties = OrderConfig.TOPIC_TYPE)
    public void processOrder(OrderDto order) {
        final ProductDto product = productClient.getProductById(order.getProductId());
        if (OrderConfig.Status.PAYMENT_AWAITING.equals(order.getStatus())) {
            notificationRepository.save(new Notification()
                .setUserId(order.getBuyerId())
                .setText("Вы сделали заказ. Ожидается оплата."));
        } else if (OrderConfig.Status.DELIVERY_AWAITING.equals(order.getStatus())) {
            notificationRepository.save(new Notification()
                .setUserId(product.getSellerId())
                .setText("Покупатель оплатил заказ. Необходимо его отправить."));
            notificationRepository.save(new Notification()
                .setUserId(order.getBuyerId())
                .setText("Вы оплатили заказ. Скоро продавец отправит его."));
        } else if (OrderConfig.Status.DELIVERY_SENT.equals(order.getStatus())) {
            notificationRepository.save(new Notification()
                .setUserId(order.getBuyerId())
                .setText("Продавец отправил ваш заказ. Ожидайте получения."));
        } else if (OrderConfig.Status.COMPLETED.equals(order.getStatus())) {
            notificationRepository.save(new Notification()
                .setUserId(product.getSellerId())
                .setText("Покупатель принял посылку."));
        }
    }

    public List<NotificationDto> getUserNotifications(UUID userId) {
        return notificationRepository.findAllByUserId(userId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }
}
