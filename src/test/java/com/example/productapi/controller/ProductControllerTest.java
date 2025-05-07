package com.example.productapi.controller;

import com.example.productapi.common.security.JwtProvider;
import com.example.productapi.dto.ProductDto;
import com.example.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({ProductControllerTest.TestConfig.class, ProductControllerTest.TestSecurityConfig.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtProvider jwtProvider() {
            return Mockito.mock(JwtProvider.class);
        }
        @Bean
        public ProductService productService() {
            return Mockito.mock(ProductService.class);
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
    @DisplayName("GET /api/products - 전체 상품 목록 조회")
    void getAllProducts() throws Exception {
        List<ProductDto> products = List.of(
                new ProductDto(1L, "노트북", 1500000, 10, 1L, "전자제품"),
                new ProductDto(2L, "스마트폰", 1000000, 20, 1L, "전자제품")
        );
        given(productService.getAllProducts()).willReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/products/{id} - 상품 ID로 조회")
    void getProductById() throws Exception {
        ProductDto product = new ProductDto(1L, "노트북", 1500000, 10, 1L, "전자제품");
        given(productService.getProductById(1L)).willReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("노트북"));
    }

    @Test
    @DisplayName("POST /api/products - 상품 생성")
    void createProduct() throws Exception {
        ProductDto request = new ProductDto(null, "태블릿", 800000, 15, 1L, "전자제품");
        ProductDto response = new ProductDto(3L, "태블릿", 800000, 15, 1L, "전자제품");

        given(productService.createProduct(any(ProductDto.class))).willReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - 상품 수정")
    void updateProduct() throws Exception {
        ProductDto request = new ProductDto(null, "노트북 Pro", 1800000, 12, 1L, "전자제품");
        ProductDto response = new ProductDto(1L, "노트북 Pro", 1800000, 12, 1L, "전자제품");

        given(productService.updateProduct(eq(1L), any(ProductDto.class))).willReturn(response);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("노트북 Pro"));
    }

    @Test
    @DisplayName("PATCH /api/products/{id}/stock - 재고 수량 업데이트")
    void updateProductStock() throws Exception {
        ProductDto updated = new ProductDto(1L, "노트북", 1500000, 30, 1L, "전자제품");
        given(productService.updateProductStock(1L, 30)).willReturn(updated);

        mockMvc.perform(patch("/api/products/1/stock")
                        .param("quantity", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(30));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - 상품 삭제")
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}