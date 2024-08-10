package com.slow3586.micromarket.productservice;

import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.product.ProductDto;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface ProductMapper extends DefaultMapper<ProductDto, Product> {
}
