package com.slow3586.micromarket.api.mapstruct;

public interface IMapStructMapper<DTO, ENT> {
    DTO toDto(ENT entity);

    ENT toEntity(DTO dto);
}
