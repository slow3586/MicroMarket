package com.slow3586.micromarket.api.kafka;

import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.UUID;

@Configuration
public class DefaultKafkaConfig {
    @NonFinal
    @Value("${spring.application.name}")
    String applicationName;

    @Bean
    public JsonSerde<Object> defaultJsonSerde() {
        JsonSerde<Object> jsonSerde = new JsonSerde<>();
        jsonSerde.deserializer().trustedPackages("*");
        return jsonSerde;
    }

    @Bean
    public RetryTopicConfiguration defaultRetryTopicConfiguration(KafkaTemplate<UUID, Object> template) {
        return RetryTopicConfigurationBuilder
            .newInstance()
            .retryTopicSuffix(".retry." + applicationName)
            .dltSuffix(".dlt." + applicationName)
            .autoCreateTopics(true, 1, (short) 1)
            .fixedBackOff(1000)
            .maxAttempts(5)
            .concurrency(1)
            .retryOn(Throwable.class)
            .create(template);
    }
}
