package com.slow3586.micromarket.productservice;


import com.slow3586.micromarket.api.order.OrderTransaction;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.product.RegisterProductRequest;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;

    public OrderTransaction processNewOrder(OrderTransaction order) {
        return order.setOrderItemList(
            order.getOrderItemList()
                .stream()
                .map(item -> item.setProduct(
                    productMapper.toDto(productRepository.findById(item.getId()).orElseThrow()))).toList());
    }

    public ProductDto registerProduct(RegisterProductRequest request) {
        return productMapper.toDto(
            productRepository.save(
                new Product()
                    .setSellerId(SecurityUtils.getPrincipalId())
                    .setName("product")
                    .setPrice(100)));
    }

    public ProductDto findProductById(UUID productId) {
        return productRepository.findById(productId)
            .map(productMapper::toDto)
            .orElseThrow();
    }
}
