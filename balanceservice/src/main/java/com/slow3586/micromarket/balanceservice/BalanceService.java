package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.BalanceReplenishDto;
import com.slow3586.micromarket.api.balance.BalanceTopics;
import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.balance.ReplenishBalanceRequest;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import com.slow3586.micromarket.balanceservice.entity.BalanceReplenish;
import com.slow3586.micromarket.balanceservice.entity.BalanceTransfer;
import com.slow3586.micromarket.balanceservice.mapper.BalanceReplenishMapper;
import com.slow3586.micromarket.balanceservice.mapper.BalanceTransferMapper;
import com.slow3586.micromarket.balanceservice.repository.BalanceReplenishRepository;
import com.slow3586.micromarket.balanceservice.repository.BalanceTransferRepository;
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
public class BalanceService {
    BalanceReplenishRepository balanceReplenishRepository;
    BalanceReplenishMapper balanceReplenishMapper;
    BalanceTransferRepository balanceTransferRepository;
    BalanceTransferMapper balanceTransferMapper;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @Transactional(transactionManager = "transactionManager")
    public void replenishBalance(final ReplenishBalanceRequest request) {
        final BalanceReplenish balanceReplenish = balanceReplenishRepository.save(
            new BalanceReplenish()
                .setUserId(request.getUserId())
                .setValue(request.getValue()));

        kafkaTemplate.executeInTransaction((kafkaOperations) ->
            kafkaOperations.send(
                BalanceTopics.Replenish.NEW,
                request.getUserId(),
                balanceReplenish));
    }

    @KafkaListener(topics = OrderTopics.Transaction.STOCK,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    protected void processNewOrder(final OrderTransaction order) {
        order.setBuyer(order.getBuyer().setBalance(this.getUserBalance(order.getBuyer().getId())))
            .setOrderItemList(order.getOrderItemList().stream().map(item -> {
                final int total = item.getQuantity() * item.getProduct().getPrice();

                final BalanceTransfer balanceTransfer =
                    balanceTransferRepository.save(
                        new BalanceTransfer()
                            .setSenderId(order.getBuyer().getId())
                            .setReceiverId(item.getSeller().getId())
                            .setValue(total)
                            .setStatus("AWAITING")
                            .setOrderId(order.getId())
                            .setCreatedAt(Instant.now()));

                final BalanceTransferDto balanceTransferDto = balanceTransferMapper.toDto(balanceTransfer);

                kafkaTemplate.send(
                    BalanceTopics.Transfer.AWAITING,
                    balanceTransferDto.getId(),
                    balanceTransferDto);

                return item.setBalanceTransferDto(balanceTransferDto);
            }).toList());

        kafkaTemplate.send(
            OrderTopics.Transaction.Awaiting.BALANCE,
            order.getId(),
            order);
    }

    @KafkaListener(topics = BalanceTopics.Replenish.NEW,
        errorHandler = "loggingKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    protected void processNewBalanceReplenish(final BalanceReplenishDto balanceReplenishDto) {
        balanceTransferRepository.findAllAwaitingByUserId(
            balanceReplenishDto.getUserId()
        ).forEach(balanceTransfer ->
            this.processBalanceTransferAwaitingPayment(
                balanceTransferMapper.toDto(balanceTransfer)));
    }

    @KafkaListener(topics = BalanceTopics.Transfer.AWAITING,
        errorHandler = "loggingKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    protected void processBalanceTransferAwaitingPayment(final BalanceTransferDto balanceTransferDto) {
        final long senderBalance = this.getUserBalance(balanceTransferDto.getSenderId());

        if (senderBalance >= balanceTransferDto.getValue()) {
            final BalanceTransferDto newBalanceTransfer = balanceTransferRepository
                .findById(balanceTransferDto.getId())
                .map(t -> t.setStatus("RESERVED"))
                .map(balanceTransferMapper::toDto)
                .orElseThrow();

            kafkaTemplate.send(BalanceTopics.Transfer.RESERVED,
                newBalanceTransfer.getId(),
                newBalanceTransfer);
        }
    }

    public long getUserBalance(UUID userId) {
        return balanceReplenishRepository.sumAllByUserId(userId)
            + balanceTransferRepository.sumAllPositiveByUserId(userId)
            - balanceTransferRepository.sumAllNegativeByUserId(userId);
    }
}
