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
                OrderTopics.Transaction.Awaiting.BALANCE,
                OrderTopics.Transaction.BALANCE,
                OrderTopics.Transaction.Awaiting.CONFIRMATION,
                OrderTopics.Transaction.CONFIRMATION,
                OrderTopics.Transaction.Awaiting.DELIVERY,
                OrderTopics.Transaction.DELIVERY,
                OrderTopics.Transaction.COMPLETED,
                OrderTopics.Transaction.ERROR,
                OrderTopics.Status.PAID,
                OrderTopics.Status.COMPLETED,
                OrderTopics.Status.CANCELLED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
