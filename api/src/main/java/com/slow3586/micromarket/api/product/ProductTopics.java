package com.slow3586.micromarket.api.product;

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
public class ProductTopics {
    private static final String NAME = "product";

    public final static String TABLE = NAME + ".table";

    public static final class Utils {
        public static final JsonSerde<ProductDto> SERDE = new JsonSerde<>(ProductDto.class);
        public static final Consumed<UUID, ProductDto> CONSUMED = Consumed.with(Serdes.UUID(), SERDE);
        public static final Produced<UUID, ProductDto> PRODUCED = Produced.with(Serdes.UUID(), SERDE);
    }

    @Deprecated
    public KafkaAdmin.NewTopics productTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                ProductTopics.TABLE
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
