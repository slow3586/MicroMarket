package com.slow3586.micromarket.api.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class UserDto implements Serializable {
    UUID id;
    String name;
}