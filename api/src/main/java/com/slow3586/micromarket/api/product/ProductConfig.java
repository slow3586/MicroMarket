package com.slow3586.micromarket.api.product;

import com.slow3586.micromarket.api.spring.DefaultCacheConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class ProductConfig {
    private static final String BASE = "entity.productservice";

    public final static String TOPIC = BASE + ".product";
    public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.product.ProductDto";

    @Bean
    public KafkaAdmin.NewTopics productTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                ProductConfig.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public RedisCacheManager productCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(ProductDto.class);
    }
}
