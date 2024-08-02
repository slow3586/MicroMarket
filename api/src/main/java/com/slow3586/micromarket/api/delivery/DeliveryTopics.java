package com.slow3586.micromarket.api.delivery;

import com.slow3586.micromarket.api.stock.StockChangeDto;
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
public class DeliveryTopics {
    private static final String NAME = "delivery";

    public static class Status {
        private static final String STATUS = NAME + ".status";
        public final static String AWAITING = STATUS + ".awaiting";
        public final static String SENT = STATUS + ".sent";
        public final static String RECEIVED = STATUS + ".received";
    }

    public static final class Utils {
        public static final JsonSerde<DeliveryDto> SERDE = new JsonSerde<>(DeliveryDto.class);
        public static final Consumed<UUID, DeliveryDto> CONSUMED = Consumed.with(Serdes.UUID(), SERDE);
        public static final Produced<UUID, DeliveryDto> PRODUCED = Produced.with(Serdes.UUID(), SERDE);
    }

    @Bean
    public KafkaAdmin.NewTopics deliveryTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                DeliveryTopics.Status.AWAITING,
                DeliveryTopics.Status.SENT,
                DeliveryTopics.Status.RECEIVED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
