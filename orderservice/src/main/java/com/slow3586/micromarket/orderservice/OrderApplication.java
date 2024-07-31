package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.DefaultKafkaReplyErrorChecker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.slow3586.micromarket.api")
@ComponentScan(value = {"com.slow3586.micromarket.*"})
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@EnableKafka
@EnableKafkaStreams
@EnableWebSecurity
@EnableMethodSecurity
public class OrderApplication {
    @NonFinal
    @Value("${spring.application.name}")
    String applicationName;
    DefaultKafkaReplyErrorChecker defaultKafkaReplyErrorChecker;

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
