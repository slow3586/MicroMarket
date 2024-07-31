package com.slow3586.micromarket.productservice;

import com.slow3586.micromarket.api.mapstruct.IMapStructConfig;
import com.slow3586.micromarket.api.mapstruct.IMapStructMapper;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.user.UserDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface ProductMapper extends IMapStructMapper<ProductDto, Product> {
}
