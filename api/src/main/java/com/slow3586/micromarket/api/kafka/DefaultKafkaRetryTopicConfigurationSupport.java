package com.slow3586.micromarket.api.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class DefaultKafkaRetryTopicConfigurationSupport extends RetryTopicConfigurationSupport {
    @Override
    public void configureBlockingRetries(BlockingRetriesConfigurer blockingRetries) {
        final FixedBackOff backOff = new FixedBackOff();
        backOff.setMaxAttempts(2000);
        backOff.setInterval(1000);

        blockingRetries.retryOn(Exception.class).backOff(backOff);
    }
}
