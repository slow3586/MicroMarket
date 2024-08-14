package com.slow3586.micromarket.api.delivery;

import com.slow3586.micromarket.api.spring.DefaultCacheConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class DeliveryConfig {
    private static final String BASE = "entity.deliveryservice";
    public static final String TOPIC = BASE + ".delivery";
    public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.delivery.DeliveryDto";

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

    @Bean
    public RedisCacheManager deliveryCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(DeliveryDto.class);
    }
}
