package com.slow3586.micromarket.deliveryservice;

import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface DeliveryMapper extends BaseMapper<DeliveryDto, Delivery> {
}
