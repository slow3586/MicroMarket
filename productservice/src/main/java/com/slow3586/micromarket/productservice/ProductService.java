package com.slow3586.micromarket.productservice;


import com.slow3586.micromarket.api.product.CreateProductRequest;
import com.slow3586.micromarket.api.product.ProductConfig;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.product.ProductQuery;
import com.slow3586.micromarket.api.user.UserClient;
import com.slow3586.micromarket.api.utils.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    UserClient userClient;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public ProductDto createProduct(CreateProductRequest request) {
        return productMapper.toDto(
            productRepository.save(
                new Product()
                    .setSellerId(SecurityUtils.getPrincipalId())
                    .setName(request.getName())
                    .setPrice(request.getPrice())));
    }

    @Cacheable(value = ProductConfig.TOPIC, key = "#productId", cacheManager = "productCacheManager")
    public ProductDto getProductById(final UUID productId) {
        return productRepository.findById(productId)
            .map(productMapper::toDto)
            .orElseThrow();
    }

    public List<ProductDto> queryProducts(ProductQuery query) {
        Specification<Product> productSpecification = (product, q, builder) -> {
            List<Predicate> list = new ArrayList<>();
            if (query.getSellerId() != null) {
                list.add(builder.equal(product.get("sellerId"), query.getSellerId()));
            }
            if (query.getName() != null) {
                list.add(builder.like(product.get("name"), query.getName()));
            }
            if (query.getPriceMin() > 0) {
                list.add(builder.greaterThanOrEqualTo(product.get("price"), query.getPriceMin()));
            }
            if (query.getPriceMax() > 0 && query.getPriceMax() >= query.getPriceMin()) {
                list.add(builder.lessThanOrEqualTo(product.get("price"), query.getPriceMax()));
            }
            if (query.getStartDate() != null) {
                list.add(builder.greaterThan(product.get("startDate"), query.getStartDate()));
            }
            if (query.getEndDate() != null && (query.getStartDate() == null || query.getEndDate().isAfter(query.getStartDate()))) {
                list.add(builder.lessThan(product.get("endDate"), query.getEndDate()));
            }
            return builder.and(list.toArray(new Predicate[0]));
        };
        return productRepository.findAll(productSpecification)
            .stream()
            .map(productMapper::toDto)
            .toList();
    }
}
