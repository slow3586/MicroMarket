package com.slow3586.micromarket.api.balance;

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
public class BalanceTopics {
    private static final String BALANCE = "balance";
    private static final String STATUS = ".status";

    public static class Status{
        public final static String AWAITING = "AWAITING";
        public final static String RESERVED = "RESERVED";
        public final static String COMPLETED = "COMPLETED";
        public final static String CANCELLED = "CANCELLED";
    }

    public static class Replenish {
        private final static String REPLENISH = ".replenish";
        public final static String NEW = BALANCE + REPLENISH + STATUS + ".new";

        public static final class Utils {
            public static final JsonSerde<BalanceReplenishDto> SERDE = new JsonSerde<>(BalanceReplenishDto.class);
            public static final Consumed<UUID, BalanceReplenishDto> CONSUMED = Consumed.with(Serdes.UUID(), SERDE);
            public static final Produced<UUID, BalanceReplenishDto> PRODUCED = Produced.with(Serdes.UUID(), SERDE);
        }
    }

    public static class Payment {
        private final static String PAYMENT = BALANCE + ".transfer";
        public final static String AWAITING = PAYMENT + ".awaiting";
        public final static String RESERVED = PAYMENT + ".reserved";
        public final static String COMPLETED = PAYMENT + ".completed";

        public static final class Utils {
            public static final JsonSerde<BalanceTransferDto> SERDE = new JsonSerde<>(BalanceTransferDto.class);
            public static final Consumed<UUID, BalanceTransferDto> CONSUMED = Consumed.with(Serdes.UUID(), SERDE);
            public static final Produced<UUID, BalanceTransferDto> PRODUCED = Produced.with(Serdes.UUID(), SERDE);
        }
    }

    @Bean
    public KafkaAdmin.NewTopics balanceTopicsInit() {
        return new KafkaAdmin.NewTopics(Stream.of(
                BalanceTopics.Replenish.NEW,
                Payment.COMPLETED,
                Payment.AWAITING,
                Payment.RESERVED
            ).map(t -> TopicBuilder.name(t).build())
            .toList()
            .toArray(new NewTopic[0]));
    }
}
