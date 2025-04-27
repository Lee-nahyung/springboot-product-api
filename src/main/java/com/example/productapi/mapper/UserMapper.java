package com.example.productapi.mapper;

import com.example.productapi.domain.entity.User;
import com.example.productapi.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);
    User toUser(UserDto userDto);
}
