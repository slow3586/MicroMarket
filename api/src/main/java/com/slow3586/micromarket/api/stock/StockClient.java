package com.slow3586.micromarket.api.stock;

import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
    value = "stock",
    url = "${app.client.stock}/api/stock")
public interface StockClient {
    @GetMapping("{productId}")
    @Cacheable(value = "getStockSumByProductId", key = "#productId")
    long getStockSumByProductId(@PathVariable UUID productId);

    @GetMapping("order/{orderId}")
    @Cacheable(value = "getStockChangeByOrderId", key = "#orderId")
    StockUpdateOrderDto getStockOrderChangeByOrderId(@PathVariable UUID orderId);

    @PostMapping("update")
    StockUpdateDto createStockUpdate(@RequestBody @Valid CreateStockUpdateRequest request);
}
