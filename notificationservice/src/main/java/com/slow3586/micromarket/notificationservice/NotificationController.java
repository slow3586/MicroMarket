package com.slow3586.micromarket.notificationservice;


import com.slow3586.micromarket.api.notification.NotificationClient;
import com.slow3586.micromarket.api.notification.NotificationDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class NotificationController implements NotificationClient {
    NotificationService notificationService;

    @Override
    @GetMapping("user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDto> getUserNotifications(@PathVariable UUID userId) {
        return notificationService.getUserNotifications(userId);
    }
}
