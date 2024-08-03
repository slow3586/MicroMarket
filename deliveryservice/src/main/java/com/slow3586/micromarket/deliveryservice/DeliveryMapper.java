package com.slow3586.micromarket.deliveryservice;

import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface DeliveryMapper extends DefaultMapper<DeliveryDto, Delivery> {
}
