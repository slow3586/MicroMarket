package com.slow3586.micromarket.api.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegisterUserRequest {
    String login;
    String password;
}
