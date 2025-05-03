package com.example.productapi.mapper;

import com.example.productapi.domain.entity.OrderItem;
import com.example.productapi.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDto toOrderItemDto(OrderItem orderItem);
    
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderItem toOrderItem(OrderItemDto orderItemDto);
}
