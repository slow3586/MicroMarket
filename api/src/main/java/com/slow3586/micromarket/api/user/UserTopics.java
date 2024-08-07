package com.slow3586.micromarket.api.user;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.stream.Stream;

@Configuration
public class UserTopics {
    private static final String NAME = "user";

    public final static String TABLE = NAME + ".table";

    @Deprecated
    public KafkaAdmin.NewTopics userTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                UserTopics.TABLE
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
