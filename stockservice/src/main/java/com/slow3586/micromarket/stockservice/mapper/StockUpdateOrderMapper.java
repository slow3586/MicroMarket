package com.slow3586.micromarket.stockservice.mapper;

import com.slow3586.micromarket.api.mapstruct.DefaultMapper;
import com.slow3586.micromarket.api.mapstruct.DefaultMapperConfig;
import com.slow3586.micromarket.api.stock.StockUpdateOrderDto;
import com.slow3586.micromarket.stockservice.entity.StockUpdateOrder;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface StockUpdateOrderMapper extends DefaultMapper<StockUpdateOrderDto, StockUpdateOrder> {
}
