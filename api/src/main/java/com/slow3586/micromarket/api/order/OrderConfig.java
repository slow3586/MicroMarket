
package com.slow3586.micromarket.api.order;

import com.slow3586.micromarket.api.spring.DefaultCacheConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class OrderConfig {
    private static final String BASE = "entity.orderservice";

    public final static String TOPIC = BASE + ".order_";
    public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.order.OrderDto";

    public enum Status {
        TEMPLATE,
        ACTIVATED,
        PAYMENT_AWAITING,
        PAYMENT_RESERVED,
        DELIVERY_AWAITING,
        DELIVERY_SENT,
        COMPLETED,
        CANCELLED
    }

    public enum Error {
        UNKNOWN,
        ORDER_CANCELLED,
        PAYMENT_TIMEOUT,
        PAYMENT_CANCELLED,
        DELIVERY_TIMEOUT,
        DELIVERY_CANCELLED,
        DELIVERY_RETURNED
    }

    @Bean
    public KafkaAdmin.NewTopics orderTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                OrderConfig.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public RedisCacheManager orderCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(OrderDto.class);
    }
}
