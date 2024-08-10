package com.slow3586.micromarket.api.balance;

import com.slow3586.micromarket.api.spring.DefaultCacheConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class BalanceConfig {
    private static final String BASE = "entity.balanceservice";

    public static class BalanceUpdate {
        public final static String TOPIC = BASE + ".balance_update";
        public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.balance.BalanceUpdateDto";
    }

    public static class BalanceUpdateOrder {
        public final static String TOPIC = BASE + ".balance_update_order";
        public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.balance.BalanceUpdateOrderDto";

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

    @Bean
    public RedisCacheManager balanceUpdateCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(BalanceUpdateDto.class);
    }

    @Bean
    public RedisCacheManager balanceUpdateOrderCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(BalanceUpdateOrderDto.class);
    }
}
