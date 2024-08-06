package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.BalanceReplenishDto;
import com.slow3586.micromarket.api.balance.BalanceTopics;
import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.balance.CreateBalanceReplenishRequest;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.order.OrderTopics;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class BalanceService {
    BalanceReplenishRepository balanceReplenishRepository;
    BalanceReplenishMapper balanceReplenishMapper;
    BalanceTransferRepository balanceTransferRepository;
    BalanceTransferMapper balanceTransferMapper;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public UUID createBalanceReplenish(final CreateBalanceReplenishRequest request) {
        final BalanceReplenish balanceReplenish = balanceReplenishRepository.save(
            new BalanceReplenish()
                .setUserId(request.getUserId())
                .setCreatedAt(Instant.now())
                .setValue(request.getValue()));

        kafkaTemplate.executeInTransaction(
            (kafkaOperations) ->
                kafkaOperations.send(
                    BalanceTopics.Replenish.NEW,
                    balanceReplenish.getUserId(),
                    balanceReplenishMapper.toDto(balanceReplenish)));

        return balanceReplenish.getId();
    }

    @KafkaListener(topics = OrderTopics.Initialization.STOCK,
        errorHandler = "orderTransactionListenerErrorHandler")
    protected void processOrderCreated(OrderDto order) {
        if (balanceTransferRepository.existsByOrderId(order.getId())) {
            return;
        }

        final int total = order.getQuantity() * order.getProduct().getPrice();
        final long userBalance = this.getBalanceSumByUserId(order.getBuyer().getId());
        final boolean enoughBalance = total <= userBalance;

        final BalanceTransfer balanceTransfer =
            balanceTransferRepository.save(
                new BalanceTransfer()
                    .setValue(total)
                    .setStatus(enoughBalance ? BalanceTopics.Status.RESERVED : BalanceTopics.Status.AWAITING)
                    .setSenderId(order.getBuyer().getId())
                    .setReceiverId(order.getProduct().getSeller().getId())
                    .setOrderId(order.getId())
                    .setCreatedAt(Instant.now()));

        final BalanceTransferDto balanceTransferDto =
            balanceTransferMapper.toDto(balanceTransfer);

        kafkaTemplate.send(
            enoughBalance ? OrderTopics.Payment.RESERVED : OrderTopics.Payment.AWAITING,
            order.getId(),
            order.setBalanceTransfer(balanceTransferDto));
    }

    @KafkaListener(topics = OrderTopics.ERROR)
    protected void processOrderError(final OrderDto order) {
        balanceTransferRepository.findAllByOrderId(order.getId())
            .forEach(balanceTransfer -> balanceTransfer.setStatus(BalanceTopics.Status.CANCELLED));
    }

    @KafkaListener(topics = BalanceTopics.Replenish.NEW)
    protected void processBalanceReplenishCreated(final BalanceReplenishDto balanceReplenishDto) {
        balanceTransferRepository.findAllAwaitingByUserId(
            balanceReplenishDto.getUserId()
        ).forEach(balanceTransfer ->
            this.processBalanceTransferAwaitingPayment(
                balanceTransferMapper.toDto(balanceTransfer)));
    }

    @KafkaListener(topics = BalanceTopics.Payment.AWAITING)
    protected void processBalanceTransferAwaitingPayment(final BalanceTransferDto balanceTransferDto) {
        final long senderBalance = this.getBalanceSumByUserId(balanceTransferDto.getSenderId());

        if (senderBalance >= balanceTransferDto.getValue()) {
            final BalanceTransferDto balanceTransfer =
                balanceTransferRepository
                    .findById(balanceTransferDto.getId())
                    .map(t -> t.setStatus(BalanceTopics.Status.RESERVED))
                    .map(balanceTransferRepository::save)
                    .map(balanceTransferMapper::toDto)
                    .orElseThrow();

            kafkaTemplate.send(
                BalanceTopics.Payment.RESERVED,
                balanceTransfer.getId(),
                balanceTransfer);
        }
    }

    @Cacheable(value = "getBalanceSumByUserId", key = "#userId")
    public long getBalanceSumByUserId(final UUID userId) {
        return balanceReplenishRepository.sumAllByUserId(userId)
            + balanceTransferRepository.sumAllPositiveByUserId(userId)
            - balanceTransferRepository.sumAllNegativeByUserId(userId);
    }

    @Cacheable(value = "getAllBalanceChangesByUserId", key = "#userId")
    public List<Serializable> getAllBalanceChangesByUserId(UUID userId) {
        return Stream.concat(
            balanceReplenishRepository.findAllByUserId(userId)
                .stream()
                .map(balanceReplenishMapper::toDto),
            balanceTransferRepository.findAllByUserId(userId)
                .stream()
                .map(balanceTransferMapper::toDto)
        ).toList();
    }
}
