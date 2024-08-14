package com.slow3586.micromarket.api.spring;

import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultTracingConfig {
    @Bean
    public TextMapPropagator jaegerPropagator() {
        return JaegerPropagator.getInstance();
    }

    @Bean
    public OtlpGrpcSpanExporter otlpExporter(
        @Value("${management.otlp.tracing.endpoint}") String endpoint
    ) {
        return OtlpGrpcSpanExporter.builder()
            .setEndpoint(endpoint)
            .build();
    }
}
