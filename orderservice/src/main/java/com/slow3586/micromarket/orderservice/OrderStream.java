package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.balance.BalanceTopics;
import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderDto;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@DependsOn({"orderConfig", "balanceConfig"})
public class OrderStream {
    OrderRepository orderRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    StreamsBuilder streamsBuilder;

    @PostConstruct
    public void orderStream() {
        JsonSerde<OrderDto> orderTransactionSerde = new JsonSerde<>(OrderDto.class);
        JsonSerde<BalanceTransferDto> balanceTransferDtoJsonSerde = new JsonSerde<>(BalanceTransferDto.class);

        Consumed<UUID, OrderDto> orderTransactionConsumed =
            Consumed.with(Serdes.UUID(), orderTransactionSerde);
        Consumed<UUID, BalanceTransferDto> balanceTransferConsumed =
            Consumed.with(Serdes.UUID(), balanceTransferDtoJsonSerde);
        Produced<UUID, OrderDto> orderTransactionProduced =
            Produced.with(Serdes.UUID(), orderTransactionSerde);

        KTable<UUID, OrderDto> orderNewTable = streamsBuilder.table(
            OrderTopics.Transaction.NEW,
            orderTransactionConsumed);
        KTable<UUID, OrderDto> orderWithUserTable = streamsBuilder.table(
            OrderTopics.Transaction.USER,
            orderTransactionConsumed);
        KTable<UUID, OrderDto> orderWithProductTable = streamsBuilder.table(
            OrderTopics.Transaction.PRODUCT,
            orderTransactionConsumed);
        KTable<UUID, OrderDto> orderWithStockTable = streamsBuilder.table(
            OrderTopics.Transaction.STOCK,
            orderTransactionConsumed);
        KTable<UUID, OrderDto> orderAwaitingBalanceTable = streamsBuilder.table(
            OrderTopics.Transaction.Awaiting.PAYMENT,
            orderTransactionConsumed);
        KTable<UUID, OrderDto> orderWithBalanceTable = streamsBuilder.table(
            OrderTopics.Transaction.PAYMENT,
            orderTransactionConsumed);
        KTable<UUID, OrderDto> orderAwaitingConfirmationTable = streamsBuilder.table(
            OrderTopics.Transaction.Awaiting.DELIVERY,
            orderTransactionConsumed);
        KTable<UUID, OrderDto> orderWithConfirmationTable = streamsBuilder.table(
            OrderTopics.Transaction.DELIVERY,
            orderTransactionConsumed);
        KTable<UUID, BalanceTransferDto> balanceTransferReservedTable = streamsBuilder.table(
            BalanceTopics.Transfer.RESERVED,
            balanceTransferConsumed);

        // NEW TIMEOUT
        orderNewTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofSeconds(10), null))
            .leftJoin(orderAwaitingBalanceTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("TIMEOUT_NEW"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR, orderTransactionProduced);

        // BALANCE RECEIVED
        balanceTransferReservedTable
            .join(orderAwaitingBalanceTable,
                BalanceTransferDto::getOrderId,
                (a, b) -> b.setBalanceTransfer(a))
            .toStream()
            .to(OrderTopics.Transaction.PAYMENT, orderTransactionProduced);

        // PAYMENT TIMEOUT
        orderAwaitingBalanceTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofMinutes(1), null))
            .leftJoin(orderWithBalanceTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("TIMEOUT_BALANCE"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR, orderTransactionProduced);

        // CONFIRM RECEIVED
        orderAwaitingConfirmationTable
            .join(orderWithConfirmationTable, (a, b) -> a)
            .toStream()
            .to(OrderTopics.Transaction.DELIVERY, orderTransactionProduced);

        // CONFIRM TIMEOUT
        orderAwaitingConfirmationTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofDays(1), null))
            .leftJoin(orderWithConfirmationTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("TIMEOUT_CONFIRMATION"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR, orderTransactionProduced);
    }
}
