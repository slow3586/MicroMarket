package com.slow3586.micromarket.api.balance;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class BalanceConfig {
    private static final String BASE = "balance";

    public static class BalanceUpdate {
        public final static String TOPIC = BASE + ".update";
    }

    public static class BalanceUpdateOrder {
        public final static String TOPIC = BASE + ".update.order";

        public enum Status {
            AWAITING,
            RESERVED,
            COMPLETED,
            CANCELLED
        }
    }

    @Bean
    public KafkaAdmin.NewTopics balanceTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                BalanceUpdate.TOPIC,
                BalanceUpdateOrder.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
