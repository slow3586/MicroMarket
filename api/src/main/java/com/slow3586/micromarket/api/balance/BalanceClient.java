package com.slow3586.micromarket.api.balance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "balance",
    url = "${app.client.balance}/api/balance")
public interface BalanceClient {
    @PostMapping("add")
    void addBalance(@RequestBody ReplenishBalanceRequest request);
}
