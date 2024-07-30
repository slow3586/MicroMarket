package com.slow3586.micromarket.stockservice;

import com.slow3586.micromarket.api.mapstruct.IMapStructConfig;
import com.slow3586.micromarket.api.mapstruct.IMapStructMapper;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import org.mapstruct.Mapper;

@Mapper(config = IMapStructConfig.class)
public interface StockChangeMapper extends IMapStructMapper<StockChangeDto, StockChange> {
}
