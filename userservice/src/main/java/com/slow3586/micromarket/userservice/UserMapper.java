package com.slow3586.micromarket.userservice;

import com.slow3586.micromarket.api.mapstruct.IMapStructConfig;
import com.slow3586.micromarket.api.mapstruct.IMapStructMapper;
import com.slow3586.micromarket.api.user.UserDto;
import com.slow3586.micromarket.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface UserMapper extends IMapStructMapper<UserDto, User> {
}
