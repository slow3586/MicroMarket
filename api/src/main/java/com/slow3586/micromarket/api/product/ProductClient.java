package com.slow3586.micromarket.api.product;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
    value = "product",
    url = "${app.client.product}/api/product")
public interface ProductClient {
    @GetMapping("{productId}")
    ProductDto findProductById(@PathVariable UUID productId);

    @PostMapping("create")
    ProductDto createProduct(@RequestBody @Valid CreateProductRequest request);
}
