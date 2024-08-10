package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.stock.CreateStockUpdateRequest;
import com.slow3586.micromarket.api.stock.StockUpdateDto;
import com.slow3586.micromarket.api.stock.StockUpdateOrderDto;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import com.slow3586.micromarket.stockservice.entity.StockUpdate;
import com.slow3586.micromarket.stockservice.mapper.StockUpdateMapper;
import com.slow3586.micromarket.stockservice.mapper.StockUpdateOrderMapper;
import com.slow3586.micromarket.stockservice.repository.StockUpdateOrderRepository;
import com.slow3586.micromarket.stockservice.repository.StockUpdateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class StockService {
    StockUpdateRepository stockUpdateRepository;
    StockUpdateMapper stockUpdateMapper;
    StockUpdateOrderRepository stockUpdateOrderRepository;
    StockUpdateOrderMapper stockUpdateOrderMapper;
    ProductClient productClient;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public StockUpdateDto createStockUpdate(CreateStockUpdateRequest request) {
        final UUID userId = SecurityUtils.getPrincipalId();
        final ProductDto product = productClient.getProductById(request.getProductId());
        if (!userId.equals(product.getSellerId())) {
            throw new AccessDeniedException("У пользователя нет доступа к этому товару");
        }

        final StockUpdate entity = stockUpdateRepository.save(
            new StockUpdate()
                .setValue(request.getValue())
                .setProductId(request.getProductId()));

        return stockUpdateMapper.toDto(entity);
    }

    //@Cacheable(value = "getStockSumByProductId", key = "#productId")
    public long getStockSumByProductId(UUID productId) {
        return stockUpdateRepository.sumAllByProductId(productId)
            - stockUpdateOrderRepository.sumAllByProductId(productId);
    }

    //@Cacheable(value = "getStockOrderChangeByOrderId", key = "#orderId")
    public StockUpdateOrderDto getStockOrderChangeByOrderId(UUID orderId) {
        return stockUpdateOrderRepository.findByOrderId(orderId)
            .map(stockUpdateOrderMapper::toDto)
            .orElseThrow();
    }
}
