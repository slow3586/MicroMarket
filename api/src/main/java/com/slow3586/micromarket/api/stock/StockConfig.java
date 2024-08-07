package com.slow3586.micromarket.api.stock;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class StockConfig {
    private static final String NAME = "stock";

    public static class Update {
        public final static String TOPIC = NAME + ".update";
    }

    public static class UpdateOrder {
        public final static String TOPIC = NAME + ".update.order";

        public enum Status {
            AWAITING,
            RESERVED,
            COMPLETED,
            CANCELLED
        }
    }

    @Deprecated
    public KafkaAdmin.NewTopics stockTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                Update.TOPIC,
                UpdateOrder.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
