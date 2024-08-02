package com.slow3586.micromarket.api.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegisterUserRequest {
    @NotBlank
    String login;
    @NotBlank
    @ToString.Exclude
    String password;
}
