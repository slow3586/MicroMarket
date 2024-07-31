package com.slow3586.micromarket.orderservice;


import com.slow3586.micromarket.api.order.NewOrderRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class OrderController {
    OrderService orderService;

    @PostMapping("create")
    @PreAuthorize("hasAnyAuthority('USER')")
    public UUID create(@RequestBody @Valid NewOrderRequest request) {
        return orderService.newOrder(request);
    }
}
