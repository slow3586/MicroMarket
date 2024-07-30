package com.slow3586.micromarket.api.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    value = "user",
    url = "${app.client.user}/api/user")
public interface UserClient {
    @PostMapping("register")
    UserDto registerUser(RegisterUserRequest request);

    @PostMapping("login")
    String login(LoginRequest request);

    @PostMapping("token")
    UserDto token(String token);
}
