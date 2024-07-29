package com.slow3586.micromarket.api;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingKafkaListenerErrorHandler implements ConsumerAwareListenerErrorHandler {
    @Override
    public Object handleError(
        Message<?> message,
        ListenerExecutionFailedException exception,
        Consumer<?, ?> consumer
    ) {
        log.error("#handleError: {}", exception.getMessage(), exception);
        return null;
    }
}
