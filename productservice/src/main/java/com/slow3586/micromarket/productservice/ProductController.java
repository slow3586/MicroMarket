package com.slow3586.micromarket.productservice;


import com.slow3586.micromarket.api.product.CreateProductRequest;
import com.slow3586.micromarket.api.product.ProductClient;
import com.slow3586.micromarket.api.product.ProductDto;
import com.slow3586.micromarket.api.product.ProductQuery;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ProductController implements ProductClient {
    ProductService productService;

    @GetMapping("{productId}")
    @PreAuthorize("isAuthenticated()")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }

    @PostMapping("query")
    @PreAuthorize("isAuthenticated()")
    public List<ProductDto> queryProducts(@RequestBody ProductQuery query) {
        return productService.queryProducts(query);
    }

    @PostMapping("create")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ProductDto createProduct(@RequestBody @Valid CreateProductRequest request) {
        return productService.createProduct(request);
    }
}
