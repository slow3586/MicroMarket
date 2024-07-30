package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.balance.BalanceTopics;
import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.orderservice.repository.OrderItemRepository;
import com.slow3586.micromarket.orderservice.repository.OrderRepository;
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
import org.apache.kafka.streams.kstream.Suppressed;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderStream {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    StreamsBuilder streamsBuilder;

    @PostConstruct
    public void orderStream() {
        JsonSerde<OrderTransaction> orderTransactionSerde = new JsonSerde<>(OrderTransaction.class);
        JsonSerde<BalanceTransferDto> balanceTransferDtoJsonSerde = new JsonSerde<>(BalanceTransferDto.class);

        Consumed<UUID, OrderTransaction> orderTransactionConsumed =
            Consumed.with(Serdes.UUID(), orderTransactionSerde);
        Consumed<UUID, BalanceTransferDto> balanceTransferConsumed =
            Consumed.with(Serdes.UUID(), balanceTransferDtoJsonSerde);

        KTable<UUID, OrderTransaction> orderNewTable = streamsBuilder.table(
            OrderTopics.Transaction.NEW,
            orderTransactionConsumed);
        KTable<UUID, OrderTransaction> orderWithUserTable = streamsBuilder.table(
            OrderTopics.Transaction.USER,
            orderTransactionConsumed);
        KTable<UUID, OrderTransaction> orderWithProductTable = streamsBuilder.table(
            OrderTopics.Transaction.PRODUCT,
            orderTransactionConsumed);
        KTable<UUID, OrderTransaction> orderWithStockTable = streamsBuilder.table(
            OrderTopics.Transaction.STOCK,
            orderTransactionConsumed);
        KTable<UUID, OrderTransaction> orderAwaitingBalanceTable = streamsBuilder.table(
            OrderTopics.Transaction.Awaiting.BALANCE,
            orderTransactionConsumed);
        KTable<UUID, OrderTransaction> orderWithBalanceTable = streamsBuilder.table(
            OrderTopics.Transaction.BALANCE,
            orderTransactionConsumed);
        KTable<UUID, OrderTransaction> orderAwaitingConfirmationTable = streamsBuilder.table(
            OrderTopics.Transaction.Awaiting.CONFIRMATION,
            orderTransactionConsumed);
        KTable<UUID, OrderTransaction> orderWithConfirmationTable = streamsBuilder.table(
            OrderTopics.Transaction.CONFIRMATION,
            orderTransactionConsumed);
        KTable<UUID, BalanceTransferDto> balanceTransferTable = streamsBuilder.table(
            BalanceTopics.Transfer.RESERVED,
            balanceTransferConsumed);

        // NEW TIMEOUT
        orderNewTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofSeconds(10), null))
            .leftJoin(orderAwaitingBalanceTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("TIMEOUT_NEW"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR);

        // BALANCE RECEIVED
        orderAwaitingBalanceTable
            .join(balanceTransferTable, (a, b) ->
                a.setOrderItemList(a.getOrderItemList().stream()
                    .peek(i -> {
                        if (i.getId() == b.getOrderItemId()) {
                            i.setBalanceTransferDto(b);
                        }
                    }).toList()))
            .toStream()
            .to(OrderTopics.Transaction.BALANCE);

        // PAYMENT TIMEOUT
        orderAwaitingBalanceTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofMinutes(1), null))
            .leftJoin(orderWithBalanceTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("TIMEOUT_PAYMENT"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR);

        // CONFIRM RECEIVED
        orderAwaitingConfirmationTable
            .join(orderWithConfirmationTable, (a, b) -> a)
            .toStream()
            .to(OrderTopics.Transaction.CONFIRMATION);

        // CONFIRM TIMEOUT
        orderAwaitingConfirmationTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofDays(1), null))
            .leftJoin(orderWithConfirmationTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("TIMEOUT_CONFIRMATION"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR);
    }
}
