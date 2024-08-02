package com.slow3586.micromarket.orderservice;

import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(config = BaseMapperConfig.class)
public interface OrderMapper extends BaseMapper<OrderDto, Order> {
    @Override
    @Mapping(source = "buyerId", target = "buyer")
    @Mapping(source = "productId", target = "product")
    OrderDto toDto(Order entity);

    UserDto toUserDto(UUID id);
    ProductDto toProductDto(UUID id);
}
