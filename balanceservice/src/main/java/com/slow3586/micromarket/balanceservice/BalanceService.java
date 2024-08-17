package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.BalanceConfig;
import com.slow3586.micromarket.api.balance.BalanceUpdateOrderDto;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
    OrderClient orderClient;
    ProductClient productClient;

    public UUID createBalanceUpdate(final CreateBalanceUpdateRequest request) {
        return balanceUpdateRepository.save(
            new BalanceUpdate()
                .setUserId(request.getUserId())
                .setValue(request.getValue())).getId();
    }

    @Cacheable(value = BalanceConfig.CACHE_GETBALANCESUMBYUSERID, key = "#userId")
    public long getBalanceSumByUserId(final UUID userId) {
        return balanceUpdateRepository.sumAllByUserId(userId)
               + balanceUpdateOrderRepository.sumAllPositiveByUserId(userId)
               - balanceUpdateOrderRepository.sumAllNegativeByUserId(userId);
    }

    public BalanceUpdateOrderDto getBalanceUpdateOrderByOrderId(final UUID orderId) {
        return balanceUpdateOrderRepository.findByOrderId(orderId)
            .map(balanceUpdateOrderMapper::toDto)
            .orElseThrow();
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
}
