package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.balance.BalanceTopics;
import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.delivery.DeliveryTopics;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.order.OrderTopics;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Suppressed;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@DependsOn({"orderTopics", "balanceTopics"})
public class OrderStream {
    OrderRepository orderRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    StreamsBuilder streamsBuilder;

    @PostConstruct
    public void orderStream() {
        KTable<UUID, OrderDto> orderCreatedTable = streamsBuilder.table(
            OrderTopics.CREATED,
            OrderTopics.Utils.CONSUMED);
        KTable<UUID, OrderDto> orderPaymentAwaitingTable = streamsBuilder.table(
            OrderTopics.Payment.AWAITING,
            OrderTopics.Utils.CONSUMED);
        KTable<UUID, OrderDto> orderPaymentReservedTable = streamsBuilder.table(
            OrderTopics.Payment.RESERVED,
            OrderTopics.Utils.CONSUMED);
        KTable<UUID, OrderDto> orderDeliveryAwaitingTable = streamsBuilder.table(
            OrderTopics.Delivery.AWAITING,
            OrderTopics.Utils.CONSUMED);
        KTable<UUID, OrderDto> orderDeliverySentTable = streamsBuilder.table(
            OrderTopics.Delivery.SENT,
            OrderTopics.Utils.CONSUMED);
        KTable<UUID, OrderDto> orderDeliveryReceivedTable = streamsBuilder.table(
            OrderTopics.Delivery.RECEIVED,
            OrderTopics.Utils.CONSUMED);
        KTable<UUID, BalanceTransferDto> balancePaymentReservedTable = streamsBuilder.table(
            BalanceTopics.Payment.RESERVED,
            BalanceTopics.Payment.Utils.CONSUMED);
        KTable<UUID, DeliveryDto> deliverySentTable = streamsBuilder.table(
            DeliveryTopics.Status.SENT,
            DeliveryTopics.Utils.CONSUMED);
        KTable<UUID, DeliveryDto> deliveryReceivedTable = streamsBuilder.table(
            DeliveryTopics.Status.RECEIVED,
            DeliveryTopics.Utils.CONSUMED);

        // NEW TIMEOUT
        orderCreatedTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofSeconds(10), null))
            .leftJoin(orderPaymentAwaitingTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("CREATED_TIMEOUT"))
            .toStream()
            .to(OrderTopics.ERROR, OrderTopics.Utils.PRODUCED);

        // PAYMENT RECEIVED
        orderPaymentAwaitingTable
            .join(balancePaymentReservedTable,
                order -> order.getBalanceTransfer().getId(),
                OrderDto::setBalanceTransfer)
            .toStream()
            .to(OrderTopics.Payment.RESERVED, OrderTopics.Utils.PRODUCED);

        // PAYMENT TIMEOUT
        orderPaymentAwaitingTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofMinutes(1), null))
            .leftJoin(orderPaymentReservedTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("PAYMENT_AWAITING_TIMEOUT"))
            .toStream()
            .to(OrderTopics.ERROR, OrderTopics.Utils.PRODUCED);

        // DELIVERY SENT
        orderDeliveryAwaitingTable
            .join(deliverySentTable,
                order -> order.getDelivery().getId(),
                (a, b) -> a)
            .toStream()
            .to(OrderTopics.Delivery.SENT, OrderTopics.Utils.PRODUCED);

        // DELIVERY SENT TIMEOUT
        orderDeliveryAwaitingTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofDays(1), null))
            .leftJoin(orderDeliverySentTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("DELIVERY_AWAITING_TIMEOUT"))
            .toStream()
            .to(OrderTopics.ERROR, OrderTopics.Utils.PRODUCED);

        // DELIVERY RECEIVED
        orderDeliverySentTable
            .join(deliveryReceivedTable,
                order -> order.getDelivery().getId(),
                (a, b) -> a)
            .toStream()
            .to(OrderTopics.Delivery.RECEIVED, OrderTopics.Utils.PRODUCED);

        // DELIVERY RECEIVED TIMEOUT
        orderDeliverySentTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofDays(30), null))
            .leftJoin(orderDeliveryReceivedTable, KeyValue::new)
            .filter((k, v) -> v.value == null)
            .mapValues(v -> v.key.setError("DELIVERY_SENT_TIMEOUT"))
            .toStream()
            .to(OrderTopics.ERROR, OrderTopics.Utils.PRODUCED);
    }
}
