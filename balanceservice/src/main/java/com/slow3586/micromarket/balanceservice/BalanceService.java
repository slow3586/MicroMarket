package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.CreateBalanceUpdateRequest;
import com.slow3586.micromarket.api.order.OrderClient;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.balanceservice.entity.BalanceUpdate;
import com.slow3586.micromarket.balanceservice.mapper.BalanceUpdateMapper;
import com.slow3586.micromarket.balanceservice.mapper.BalanceUpdateOrderMapper;
import com.slow3586.micromarket.balanceservice.repository.BalanceUpdateOrderRepository;
import com.slow3586.micromarket.balanceservice.repository.BalanceUpdateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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
    BalanceUpdateRepository balanceUpdateRepository;
    BalanceUpdateMapper balanceUpdateMapper;
    BalanceUpdateOrderRepository balanceUpdateOrderRepository;
    BalanceUpdateOrderMapper balanceUpdateOrderMapper;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    OrderClient orderClient;
    ProductClient productClient;

    public UUID createBalanceUpdate(final CreateBalanceUpdateRequest request) {
        final BalanceUpdate balanceUpdate = balanceUpdateRepository.save(
            new BalanceUpdate()
                .setUserId(request.getUserId())
                .setCreatedAt(Instant.now())
                .setValue(request.getValue()));

        this.resetUserCache(request.getUserId());

        return balanceUpdate.getId();
    }

    //@Cacheable(value = "getBalanceSumByUserId", key = "#userId")
    public long getBalanceSumByUserId(final UUID userId) {
        return balanceUpdateRepository.sumAllByUserId(userId)
               + balanceUpdateOrderRepository.sumAllPositiveByUserId(userId)
               - balanceUpdateOrderRepository.sumAllNegativeByUserId(userId);
    }

    public List<Serializable> getAllBalanceChangesByUserId(UUID userId) {
        return Stream.concat(
            balanceUpdateRepository.findAllByUserId(userId)
                .stream()
                .map(balanceUpdateMapper::toDto),
            balanceUpdateOrderRepository.findAllByUserId(userId)
                .stream()
                .map(balanceUpdateOrderMapper::toDto)
        ).toList();
    }

    @Caching(evict = {
        @CacheEvict(value = "getBalanceSumByUserId", key = "#userId"),
        @CacheEvict(value = "getAllBalanceChangesByUserId", key = "#userId")
    })
    public void resetUserCache(final UUID userId) {}
}
