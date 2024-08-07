package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.stock.CreateStockUpdateRequest;
import com.slow3586.micromarket.api.stock.StockClient;
import com.slow3586.micromarket.api.stock.StockUpdateDto;
import com.slow3586.micromarket.api.stock.StockUpdateOrderDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/stock")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class StockController implements StockClient {
    StockService stockService;

    @GetMapping("{productId}")
    @PreAuthorize("isAuthenticated()")
    public long getStockSumByProductId(@PathVariable UUID productId) {
        return stockService.getStockSumByProductId(productId);
    }

    @GetMapping("order/{orderId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public StockUpdateOrderDto getStockOrderChangeByOrderId(@PathVariable UUID orderId) {
        return stockService.getStockOrderChangeByOrderId(orderId);
    }

    @PostMapping("update")
    @PreAuthorize("hasAnyAuthority('USER')")
    public StockUpdateDto createStockUpdate(@RequestBody @Valid CreateStockUpdateRequest request) {
        return stockService.createStockUpdate(request);
    }
}
