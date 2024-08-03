package com.slow3586.micromarket.productservice;

import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(config = DefaultMapperConfig.class)
public interface ProductMapper extends DefaultMapper<ProductDto, Product> {
    @Override
    @Mapping(source = "sellerId", target = "seller")
    ProductDto toDto(Product entity);

    UserDto toUserDto(UUID id);
}
