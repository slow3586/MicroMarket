package com.slow3586.micromarket.api.order;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderTransactionListenerErrorHandler implements ConsumerAwareListenerErrorHandler {
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @Override
    public Object handleError(
        Message<?> message,
        ListenerExecutionFailedException exception,
        Consumer<?, ?> consumer
    ) {
        final OrderDto order = (OrderDto) message.getPayload();

        kafkaTemplate.send(MessageBuilder.withPayload(order
                .setStatus(OrderTopics.Status.ERROR)
                .setError(exception.getMessage()))
            .setHeader(KafkaHeaders.EXCEPTION_FQCN, exception.getCause().getClass().getSimpleName())
            .setHeader(KafkaHeaders.EXCEPTION_MESSAGE, exception.getCause().getMessage())
            .setHeader(KafkaHeaders.KEY, message.getHeaders().get(KafkaHeaders.RECEIVED_KEY))
            .setHeader(KafkaHeaders.TOPIC, OrderTopics.ERROR)
            .build());

        return null;
    }
}
