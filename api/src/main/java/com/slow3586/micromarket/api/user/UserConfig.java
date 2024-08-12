package com.slow3586.micromarket.api.user;

import com.slow3586.micromarket.api.spring.DefaultCacheConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class UserConfig {
    private static final String BASE = "entity.userservice";

    public final static String TOPIC = BASE + ".user_";
    public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.user.UserDto";

    @Bean
    public KafkaAdmin.NewTopics userTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                UserConfig.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public RedisCacheManager userCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(UserDto.class);
    }
}
