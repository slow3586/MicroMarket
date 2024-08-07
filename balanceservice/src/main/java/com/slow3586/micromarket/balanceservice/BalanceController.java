package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.BalanceClient;
import com.slow3586.micromarket.api.balance.CreateBalanceUpdateRequest;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/balance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class BalanceController implements BalanceClient {
    BalanceService balanceService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public long getCurrentUserBalance() {
        return balanceService.getBalanceSumByUserId(SecurityUtils.getPrincipalId());
    }

    @GetMapping("list")
    @PreAuthorize("hasAnyAuthority('USER')")
    public List<Serializable> getCurrentUserBalanceChanges() {
        return balanceService.getAllBalanceChangesByUserId(SecurityUtils.getPrincipalId());
    }

    @PostMapping("replenish/create")
    @PreAuthorize("hasAnyAuthority('API')")
    public UUID createBalanceUpdate(@RequestBody @Valid CreateBalanceUpdateRequest request) {
        return balanceService.createBalanceUpdate(request);
    }
}
