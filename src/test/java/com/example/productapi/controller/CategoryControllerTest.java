package com.example.productapi.controller;

import com.example.productapi.common.security.JwtProvider;
import com.example.productapi.dto.CategoryDto;
import com.example.productapi.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import({CategoryControllerTest.TestConfig.class, CategoryControllerTest.TestSecurityConfig.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtProvider jwtProvider() {
            return Mockito.mock(JwtProvider.class);
        }
        @Bean
        public CategoryService categoryService() {
            return Mockito.mock(CategoryService.class);
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
    @DisplayName("POST /api/categories - 카테고리 생성")
    void createCategory() throws Exception {
        CategoryDto request = new CategoryDto(null, "전자제품");
        CategoryDto response = new CategoryDto(1L, "전자제품");

        given(categoryService.createCategory(any(CategoryDto.class))).willReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/categories - 전체 카테고리 조회")
    void getAllCategories() throws Exception {
        List<CategoryDto> categories = List.of(
                new CategoryDto(1L, "전자제품"),
                new CategoryDto(2L, "가전")
        );
        given(categoryService.getAllCategories()).willReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/categories/{id} - ID로 카테고리 조회")
    void getCategoryById() throws Exception {
        CategoryDto category = new CategoryDto(1L, "전자제품");
        given(categoryService.getCategoryById(1L)).willReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("전자제품"));
    }

    @Test
    @DisplayName("PUT /api/categories/{id} - 카테고리 수정")
    void updateCategory() throws Exception {
        CategoryDto request = new CategoryDto(null, "생활용품");
        CategoryDto response = new CategoryDto(1L, "생활용품");

        given(categoryService.updateCategory(eq(1L), any(CategoryDto.class))).willReturn(response);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("생활용품"));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} - 카테고리 삭제")
    void deleteCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}