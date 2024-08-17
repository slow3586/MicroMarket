package com.slow3586.micromarket.notificationservice;


import com.slow3586.micromarket.api.notification.NotificationConfig;
import com.slow3586.micromarket.api.notification.NotificationDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Transactional(transactionManager = "transactionManager")
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;

    @Cacheable(value = NotificationConfig.Notification.CACHE_USERID, key = "#userId", cacheManager = "typingCacheManager")
    public List<NotificationDto> getAllNotificationsByUserId(UUID userId) {
        return notificationRepository.findAllByUserId(userId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }
}
