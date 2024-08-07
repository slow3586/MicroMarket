package com.slow3586.micromarket.api.delivery;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class DeliveryConfig {
    public static final String TOPIC = "delivery.update";

    public enum Status {
        AWAITING,
        SENT,
        RECEIVED,
        CANCELLED
    }

    @Bean
    public KafkaAdmin.NewTopics deliveryTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                DeliveryConfig.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
