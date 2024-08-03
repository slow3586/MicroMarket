package com.slow3586.micromarket.userservice;

import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.user.UserDto;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface UserMapper extends DefaultMapper<UserDto, User> {
}
