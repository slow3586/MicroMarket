package com.slow3586.micromarket.userservice;


import com.slow3586.micromarket.api.user.LoginRequest;
import com.slow3586.micromarket.api.user.RegisterUserRequest;
import com.slow3586.micromarket.api.user.UserClient;
import com.slow3586.micromarket.api.user.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class UserController implements UserClient {
    UserService userService;

    @PostMapping("register")
    public UserDto registerUser(RegisterUserRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("login")
    public String login(LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("token")
    public UserDto token(String token) {
        return userService.token(token);
    }
}
