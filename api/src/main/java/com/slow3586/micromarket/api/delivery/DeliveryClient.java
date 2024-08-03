package com.slow3586.micromarket.api.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(
    value = "delivery",
    url = "${app.client.delivery}/api/delivery")
public interface DeliveryClient {
    @GetMapping("{productId}")
    DeliveryDto getDelivery(@PathVariable UUID productId);

    @PostMapping("update/sent/{deliveryId}")
    @PreAuthorize("hasAnyAuthority('API')")
    DeliveryDto updateDeliverySent(@PathVariable UUID deliveryId);

    @PostMapping("update/received/{deliveryId}")
    @PreAuthorize("hasAnyAuthority('API')")
    DeliveryDto updateDeliveryReceived(@PathVariable UUID deliveryId);
}
