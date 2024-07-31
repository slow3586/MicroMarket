package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.stock.UpdateStockRequest;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import com.slow3586.micromarket.api.stock.StockClient;
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
    ProductClient productClient;

    @GetMapping("{productId}")
    @PreAuthorize("isAuthenticated()")
    public long getProductStock(@PathVariable UUID productId) {
        return stockService.getProductStock(productId);
    }

    @PostMapping("update")
    @PreAuthorize("hasAnyAuthority('USER')")
    public StockChangeDto updateStock(@RequestBody @Valid UpdateStockRequest request) {
        return stockService.updateStock(request);
    }
}
