package com.slow3586.micromarket.balanceservice.mapper;

import com.slow3586.micromarket.api.balance.BalanceTransferDto;
import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import com.slow3586.micromarket.balanceservice.entity.BalanceTransfer;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface BalanceTransferMapper extends BaseMapper<BalanceTransferDto, BalanceTransfer> {
}
