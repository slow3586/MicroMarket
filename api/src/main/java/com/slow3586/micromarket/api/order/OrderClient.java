package com.slow3586.micromarket.api.order;

import feign.Headers;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
    value = "order",
    url = "${app.client.order}/api/order")
public interface OrderClient {
    @PostMapping("create")
    UUID create(@RequestBody @Valid NewOrderRequest newOrderRequest);
}
