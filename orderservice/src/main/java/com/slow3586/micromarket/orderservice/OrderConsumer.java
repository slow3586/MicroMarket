package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.balance.BalanceConfig;
import com.slow3586.micromarket.api.balance.BalanceUpdateOrderDto;
import com.slow3586.micromarket.api.delivery.DeliveryClient;
import com.slow3586.micromarket.api.delivery.DeliveryConfig;
import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.order.OrderConfig;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.stock.StockClient;
import com.slow3586.micromarket.api.user.UserClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class OrderConsumer {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    ProductClient productClient;
    UserClient userClient;
    DeliveryClient deliveryClient;
    StockClient stockClient;

    @KafkaListener(topics = BalanceConfig.BalanceUpdateOrder.TOPIC)
    protected void processBalanceUpdateOrder(BalanceUpdateOrderDto balanceUpdateOrder) {
        if (BalanceConfig.BalanceUpdateOrder.Status.AWAITING.equals(balanceUpdateOrder.getStatus())) {
            this.processBalanceUpdateOrderAwaiting(balanceUpdateOrder);
        } else if (BalanceConfig.BalanceUpdateOrder.Status.RESERVED.equals(balanceUpdateOrder.getStatus())) {
            this.processBalanceUpdateOrderReserved(balanceUpdateOrder);
        }
    }

    protected void processBalanceUpdateOrderAwaiting(BalanceUpdateOrderDto balanceUpdateOrder) {
        orderRepository.findByIdAndStatus(
            balanceUpdateOrder.getOrderId(),
            OrderConfig.Status.CREATED
        ).ifPresent(o -> o.setStatus(OrderConfig.Status.PAYMENT_AWAITING));
    }

    protected void processBalanceUpdateOrderReserved(BalanceUpdateOrderDto balanceUpdateOrder) {
        orderRepository.findByIdAndStatus(
            balanceUpdateOrder.getOrderId(),
            OrderConfig.Status.CREATED
        ).ifPresent(o -> o.setStatus(OrderConfig.Status.PAYMENT_RESERVED));
        orderRepository.findByIdAndStatus(
            balanceUpdateOrder.getOrderId(),
            OrderConfig.Status.PAYMENT_AWAITING
        ).ifPresent(o -> o.setStatus(OrderConfig.Status.PAYMENT_RESERVED));
    }

    @KafkaListener(topics = DeliveryConfig.TOPIC)
    public void processDelivery(DeliveryDto delivery) {
        if (DeliveryConfig.Status.AWAITING.equals(delivery.getStatus())) {
            this.processDeliveryAwaiting(delivery);
        } else if (DeliveryConfig.Status.SENT.equals(delivery.getStatus())) {
            this.processDeliverySent(delivery);
        } else if (DeliveryConfig.Status.RECEIVED.equals(delivery.getStatus())) {
            this.processDeliveryReceived(delivery);
        }
    }

    public void processDeliveryAwaiting(DeliveryDto delivery) {
        orderRepository.findByIdAndStatus(
            delivery.getOrder().getId(),
            OrderConfig.Status.PAYMENT_RESERVED
        ).ifPresent(o -> o.setStatus(OrderConfig.Status.DELIVERY_AWAITING));
    }

    public void processDeliverySent(DeliveryDto delivery) {
        orderRepository.findByIdAndStatus(
            delivery.getOrder().getId(),
            OrderConfig.Status.DELIVERY_AWAITING
        ).ifPresent(o -> o.setStatus(OrderConfig.Status.DELIVERY_SENT));
    }

    public void processDeliveryReceived(DeliveryDto delivery) {
        orderRepository.findByIdAndStatus(
            delivery.getOrder().getId(),
            OrderConfig.Status.DELIVERY_SENT
        ).ifPresent(o -> o.setStatus(OrderConfig.Status.COMPLETED));
    }
}
