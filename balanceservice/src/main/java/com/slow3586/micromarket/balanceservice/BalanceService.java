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

    public void createBalanceReplenish(final CreateBalanceReplenishRequest request) {
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
    }

    @KafkaListener(topics = OrderTopics.Initialization.STOCK,
        errorHandler = "orderTransactionListenerErrorHandler")
    protected void processOrderCreated(final OrderDto order) {
        final int total = order.getQuantity() * order.getProduct().getPrice();
        final long userBalance = getUserBalance(order.getBuyer().getId());
        final boolean enoughBalance = total <= userBalance;

        final BalanceTransfer balanceTransfer =
            balanceTransferRepository.save(
                new BalanceTransfer()
                    .setValue(total)
                    .setStatus(enoughBalance ? "RESERVED" : "AWAITING")
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

    @KafkaListener(topics = OrderTopics.ERROR,
        errorHandler = "loggingKafkaListenerErrorHandler")
    protected void processOrderError(final OrderDto order) {
        balanceTransferRepository.findAllByOrderId(order.getId())
            .forEach(balanceTransfer -> balanceTransfer.setStatus("CANCELLED"));
    }

    @KafkaListener(topics = BalanceTopics.Replenish.NEW,
        errorHandler = "loggingKafkaListenerErrorHandler")
    protected void processBalanceReplenishCreated(final BalanceReplenishDto balanceReplenishDto) {
        balanceTransferRepository.findAllAwaitingByUserId(
            balanceReplenishDto.getUserId()
        ).forEach(balanceTransfer ->
            this.processBalanceTransferAwaitingPayment(
                balanceTransferMapper.toDto(balanceTransfer)));
    }

    @KafkaListener(topics = BalanceTopics.Payment.AWAITING,
        errorHandler = "loggingKafkaListenerErrorHandler")
    protected void processBalanceTransferAwaitingPayment(final BalanceTransferDto balanceTransferDto) {
        final long senderBalance = this.getUserBalance(balanceTransferDto.getSenderId());

        if (senderBalance >= balanceTransferDto.getValue()) {
            final BalanceTransferDto balanceTransfer =
                balanceTransferRepository
                    .findById(balanceTransferDto.getId())
                    .map(t -> t.setStatus("RESERVED"))
                    .map(balanceTransferRepository::save)
                    .map(balanceTransferMapper::toDto)
                    .orElseThrow();

            kafkaTemplate.send(
                BalanceTopics.Payment.RESERVED,
                balanceTransfer.getId(),
                balanceTransfer);
        }
    }

    public long getUserBalance(UUID userId) {
        return balanceReplenishRepository.sumAllByUserId(userId)
            + balanceTransferRepository.sumAllPositiveByUserId(userId)
            - balanceTransferRepository.sumAllNegativeByUserId(userId);
    }

    public List<Serializable> getUserBalanceChanges(UUID userId) {
        Stream<BalanceReplenishDto> balanceReplenishList = balanceReplenishRepository.findAllByUserId(userId)
            .stream()
            .map(balanceReplenishMapper::toDto);
        Stream<BalanceTransferDto> balanceTransferList = balanceTransferRepository.findAllByUserId(userId)
            .stream()
            .map(balanceTransferMapper::toDto);
        return Stream.concat(balanceReplenishList, balanceTransferList).toList();
    }
}
