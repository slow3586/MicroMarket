package com.slow3586.micromarket.balanceservice;

import com.slow3586.micromarket.api.OrderDto;
import com.slow3586.micromarket.api.OrderTopics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class BalanceConsumer {
    BalanceChangeRepository balanceChangeRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Transaction.CREATED, errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrder(OrderDto order) {
        UUID buyerId = order.getBuyer().getId();

        List<BalanceChange> balanceChangeList = balanceChangeRepository.findAllByUserId(buyerId);
        int balance = balanceChangeList.stream()
            .mapToInt(BalanceChange::getValue).sum();
        int total = order.getOrderItemList().stream()
            .mapToInt(i -> i.getProduct().getPrice() * i.getQuantity()).sum();

        if (balance < total) {
            throw new IllegalStateException("No money");
        }

        balanceChangeRepository.save(new BalanceChange()
            .setUserId(buyerId)
            .setValue(-total));

        kafkaTemplate.send(
            OrderTopics.Transaction.BALANCE,
            order.getId(),
            order);
    }
}
