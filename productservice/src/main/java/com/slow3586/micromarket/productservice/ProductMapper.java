package com.slow3586.micromarket.productservice;

import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import com.slow3586.micromarket.api.product.ProductDto;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface ProductMapper extends BaseMapper<ProductDto, Product> {
}
