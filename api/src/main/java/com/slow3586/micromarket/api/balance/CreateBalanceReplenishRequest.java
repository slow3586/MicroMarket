package com.slow3586.micromarket.api.balance;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreateBalanceReplenishRequest {
    UUID userId;
    int value;
}
