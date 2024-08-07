package com.slow3586.micromarket.deliveryservice;


import com.slow3586.micromarket.api.balance.BalanceConfig;
import com.slow3586.micromarket.api.balance.BalanceUpdateOrderDto;
import com.slow3586.micromarket.api.delivery.DeliveryConfig;
import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.order.OrderClient;
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

        if (!delivery.getStatus().equals(DeliveryConfig.Status.AWAITING)) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        final DeliveryDto deliveryDto = deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus(DeliveryConfig.Status.SENT)
                    .setReceivedAt(Instant.now())));

        kafkaTemplate.executeInTransaction(kafkaOperations ->
            kafkaOperations.send(
                DeliveryConfig.TOPIC,
                deliveryDto.getId(),
                deliveryDto
            ));

        return deliveryDto;
    }

    public DeliveryDto updateDeliveryReceived(UUID deliveryId) {
        final Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        if (!delivery.getStatus().equals(DeliveryConfig.Status.SENT)) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        final DeliveryDto deliveryDto = deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus(DeliveryConfig.Status.RECEIVED)
                    .setReceivedAt(Instant.now())));

        kafkaTemplate.executeInTransaction(kafkaOperations ->
            kafkaOperations.send(
                DeliveryConfig.TOPIC,
                deliveryDto.getId(),
                deliveryDto
            ));

        return deliveryDto;
    }

    public DeliveryDto updateDeliveryCancelled(UUID deliveryId) {
        final Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        if (!delivery.getStatus().equals(DeliveryConfig.Status.AWAITING)) {
            throw new AccessDeniedException("Некорректный статус доставки!");
        }

        final DeliveryDto deliveryDto = deliveryMapper.toDto(
            deliveryRepository.save(
                delivery.setStatus(DeliveryConfig.Status.CANCELLED)
                    .setReceivedAt(Instant.now())));

        kafkaTemplate.executeInTransaction(kafkaOperations ->
            kafkaOperations.send(
                DeliveryConfig.TOPIC,
                deliveryDto.getId(),
                deliveryDto
            ));

        return deliveryDto;
    }

    @KafkaListener(topics = BalanceConfig.BalanceUpdateOrder.TOPIC)
    public void processBalanceUpdateOrderReserved(final BalanceUpdateOrderDto balanceUpdateOrder) {
        if (!BalanceConfig.BalanceUpdateOrder.Status.RESERVED.equals(balanceUpdateOrder.getStatus())) {
            return;
        }
        if (deliveryRepository.existsByOrderId(balanceUpdateOrder.getOrderId())) {
            return;
        }

        final Delivery delivery = deliveryRepository.save(
            new Delivery()
                .setCreatedAt(Instant.now())
                .setStatus(DeliveryConfig.Status.AWAITING)
                .setOrderId(balanceUpdateOrder.getOrderId()));

        kafkaTemplate.send(
            DeliveryConfig.TOPIC,
            delivery.getId(),
            deliveryMapper.toDto(delivery));
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
