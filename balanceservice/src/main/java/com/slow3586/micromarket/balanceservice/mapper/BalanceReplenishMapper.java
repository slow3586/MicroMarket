package com.slow3586.micromarket.balanceservice.mapper;

import com.slow3586.micromarket.api.balance.BalanceReplenishDto;
import com.slow3586.micromarket.api.mapstruct.IMapStructMapper;
import com.slow3586.micromarket.balanceservice.entity.BalanceReplenish;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceReplenishMapper extends IMapStructMapper<BalanceReplenishDto, BalanceReplenish> {
}
