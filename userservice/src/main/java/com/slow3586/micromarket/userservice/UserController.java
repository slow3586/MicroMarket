package com.slow3586.micromarket.userservice;


import com.slow3586.micromarket.api.user.LoginRequest;
import com.slow3586.micromarket.api.user.RegisterUserRequest;
import com.slow3586.micromarket.api.user.UserClient;
import com.slow3586.micromarket.api.user.UserDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class UserController implements UserClient {
    UserService userService;

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("register")
    public UserDto registerUser(@RequestBody @Valid RegisterUserRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("login")
    public String login(@RequestBody @Valid LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("token")
    public UserDto token(String token) {
        return userService.token(token);
    }
}
