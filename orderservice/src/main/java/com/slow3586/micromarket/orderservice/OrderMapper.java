package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.order.OrderDto;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface OrderMapper extends DefaultMapper<OrderDto, Order> {
}
