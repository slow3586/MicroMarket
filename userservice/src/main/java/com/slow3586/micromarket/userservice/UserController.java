package com.slow3586.micromarket.userservice;


import com.slow3586.micromarket.api.mainservice.client.CustomerClient;
import com.slow3586.micromarket.api.mainservice.dto.GetQrCodeResponse;
import com.slow3586.micromarket.api.mainservice.entity.Customer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class UserController implements CustomerClient {
    UserService userService;

    @GetMapping("findById/{id}")
    public Customer findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @GetMapping("findOrCreateByTelegramId/{id}")
    public Customer findOrCreateByTelegramId(@PathVariable String id) {
        return userService.findOrCreateByTelegramId(id);
    }

    @GetMapping("findByQrCode/{qrCode}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public Customer findByQrCode(@PathVariable String qrCode) {
        return userService.findByQrCode(qrCode);
    }

    @GetMapping("getQrCode/{id}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public GetQrCodeResponse getQrCode(String telegramId) {
        return userService.getQrCode(telegramId);
    }

    @PostMapping("updateContact")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public Customer updateContact(@RequestBody Customer customer) {
        return userService.updateContact(customer);
    }
}
