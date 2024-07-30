package com.slow3586.micromarket.stockservice;


import com.slow3586.micromarket.api.stock.AddStockRequest;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import com.slow3586.micromarket.api.stock.StockClient;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/product/stock")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class StockController implements StockClient {
    StockService stockService;

    @PostMapping("add")
    public StockChangeDto addStock(@RequestBody @Valid AddStockRequest request) {
        return stockService.addStock(request);
    }
}
