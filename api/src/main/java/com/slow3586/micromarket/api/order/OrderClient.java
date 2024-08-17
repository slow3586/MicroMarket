package com.slow3586.micromarket.api.order;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
    value = "order",
    url = "${app.client.order}/api/order")
public interface OrderClient {
    @GetMapping("{orderId}")
    OrderDto getOrderById(@PathVariable UUID orderId);

    @PostMapping("create")
    OrderDto createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest);
}
