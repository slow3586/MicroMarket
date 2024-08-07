package com.slow3586.micromarket.api.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableScheduling
public class DefaultKafkaRetryTopicConfigurationSupport extends RetryTopicConfigurationSupport {
    @Override
    protected void configureBlockingRetries(BlockingRetriesConfigurer blockingRetries) {
        final FixedBackOff backOff = new FixedBackOff();
        backOff.setMaxAttempts(1);
        backOff.setInterval(1);

        blockingRetries.retryOn(Exception.class).backOff(backOff);
    }
}
