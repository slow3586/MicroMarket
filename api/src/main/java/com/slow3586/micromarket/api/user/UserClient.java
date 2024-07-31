package com.slow3586.micromarket.api.user;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(
    value = "user",
    url = "${app.client.user}/api/user")
public interface UserClient {
    @GetMapping("{uuid}")
    UserDto findUserById(@PathVariable UUID uuid);

    @PostMapping("register")
    UserDto registerUser(RegisterUserRequest request);

    @PostMapping("login")
    String login(LoginRequest request);

    @PostMapping("token")
    UserDto token(String token);
}
