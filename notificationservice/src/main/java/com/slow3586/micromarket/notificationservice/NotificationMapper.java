package com.slow3586.micromarket.notificationservice;

import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.api.notification.NotificationDto;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface NotificationMapper extends BaseMapper<NotificationDto, Notification> {
}
