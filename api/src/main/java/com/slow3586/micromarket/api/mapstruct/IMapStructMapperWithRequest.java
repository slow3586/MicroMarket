package com.slow3586.micromarket.api.mapstruct;

public interface IMapStructMapperWithRequest<DTO, ENT, REQ> {
    DTO toDto(ENT entity);

    ENT toEntity(DTO dto);

    DTO requestToDto(REQ request);
}
