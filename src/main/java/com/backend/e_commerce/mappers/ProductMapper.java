package com.backend.e_commerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.backend.e_commerce.domain.dtos.ProductDto;
import com.backend.e_commerce.domain.entities.ProductEntity;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductDto toDto(ProductEntity productEntity);
}
