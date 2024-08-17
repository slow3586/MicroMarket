package com.slow3586.micromarket.api.spring;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonObjectWriter;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class DefaultCacheConfig {
    ObjectMapper objectMapper;
    @NonFinal
    @Value("${spring.application.name}")
    String applicationName;
    RedisConnectionFactory connectionFactory;

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(60))
            .prefixCacheNameWith("");
    }

    public RedisCacheManager createBasicCacheManager(
        Class<?> clazz
    ) {
        return new RedisCacheManager(
            RedisCacheWriter.lockingRedisCacheWriter(connectionFactory),
            redisCacheConfiguration()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer(
                        objectMapper.copy()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                            .findAndRegisterModules(),
                        (mapper, source, type) -> mapper.readValue(source, clazz),
                        JacksonObjectWriter.create()))));
    }

    @Bean
    @Primary
    public RedisCacheManager typingCacheManager() {
        return new RedisCacheManager(
            RedisCacheWriter.lockingRedisCacheWriter(connectionFactory),
            redisCacheConfiguration()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer(
                        this.objectMapper.copy()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                            .findAndRegisterModules()
                            .activateDefaultTyping(
                                this.objectMapper.getPolymorphicTypeValidator(),
                                ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
                                JsonTypeInfo.As.PROPERTY)))));
    }
}
