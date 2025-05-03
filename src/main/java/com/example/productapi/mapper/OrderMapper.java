package com.example.productapi.mapper;

import com.example.productapi.domain.entity.Order;
import com.example.productapi.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    OrderDto toOrderDto(Order order);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toOrder(OrderDto orderDto);
}
