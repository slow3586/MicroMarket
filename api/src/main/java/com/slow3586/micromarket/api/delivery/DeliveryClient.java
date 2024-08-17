package com.slow3586.micromarket.api.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(
    value = "delivery",
    url = "${app.client.delivery}/api/delivery")
public interface DeliveryClient {
    @GetMapping("{deliveryId}")
    DeliveryDto getDeliveryById(@PathVariable UUID deliveryId);

    @PostMapping("update/sent/{deliveryId}")
    DeliveryDto updateDeliverySent(@PathVariable UUID deliveryId);

    @PostMapping("update/received/{deliveryId}")
    DeliveryDto updateDeliveryReceived(@PathVariable UUID deliveryId);
}
