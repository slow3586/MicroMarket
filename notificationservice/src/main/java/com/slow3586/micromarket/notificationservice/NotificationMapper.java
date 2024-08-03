package com.slow3586.micromarket.notificationservice;

import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.notification.NotificationDto;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface NotificationMapper extends DefaultMapper<NotificationDto, Notification> {
}
