package com.slow3586.micromarket.api.user;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.UUID;
import java.util.stream.Stream;

@Configuration
public class UserTopics {
    private static final String NAME = "user";

    public final static String TABLE = NAME + ".table";

    public static final class Utils {
        public static final JsonSerde<UserDto> SERDE = new JsonSerde<>(UserDto.class);
        public static final Consumed<UUID, UserDto> CONSUMED = Consumed.with(Serdes.UUID(), SERDE);
        public static final Produced<UUID, UserDto> PRODUCED = Produced.with(Serdes.UUID(), SERDE);
    }

    @Deprecated
    public KafkaAdmin.NewTopics userTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                UserTopics.TABLE
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
