package com.example.productapi.mapper;

import com.example.productapi.domain.entity.Product;
import com.example.productapi.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toProductDto(Product product);
    
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Product toProduct(ProductDto productDto);
}
