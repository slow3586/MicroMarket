package com.slow3586.micromarket.deliveryservice;


import com.slow3586.micromarket.api.delivery.DeliveryClient;
import com.slow3586.micromarket.api.delivery.DeliveryDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/delivery")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class DeliveryController implements DeliveryClient {
    DeliveryService deliveryService;

    @GetMapping("{deliveryId}")
    @PreAuthorize("isAuthenticated()")
    public DeliveryDto getDeliveryById(@PathVariable UUID deliveryId) {
        return deliveryService.getDeliveryById(deliveryId);
    }

    @GetMapping("order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public DeliveryDto getDeliveryByOrder(@PathVariable UUID orderId) {
        return deliveryService.getDeliveryByOrder(orderId);
    }

    @PostMapping("update/sent/{deliveryId}")
    @PreAuthorize("hasAnyAuthority('API')")
    public DeliveryDto updateDeliverySent(@PathVariable UUID deliveryId) {
        return deliveryService.updateDeliverySent(deliveryId);
    }

    @PostMapping("update/received/{deliveryId}")
    @PreAuthorize("hasAnyAuthority('API')")
    public DeliveryDto updateDeliveryReceived(@PathVariable UUID deliveryId) {
        return deliveryService.updateDeliveryReceived(deliveryId);
    }

    @PostMapping("update/cancelled/{deliveryId}")
    @PreAuthorize("hasAnyAuthority('API')")
    public DeliveryDto updateDeliveryCancelled(@PathVariable UUID deliveryId) {
        return deliveryService.updateDeliveryCancelled(deliveryId);
    }
}
