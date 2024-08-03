package com.slow3586.micromarket.balanceservice.mapper;

import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.balanceservice.entity.BalanceTransfer;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface BalanceTransferMapper extends DefaultMapper<BalanceTransferDto, BalanceTransfer> {
}
