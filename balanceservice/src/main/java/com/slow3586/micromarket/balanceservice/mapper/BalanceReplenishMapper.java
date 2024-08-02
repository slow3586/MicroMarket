package com.slow3586.micromarket.balanceservice.mapper;

import com.slow3586.micromarket.api.balance.BalanceReplenishDto;
import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.balanceservice.entity.BalanceReplenish;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface BalanceReplenishMapper extends BaseMapper<BalanceReplenishDto, BalanceReplenish> {
}
