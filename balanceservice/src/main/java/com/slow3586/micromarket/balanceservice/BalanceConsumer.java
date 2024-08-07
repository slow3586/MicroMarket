package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.BalanceConfig;
import com.slow3586.micromarket.api.balance.BalanceUpdateDto;
import com.slow3586.micromarket.api.order.OrderClient;
import com.slow3586.micromarket.api.order.OrderConfig;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.StockConfig;
import com.slow3586.micromarket.api.stock.StockUpdateOrderDto;
import com.slow3586.micromarket.balanceservice.entity.BalanceUpdateOrder;
import com.slow3586.micromarket.balanceservice.mapper.BalanceUpdateMapper;
import com.slow3586.micromarket.balanceservice.mapper.BalanceUpdateOrderMapper;
import com.slow3586.micromarket.balanceservice.repository.BalanceUpdateOrderRepository;
import com.slow3586.micromarket.balanceservice.repository.BalanceUpdateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class BalanceConsumer {
    BalanceUpdateRepository balanceUpdateRepository;
    BalanceUpdateMapper balanceUpdateMapper;
    BalanceUpdateOrderRepository balanceUpdateOrderRepository;
    BalanceUpdateOrderMapper balanceUpdateOrderMapper;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    OrderClient orderClient;
    ProductClient productClient;
    BalanceService balanceService;

    @KafkaListener(topics = StockConfig.UpdateOrder.TOPIC)
    protected void processStockUpdateOrderReserved(final StockUpdateOrderDto stockUpdateOrder) {
        if (!stockUpdateOrder.getStatus().equals(StockConfig.UpdateOrder.Status.RESERVED)) {
            return;
        }
        if (balanceUpdateOrderRepository.existsByOrderId(stockUpdateOrder.getOrderId())) {
            return;
        }

        final OrderDto order = orderClient.getOrderById(stockUpdateOrder.getOrderId());
        final ProductDto product = productClient.getProductById(order.getProduct().getId());
        final int total = order.getQuantity() * product.getPrice();
        final long userBalance = balanceService.getBalanceSumByUserId(order.getBuyer().getId());
        final boolean enoughBalance = total <= userBalance;

        final BalanceUpdateOrder balanceUpdateOrder =
            balanceUpdateOrderRepository.save(
                new BalanceUpdateOrder()
                    .setValue(total)
                    .setStatus(enoughBalance
                        ? BalanceConfig.BalanceUpdateOrder.Status.RESERVED
                        : BalanceConfig.BalanceUpdateOrder.Status.AWAITING)
                    .setSenderId(order.getBuyer().getId())
                    .setReceiverId(product.getSeller().getId())
                    .setOrderId(order.getId())
                    .setCreatedAt(Instant.now()));

        kafkaTemplate.send(
            BalanceConfig.BalanceUpdateOrder.TOPIC,
            balanceUpdateOrder.getId(),
            balanceUpdateOrderMapper.toDto(balanceUpdateOrder));

        balanceService.resetUserCache(balanceUpdateOrder.getSenderId());
        balanceService.resetUserCache(balanceUpdateOrder.getReceiverId());
    }

    @KafkaListener(topics = OrderConfig.TOPIC)
    protected void processOrderCancelled(final OrderDto order) {
        if (OrderConfig.Status.CANCELLED.equals(order.getStatus())) {
            balanceUpdateOrderRepository.findByOrderId(order.getId())
                .ifPresent(balanceUpdateOrder -> {
                    balanceUpdateOrder.setStatus(BalanceConfig.BalanceUpdateOrder.Status.CANCELLED);
                    balanceService.resetUserCache(balanceUpdateOrder.getSenderId());
                    balanceService.resetUserCache(balanceUpdateOrder.getReceiverId());
                });
        }
    }

    @KafkaListener(topics = BalanceConfig.BalanceUpdate.TOPIC)
    protected void processBalanceUpdateCreated(final BalanceUpdateDto balanceUpdateDto) {
        balanceUpdateOrderRepository.findAllAwaitingByUserId(
            balanceUpdateDto.getUserId()
        ).forEach(balanceUpdateOrder -> {
            final long senderBalance = balanceService.getBalanceSumByUserId(balanceUpdateOrder.getSenderId());

            if (senderBalance >= balanceUpdateOrder.getValue()) {
                balanceUpdateOrder.setStatus(BalanceConfig.BalanceUpdateOrder.Status.RESERVED);

                kafkaTemplate.send(
                    BalanceConfig.BalanceUpdateOrder.TOPIC,
                    balanceUpdateOrder.getId(),
                    balanceUpdateOrderMapper.toDto(balanceUpdateOrder));

                balanceService.resetUserCache(balanceUpdateOrder.getSenderId());
                balanceService.resetUserCache(balanceUpdateOrder.getReceiverId());
            }
        });
    }

}
