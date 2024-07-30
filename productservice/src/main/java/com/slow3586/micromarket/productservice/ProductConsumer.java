package com.slow3586.micromarket.productservice;

import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.api.order.OrderTopics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductConsumer {
    ProductService productService;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Transaction.USER,
        errorHandler = "orderTransactionListenerErrorHandler")
    public void processNewOrder(OrderTransaction order) {
        kafkaTemplate.send(
            OrderTopics.Transaction.PRODUCT,
            order.getId(),
            productService.processNewOrder(order));
    }
}
