package com.slow3586.micromarket.deliveryservice;


import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.order.OrderClient;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class DeliveryService {
    DeliveryRepository deliveryRepository;
    DeliveryMapper deliveryMapper;
    OrderClient orderClient;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public DeliveryDto updateDeliverySent(UUID deliveryId) {
        final Delivery delivery = updateDeliveryInternal(deliveryId);

        if (!delivery.getStatus().equals("AWAITING")) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        return deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus("SENT")
                    .setSentAt(Instant.now())));
    }

    public DeliveryDto updateDeliveryReceived(UUID deliveryId) {
        final Delivery delivery = updateDeliveryInternal(deliveryId);

        if (!delivery.getStatus().equals("SENT")) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        return deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus("RECEIVED")
                    .setReceivedAt(Instant.now())));
    }

    public DeliveryDto updateDeliveryCancelled(UUID deliveryId) {
        final Delivery delivery = updateDeliveryInternal(deliveryId);

        if (delivery.getStatus().equals("RECEIVED")) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        return deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus("CANCELLED")
                    .setReceivedAt(Instant.now())));
    }

    protected Delivery updateDeliveryInternal(UUID deliveryId) {
        final UUID userId = SecurityUtils.getPrincipalId();
        final Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        final OrderDto order = orderClient.getOrder(delivery.getOrderId());

        if (!userId.equals(order.getProduct().getSeller().getId())) {
            throw new AccessDeniedException("У пользователя нет доступа к этой доставке!");
        }

        return delivery;
    }

    @KafkaListener(topics = OrderTopics.Transaction.Payment.RESERVED,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderPaymentReserved(OrderDto order) {
        final Delivery delivery = deliveryRepository.save(
            new Delivery()
                .setCreatedAt(Instant.now())
                .setStatus("AWAITING")
                .setOrderId(order.getId()));

        kafkaTemplate.send(
            OrderTopics.Transaction.Delivery.AWAITING,
            order.getId(),
            order.setDelivery(deliveryMapper.toDto(delivery)));
    }

    public DeliveryDto getDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
            .map(deliveryMapper::toDto)
            .orElseThrow();
    }

    public DeliveryDto getDeliveryByOrder(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
            .map(deliveryMapper::toDto)
            .orElseThrow();
    }
}
