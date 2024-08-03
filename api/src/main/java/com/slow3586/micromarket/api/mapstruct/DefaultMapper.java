package com.slow3586.micromarket.api.mapstruct;

public interface DefaultMapper<DTO, ENT> {
    DTO toDto(ENT entity);

    ENT toEntity(DTO dto);
}
