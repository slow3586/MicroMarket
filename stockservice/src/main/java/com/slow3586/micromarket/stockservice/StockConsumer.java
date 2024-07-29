package com.slow3586.micromarket.stockservice;

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
public class StockConsumer {
    StockChangeRepository stockChangeRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Transaction.CREATED, errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrder(OrderDto order) {
        order.getOrderItemList().forEach(item -> {
            UUID productId = item.getProduct().getId();
            List<StockChange> balanceChangeList = stockChangeRepository.findAllByProductId(productId);
            int stock = balanceChangeList.stream()
                .mapToInt(StockChange::getValue)
                .sum();
            int total = order.getOrderItemList().stream()
                .mapToInt(OrderDto.OrderItemDto::getQuantity)
                .sum();

            if (stock < total) {
                throw new IllegalStateException("No stock");
            }

            stockChangeRepository.save(new StockChange()
                .setProductId(productId)
                .setValue(-total));
        });

        kafkaTemplate.send(
            OrderTopics.Transaction.STOCK,
            order.getId(),
            order);
    }
}
