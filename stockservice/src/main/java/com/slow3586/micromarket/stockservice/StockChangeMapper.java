package com.slow3586.micromarket.stockservice;

import com.slow3586.micromarket.api.mapstruct.BaseMapperConfig;
import com.slow3586.micromarket.api.mapstruct.BaseMapper;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface StockChangeMapper extends BaseMapper<StockChangeDto, StockChange> {
}
