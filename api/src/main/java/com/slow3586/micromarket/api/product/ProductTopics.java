package com.slow3586.micromarket.api.product;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class ProductTopics {
    private static final String NAME = "product";

    public final static String TABLE = NAME + ".table";

    @Deprecated
    public KafkaAdmin.NewTopics productTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                ProductTopics.TABLE
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
