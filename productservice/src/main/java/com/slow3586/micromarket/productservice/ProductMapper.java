package com.slow3586.micromarket.productservice;

import com.slow3586.micromarket.api.product.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product entity);
    Product toEntity(ProductDto dto);
}
