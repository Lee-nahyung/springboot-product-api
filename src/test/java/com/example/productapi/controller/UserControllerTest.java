package com.example.productapi.controller;

import com.example.productapi.common.security.JwtProvider;
import com.example.productapi.dto.UserDto;
import com.example.productapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({UserControllerTest.TestConfig.class, UserControllerTest.TestSecurityConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtProvider jwtProvider() {
            return Mockito.mock(JwtProvider.class);
        }
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeHttpRequests((authz) -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    @DisplayName("GET /api/users - 전체 사용자 조회")
    void getAllUsers() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "홍길동", "hong@test.com", "1234"),
                new UserDto(2L, "김영희", "kim@test.com", "5678")
        );
        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/users/{id} - ID로 사용자 조회")
    void getUserById() throws Exception {
        UserDto user = new UserDto(1L, "hong@test.com", "홍길동", "1234");
        given(userService.getUserById(1L)).willReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("홍길동"));
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - 이메일로 사용자 조회")
    void getUserByEmail() throws Exception {
        UserDto user = new UserDto(1L, "hong@test.com", "홍길동", "1234");
        given(userService.getUserByEmail("hong@test.com")).willReturn(user);

        mockMvc.perform(get("/api/users/email/hong@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("hong@test.com"));
    }

    @Test
    @DisplayName("GET /api/users/check-email?email=xxx - 이메일 중복 확인")
    void checkEmailExists() throws Exception {
        given(userService.isEmailExists("hong@test.com")).willReturn(true);

        mockMvc.perform(get("/api/users/check-email")
                        .param("email", "hong@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /api/users - 사용자 생성")
    void createUser() throws Exception {
        UserDto request = new UserDto(null, "홍길동", "hong@test.com", "1234");
        UserDto response = new UserDto(1L, "홍길동", "hong@test.com", "1234");

        given(userService.createUser(any(UserDto.class))).willReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - 사용자 수정")
    void updateUser() throws Exception {
        UserDto request = new UserDto(null, "홍길동", "hong@test.com", "5678");
        UserDto response = new UserDto(1L, "홍길동", "hong@test.com", "5678");

        given(userService.updateUser(any(Long.class), any(UserDto.class))).willReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").value("5678"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - 사용자 삭제")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}

