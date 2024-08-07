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
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderListenerErrorHandler implements ConsumerAwareListenerErrorHandler {
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @Override
    public Object handleError(
        Message<?> message,
        ListenerExecutionFailedException exception,
        Consumer<?, ?> consumer
    ) {
        final OrderDto order = (OrderDto) message.getPayload();

        kafkaTemplate.send(OrderConfig.TOPIC,
            (UUID) message.getHeaders().get(KafkaHeaders.KEY),
            order);

        return null;
    }
}
