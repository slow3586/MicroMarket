package com.slow3586.micromarket.stockservice.mapper;

import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.stock.StockUpdateDto;
import com.slow3586.micromarket.stockservice.entity.StockUpdate;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface StockUpdateMapper extends DefaultMapper<StockUpdateDto, StockUpdate> {
}
