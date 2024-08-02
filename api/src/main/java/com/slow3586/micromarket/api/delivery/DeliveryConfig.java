package com.slow3586.micromarket.api.delivery;

import com.slow3586.micromarket.api.balance.BalanceTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class DeliveryConfig {
    @Bean
    public KafkaAdmin.NewTopics deliveryTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                DeliveryTopics.Status.AWAITING,
                DeliveryTopics.Status.SENT,
                DeliveryTopics.Status.RECEIVED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
