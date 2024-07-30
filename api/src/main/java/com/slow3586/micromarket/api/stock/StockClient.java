package com.slow3586.micromarket.api.stock;

import feign.Headers;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
    value = "stock",
    url = "${app.client.stock}/api/stock")
@Headers("Authorization: API ${API_KEY}")
public interface StockClient {
    @GetMapping("{productId}")
    long getProductStock(@PathVariable UUID productId);

    @PostMapping("add")
    StockChangeDto addStock(@RequestBody @Valid AddStockRequest request);
}
