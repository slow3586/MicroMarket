package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.order.CreateOrderRequest;
import com.slow3586.micromarket.api.order.OrderClient;
import com.slow3586.micromarket.api.order.OrderDto;
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

import java.util.UUID;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class OrderController implements OrderClient {
    OrderService orderService;

    @GetMapping("{orderId}")
    @PreAuthorize("isAuthenticated()")
    public OrderDto getOrder(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @PostMapping("create")
    @PreAuthorize("hasAnyAuthority('USER')")
    public OrderDto createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("update/cancelled/{orderId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public OrderDto updateOrderCancelled(@PathVariable UUID orderId) {
        return orderService.updateOrderCancelled(orderId);
    }
}
