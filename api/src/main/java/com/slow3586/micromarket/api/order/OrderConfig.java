package com.slow3586.micromarket.api.order;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class OrderConfig {
    @Bean
    public KafkaAdmin.NewTopics orderTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                OrderTopics.Transaction.NEW,
                OrderTopics.Transaction.PRODUCT,
                OrderTopics.Transaction.USER,
                OrderTopics.Transaction.STOCK,
                OrderTopics.Transaction.BALANCE,
                OrderTopics.Transaction.PUBLISH,
                OrderTopics.Transaction.BALANCE,
                OrderTopics.Transaction.COMPLETED,
                OrderTopics.Transaction.ERROR,
                OrderTopics.Request.REQUEST_CREATE,
                OrderTopics.Request.REQUEST_COMPLETED,
                OrderTopics.Request.REQUEST_CANCEL,
                OrderTopics.Request.REQUEST_CREATE_RESPONSE,
                OrderTopics.Request.REQUEST_COMPLETED_RESPONSE,
                OrderTopics.Request.REQUEST_CANCEL_RESPONSE,
                OrderTopics.Status.COMPLETED,
                OrderTopics.Status.CANCELLED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
