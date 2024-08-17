package com.slow3586.micromarket.deliveryservice;


import com.slow3586.micromarket.api.balance.BalanceConfig;
import com.slow3586.micromarket.api.balance.BalanceUpdateOrderDto;
import com.slow3586.micromarket.api.delivery.DeliveryConfig;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class DeliveryConsumer {
    DeliveryRepository deliveryRepository;

    @KafkaListener(topics = BalanceConfig.BalanceUpdateOrder.TOPIC, properties = BalanceConfig.BalanceUpdateOrder.TOPIC_TYPE)
    public void processBalanceUpdateOrderReserved(final BalanceUpdateOrderDto balanceUpdateOrder) {
        if (!BalanceConfig.BalanceUpdateOrder.Status.RESERVED.equals(balanceUpdateOrder.getStatus())) {
            return;
        }
        if (deliveryRepository.existsByOrderId(balanceUpdateOrder.getOrderId())) {
            return;
        }

        deliveryRepository.save(
            new Delivery()
                .setStatus(DeliveryConfig.Status.AWAITING)
                .setOrderId(balanceUpdateOrder.getOrderId()));
    }
}
