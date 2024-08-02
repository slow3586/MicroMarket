package com.slow3586.micromarket.userservice;

import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import com.slow3586.micromarket.api.user.UserDto;
import com.slow3586.micromarket.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface UserMapper extends BaseMapper<UserDto, User> {
}
