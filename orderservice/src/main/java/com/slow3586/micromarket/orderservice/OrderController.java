package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.order.NewOrderRequest;
import com.slow3586.micromarket.api.order.OrderTopics;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class OrderController {
    OrderService orderService;
    ReplyingKafkaTemplate<UUID, Object, Object> replyingKafkaTemplate;

    @PostMapping("create")
    @Transactional(transactionManager = "kafkaTransactionManager")
    public CompletableFuture<UUID> create(@RequestBody @Valid NewOrderRequest newOrderRequest) {
        return replyingKafkaTemplate.sendAndReceive(
                new ProducerRecord<>(OrderTopics.Request.REQUEST_CREATE, newOrderRequest))
            .thenApply(ConsumerRecord::value)
            .thenApply(o -> ((UUID) o))
            .toCompletableFuture();
    }
}
