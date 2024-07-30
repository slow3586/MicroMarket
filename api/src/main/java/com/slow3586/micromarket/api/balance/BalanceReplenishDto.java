package com.slow3586.micromarket.api.balance;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Value
public class BalanceReplenishDto implements Serializable {
    UUID id;
    @NotNull
    UUID userId;
    @NotNull
    UUID orderId;
    int value;
}