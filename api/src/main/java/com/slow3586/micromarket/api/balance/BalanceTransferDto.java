package com.slow3586.micromarket.api.balance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class BalanceTransferDto implements Serializable {
    UUID id;
    @NotNull
    UUID senderId;
    @NotNull
    UUID receiverId;
    int value;
    String status;
    Instant createdAt;
}