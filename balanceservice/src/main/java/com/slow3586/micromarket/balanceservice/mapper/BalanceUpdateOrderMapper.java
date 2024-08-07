package com.slow3586.micromarket.balanceservice.mapper;

import com.slow3586.micromarket.api.balance.BalanceUpdateOrderDto;
import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.balanceservice.entity.BalanceUpdateOrder;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface BalanceUpdateOrderMapper extends DefaultMapper<BalanceUpdateOrderDto, BalanceUpdateOrder> {
}
