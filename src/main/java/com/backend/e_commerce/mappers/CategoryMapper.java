package com.backend.e_commerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.backend.e_commerce.domain.dtos.CategoryDto;
import com.backend.e_commerce.domain.entities.CategoryEntity;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryEntity toEntity(CategoryDto categoryDto);

    CategoryDto toDto(CategoryEntity categoryEntity);
}