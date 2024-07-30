package com.slow3586.micromarket.balanceservice;


import com.slow3586.micromarket.api.balance.ReplenishBalanceRequest;
import com.slow3586.micromarket.api.balance.BalanceClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user/balance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class BalanceController implements BalanceClient {
    BalanceService balanceService;

    @PostMapping("add")
    public void addBalance(ReplenishBalanceRequest request){
        balanceService.replenishBalance(request);
    }
}
