package com.slow3586.micromarket.api.stock;

import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.product.RegisterProductRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "stock",
    url = "${app.client.stock}/api/stock")
public interface StockClient {
    @PostMapping("add")
    StockChangeDto addStock(@RequestBody @Valid AddStockRequest request);
}
