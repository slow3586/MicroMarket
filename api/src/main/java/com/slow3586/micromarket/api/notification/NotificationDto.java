package com.slow3586.micromarket.api.notification;

import com.slow3586.micromarket.api.order.OrderDto;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
public class NotificationDto implements Serializable {
    UUID id;
    String status;
    OrderDto order;
    Instant createdAt;
    Instant sentAt;
    Instant receivedAt;
}