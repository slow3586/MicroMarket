package com.slow3586.micromarket.api.product;

import lombok.NonNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "product",
    url = "${app.client.product}/api/product")
public interface ProductClient {
    @PostMapping("register")
    ProductDto registerProduct(@RequestBody @NonNull RegisterProductRequest request);
}
