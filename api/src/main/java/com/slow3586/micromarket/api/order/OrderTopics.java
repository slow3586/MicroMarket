
package com.slow3586.micromarket.api.order;

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
public class OrderTopics {
    private static final String NAME = "order";

    public final static String TABLE = NAME + ".table";

    public static final class Utils {
        public static final JsonSerde<OrderDto> SERDE = new JsonSerde<>(OrderDto.class);
        public static final Consumed<UUID, OrderDto> CONSUMED = Consumed.with(Serdes.UUID(), SERDE);
        public static final Produced<UUID, OrderDto> PRODUCED = Produced.with(Serdes.UUID(), SERDE);
    }

    public enum Status {
        INITIALIZATION_AWAITING,
        PAYMENT_AWAITING,
        PAYMENT_RESERVED,
        DELIVERY_AWAITING,
        DELIVERY_SENT,
        DELIVERY_RECEIVED,
        ERROR,
        CANCELLED
    }

    public final static String CREATED = NAME + ".created";

    public static final class Initialization {
        private final static String INITIALIZATION = NAME + ".initialization";
        public final static String PRODUCT = INITIALIZATION + ".product";
        public final static String USER = INITIALIZATION + ".user";
        public final static String STOCK = INITIALIZATION + ".stock";
    }

    public static final class Payment {
        private final static String PAYMENT = NAME + ".payment";
        public final static String AWAITING = PAYMENT + ".awaiting";
        public final static String RESERVED = PAYMENT + ".reserved";
    }

    public static final class Delivery {
        private final static String DELIVERY = NAME + ".delivery";
        public final static String AWAITING = DELIVERY + ".awaiting";
        public final static String SENT = DELIVERY + ".sent";
        public final static String RECEIVED = DELIVERY + ".received";
    }

    public final static String ERROR = NAME + ".error";

    @Bean
    public KafkaAdmin.NewTopics orderTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                OrderTopics.CREATED,
                OrderTopics.Initialization.PRODUCT,
                OrderTopics.Initialization.USER,
                OrderTopics.Initialization.STOCK,
                OrderTopics.Payment.AWAITING,
                OrderTopics.Payment.RESERVED,
                OrderTopics.Delivery.AWAITING,
                OrderTopics.Delivery.SENT,
                OrderTopics.Delivery.RECEIVED,
                OrderTopics.ERROR
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
