package com.slow3586.micromarket.productservice;


import com.slow3586.micromarket.api.order.OrderDto;
import com.slow3586.micromarket.api.order.OrderTopics;
import com.slow3586.micromarket.api.product.CreateProductRequest;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Transaction.NEW,
        errorHandler = "orderTransactionListenerErrorHandler")
    public OrderDto processNewOrder(OrderDto order) {
        kafkaTemplate.send(
            OrderTopics.Transaction.PRODUCT,
            order.getId(),
            order.setProduct(
                productRepository.findById(
                        order.getProduct().getId())
                    .map(productMapper::toDto)
                    .orElseThrow()));
    }

    public ProductDto createProduct(CreateProductRequest request) {
        return productMapper.toDto(
            productRepository.save(
                new Product()
                    .setSellerId(SecurityUtils.getPrincipalId())
                    .setName(request.getName())
                    .setPrice(request.getPrice())));
    }

    public ProductDto findProductById(UUID productId) {
        return productRepository.findById(productId)
            .map(productMapper::toDto)
            .orElseThrow();
    }
}
