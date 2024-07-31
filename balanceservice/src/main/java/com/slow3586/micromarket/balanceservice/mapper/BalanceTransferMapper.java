package com.slow3586.micromarket.balanceservice.mapper;

import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.mapstruct.IMapStructConfig;
import com.slow3586.micromarket.api.mapstruct.IMapStructMapper;
import com.slow3586.micromarket.balanceservice.entity.BalanceTransfer;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface BalanceTransferMapper extends IMapStructMapper<BalanceTransferDto, BalanceTransfer> {
}
