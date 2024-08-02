package com.slow3586.micromarket.api.stock;

import com.slow3586.micromarket.api.user.UserDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.UUID;
import java.util.stream.Stream;

@Configuration
public class StockTopics {
    private static final String NAME = "stock";

    public final static String TABLE = NAME + ".table";

    public static final class Utils {
        public static final JsonSerde<StockChangeDto> SERDE = new JsonSerde<>(StockChangeDto.class);
        public static final Consumed<UUID, StockChangeDto> CONSUMED = Consumed.with(Serdes.UUID(), SERDE);
        public static final Produced<UUID, StockChangeDto> PRODUCED = Produced.with(Serdes.UUID(), SERDE);
    }

    @Deprecated
    public KafkaAdmin.NewTopics stockTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                StockTopics.TABLE
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
