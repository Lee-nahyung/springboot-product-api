package com.example.productapi.mapper;

import com.example.productapi.domain.entity.Category;
import com.example.productapi.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    CategoryDto toCategoryDto(Category category);
    
    @Mapping(target = "products", ignore = true)
    Category toCategory(CategoryDto categoryDto);
}
