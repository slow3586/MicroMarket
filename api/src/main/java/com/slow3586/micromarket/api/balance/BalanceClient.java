package com.slow3586.micromarket.api.balance;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "balance",
    url = "${app.client.balance}/api/balance")
public interface BalanceClient {
    @GetMapping
    long getCurrentUserBalance();

    @PostMapping("add")
    void createReplenishBalance(@RequestBody @Valid CreateBalanceReplenishRequest request);
}
