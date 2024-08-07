package com.slow3586.micromarket.balanceservice.mapper;

import com.slow3586.micromarket.api.balance.BalanceUpdateDto;
import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.balanceservice.entity.BalanceUpdate;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface BalanceUpdateMapper extends DefaultMapper<BalanceUpdateDto, BalanceUpdate> {
}
