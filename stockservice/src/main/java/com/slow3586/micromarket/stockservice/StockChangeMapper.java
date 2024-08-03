package com.slow3586.micromarket.stockservice;

import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.stock.StockChangeDto;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface StockChangeMapper extends DefaultMapper<StockChangeDto, StockChange> {
}
