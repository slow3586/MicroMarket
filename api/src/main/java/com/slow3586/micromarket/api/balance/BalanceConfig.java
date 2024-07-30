package com.slow3586.micromarket.api.balance;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class BalanceConfig {
    @Bean
    public KafkaAdmin.NewTopics balanceTopics() {
        return new KafkaAdmin.NewTopics(Stream.of(
                BalanceTopics.Replenish.NEW,
                BalanceTopics.Transfer.COMPLETED,
                BalanceTopics.Transfer.AWAITING,
                BalanceTopics.Transfer.RESERVED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
