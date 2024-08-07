package com.slow3586.micromarket.api.delivery;

import com.slow3586.micromarket.api.order.OrderDto;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
public class DeliveryDto implements Serializable {
    UUID id;
    DeliveryConfig.Status status;
    OrderDto order;
    Instant createdAt;
    Instant sentAt;
    Instant receivedAt;
}