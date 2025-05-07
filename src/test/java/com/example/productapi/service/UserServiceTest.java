package com.example.productapi.service;

import com.example.productapi.domain.entity.User;
import com.example.productapi.domain.repository.UserRepository;
import com.example.productapi.dto.UserDto;
import com.example.productapi.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("pass")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("pass")
                .build();
    }

    @Test
    @DisplayName("사용자 단건 조회 성공")
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    @DisplayName("사용자 단건 조회 실패")
    void getUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(1L));
    }

    @Test
    @DisplayName("사용자 저장")
    void createUser() {
        UserDto inputDto = UserDto.builder().name("New User").email("new@example.com").build();
        User newUser = User.builder().name("New User").email("new@example.com").build();

        when(userMapper.toUser(inputDto)).thenReturn(newUser);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(inputDto);

        assertEquals("John", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 목록 조회")
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> users = userService.getAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("사용자 이메일로 조회")
    void getUserByEmail() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserByEmail("john@example.com");

        assertEquals("John", result.getName());
    }

    @Test
    @DisplayName("사용자 수정")
    void updateUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto updated = userService.updateUser(1L, userDto);

        assertEquals("John", updated.getName());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void isEmailExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        boolean exists = userService.isEmailExists("john@example.com");

        assertTrue(exists);
    }
}
