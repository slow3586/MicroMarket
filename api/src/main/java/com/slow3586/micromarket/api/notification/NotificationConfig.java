package com.slow3586.micromarket.api.notification;

import com.slow3586.micromarket.api.spring.DefaultCacheConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class NotificationConfig {
    private static final String BASE = "entity.notificationservice";
    public static final String TOPIC = BASE + ".notification";
    public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.notification.NotificationDto";

    @Bean
    public KafkaAdmin.NewTopics notificationTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                NotificationConfig.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public RedisCacheManager notificationCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(NotificationDto.class);
    }
}
