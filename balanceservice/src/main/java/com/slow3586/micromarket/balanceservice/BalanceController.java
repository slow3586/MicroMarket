package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.BalanceClient;
import com.slow3586.micromarket.api.balance.ReplenishBalanceRequest;
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
        return balanceService.getUserBalance(SecurityUtils.getPrincipalId());
    }

    @PostMapping("add")
    @PreAuthorize("hasAnyAuthority('API')")
    public void addBalance(@RequestBody @Valid ReplenishBalanceRequest request) {
        balanceService.replenishBalance(request);
    }
}
