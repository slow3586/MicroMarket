package com.slow3586.micromarket.balanceservice.mapper;

import com.slow3586.micromarket.api.balance.BalanceReplenishDto;
import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.balanceservice.entity.BalanceReplenish;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface BalanceReplenishMapper extends DefaultMapper<BalanceReplenishDto, BalanceReplenish> {
}
