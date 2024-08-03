package com.slow3586.micromarket.api.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
@EnableScheduling
public class DefaultKafkaRetryTopicConfigurationSupport extends RetryTopicConfigurationSupport {

    @Override
    protected void configureBlockingRetries(BlockingRetriesConfigurer blockingRetries) {
        final ExponentialBackOff exponentialBackOff = new ExponentialBackOff();
        exponentialBackOff.setMaxAttempts(3);
        exponentialBackOff.setInitialInterval(25);
        exponentialBackOff.setMultiplier(2);

        blockingRetries
            .retryOn(Exception.class)
            .backOff(exponentialBackOff);
    }
}
