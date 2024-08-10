package com.slow3586.micromarket.api.balance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public class BalanceUpdateOrderDto implements Serializable {
    UUID id;
    @NotNull
    UUID orderId;
    @NotNull
    UUID senderId;
    @NotNull
    UUID receiverId;
    long value;
    BalanceConfig.BalanceUpdateOrder.Status status;
    Instant createdAt;
}