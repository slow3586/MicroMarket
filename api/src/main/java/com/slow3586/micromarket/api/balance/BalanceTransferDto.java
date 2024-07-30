package com.slow3586.micromarket.api.balance;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Value
public class BalanceTransferDto implements Serializable {
    UUID id;
    @NotNull
    UUID senderId;
    @NotNull
    UUID receiverId;
    @NotNull
    UUID orderItemId;
    int value;
    String status;
    Instant createdAt;
}