package com.slow3586.micromarket.deliveryservice;


import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.delivery.DeliveryTopics;
import com.slow3586.micromarket.api.order.OrderClient;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.order.OrderTopics;
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
        final Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        if (!delivery.getStatus().equals("AWAITING")) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        final DeliveryDto deliveryDto = deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus("SENT")
                    .setReceivedAt(Instant.now())));

        kafkaTemplate.executeInTransaction(kafkaOperations ->
            kafkaOperations.send(
                DeliveryTopics.Status.SENT,
                deliveryDto.getId(),
                deliveryDto
            ));

        return deliveryDto;
    }

    public DeliveryDto updateDeliveryReceived(UUID deliveryId) {
        final Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        if (!delivery.getStatus().equals("SENT")) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        final DeliveryDto deliveryDto = deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus("RECEIVED")
                    .setReceivedAt(Instant.now())));

        kafkaTemplate.executeInTransaction(kafkaOperations ->
            kafkaOperations.send(
                DeliveryTopics.Status.RECEIVED,
                deliveryDto.getId(),
                deliveryDto
            ));

        return deliveryDto;
    }

    public DeliveryDto updateDeliveryCancelled(UUID deliveryId) {
        final Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        if (!delivery.getStatus().equals("AWAITING")) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        final DeliveryDto deliveryDto = deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus("CANCELLED")
                    .setReceivedAt(Instant.now())));

        kafkaTemplate.executeInTransaction(kafkaOperations ->
            kafkaOperations.send(
                DeliveryTopics.Status.RECEIVED,
                deliveryDto.getId(),
                deliveryDto
            ));

        return deliveryDto;
    }

    @KafkaListener(topics = OrderTopics.Payment.RESERVED,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrderPaymentReserved(OrderDto order) {
        if (deliveryRepository.existsByOrderId(order.getId())) {
            return;
        }

        final Delivery delivery = deliveryRepository.save(
            new Delivery()
                .setCreatedAt(Instant.now())
                .setStatus("AWAITING")
                .setOrderId(order.getId()));

        kafkaTemplate.send(
            OrderTopics.Delivery.AWAITING,
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
