package com.slow3586.micromarket.api.stock;

import com.slow3586.micromarket.api.spring.DefaultCacheConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class StockConfig {
    private static final String BASE = "entity.stockservice";

    public static final String CACHE_GETSTOCKSUMBYPRODUCTID = "cache.stockservice.getStockSumByProductId";


    public static class StockUpdate {
        public final static String TOPIC = BASE + ".stock_update";
        public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.stock.StockUpdateDto";
    }

    public static class StockUpdateOrder {
        public final static String TOPIC = BASE + ".stock_update_order";
        public final static String TOPIC_TYPE = "spring.json.value.default.type=" + "com.slow3586.micromarket.api.stock.StockUpdateOrderDto";

        public static final String CACHE_ORDERID = "cache.stockservice.stockorder.orderid";

        public enum Status {
            AWAITING,
            RESERVED,
            COMPLETED,
            CANCELLED
        }
    }

    @Bean
    public KafkaAdmin.NewTopics stockTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                StockUpdate.TOPIC,
                StockUpdateOrder.TOPIC
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }

    @Bean
    public RedisCacheManager stockUpdateCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(StockUpdateDto.class);
    }

    @Bean
    public RedisCacheManager stockUpdateOrderCacheManager(DefaultCacheConfig defaultCacheConfig) {
        return defaultCacheConfig.createBasicCacheManager(StockUpdateOrderDto.class);
    }
}
