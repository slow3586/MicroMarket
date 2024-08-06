package com.slow3586.micromarket.api.notification;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
    value = "notification",
    url = "${app.client.notification}/api/notification")
public interface NotificationClient {
    @GetMapping("user/{userId}")
    @Cacheable(value = "getAllNotificationsByUserId", key = "#userId")
    List<NotificationDto> getAllNotificationsByUserId(@PathVariable("userId") UUID userId);
}
