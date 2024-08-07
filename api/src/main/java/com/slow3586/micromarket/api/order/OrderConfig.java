
package com.slow3586.micromarket.api.order;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class OrderConfig {
    private static final String NAME = "order";

    public final static String TOPIC = NAME + ".table";

    public enum Status {
        TEMPLATE,
        CREATED,
        PAYMENT_AWAITING,
        PAYMENT_RESERVED,
        DELIVERY_AWAITING,
        DELIVERY_SENT,
        COMPLETED,
        CANCELLED
    }

    @Bean
    public KafkaAdmin.NewTopics orderTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                OrderConfig.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
