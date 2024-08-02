package com.slow3586.micromarket.productservice;

import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(config = BaseMapperConfig.class)
public interface ProductMapper extends BaseMapper<ProductDto, Product> {
    @Override
    @Mapping(source = "sellerId", target = "seller")
    ProductDto toDto(Product entity);

    UserDto toUserDto(UUID id);
}
