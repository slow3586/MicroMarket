package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.delivery.DeliveryDto;
import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(config = DefaultMapperConfig.class)
public interface OrderMapper extends DefaultMapper<OrderDto, Order> {
    @Override
    @Mapping(source = "buyerId", target = "buyer")
    @Mapping(source = "productId", target = "product")
    OrderDto toDto(Order entity);

    UserDto toUserDto(UUID id);
    ProductDto toProductDto(UUID id);
    DeliveryDto toDeliveryDto(UUID id);
}
