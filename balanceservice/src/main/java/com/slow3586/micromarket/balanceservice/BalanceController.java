package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.BalanceClient;
import com.slow3586.micromarket.api.balance.ReplenishBalanceRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/balance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class BalanceController implements BalanceClient {
    BalanceService balanceService;

    @GetMapping("{userId}")
    public long getUserBalance(@PathVariable UUID userId) {
        return balanceService.getUserBalance(userId);
    }

    @PostMapping("add")
    public void addBalance(@RequestBody @Valid ReplenishBalanceRequest request) {
        balanceService.replenishBalance(request);
    }
}
