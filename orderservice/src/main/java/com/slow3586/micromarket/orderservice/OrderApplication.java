package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.DefaultKafkaReplyErrorChecker;
import com.slow3586.micromarket.api.order.OrderTopics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;
import java.util.UUID;

@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.slow3586.micromarket.api")
@ComponentScan(value = {"com.slow3586.micromarket.*"})
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@EnableKafka
@EnableMethodSecurity
@EnableKafkaStreams
public class OrderApplication {
    @NonFinal
    @Value("${spring.application.name}")
    String applicationName;
    DefaultKafkaReplyErrorChecker defaultKafkaReplyErrorChecker;

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Bean
    public ReplyingKafkaTemplate<UUID, Object, Object> kafkaReplyingTemplate(
        ConcurrentKafkaListenerContainerFactory<UUID, Object> kafkaListenerContainerFactory,
        DefaultKafkaProducerFactory<UUID, Object> defaultKafkaProducerFactory,
        DefaultKafkaConsumerFactory<UUID, Object> defaultKafkaConsumerFactory
    ) {
        kafkaListenerContainerFactory.setReplyTemplate(
            new KafkaTemplate<>(defaultKafkaProducerFactory));
        kafkaListenerContainerFactory.setConsumerFactory(defaultKafkaConsumerFactory);

        ConcurrentMessageListenerContainer<UUID, Object> container =
            kafkaListenerContainerFactory
                .createContainer(
                    OrderTopics.Request.REQUEST_COMPLETED_RESPONSE,
                    OrderTopics.Request.REQUEST_CREATE_RESPONSE);

        container.getContainerProperties().setGroupId(applicationName);

        ReplyingKafkaTemplate<UUID, Object, Object> template = new ReplyingKafkaTemplate<>(
            defaultKafkaProducerFactory,
            container);
        template.setSharedReplyTopic(true);
        template.setDefaultReplyTimeout(Duration.ofSeconds(10));
        template.setReplyErrorChecker(defaultKafkaReplyErrorChecker);

        return template;
    }
}
