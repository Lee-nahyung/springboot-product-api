package com.example.productapi.controller;

import com.example.productapi.common.security.JwtProvider;
import com.example.productapi.dto.OrderItemDto;
import com.example.productapi.service.OrderItemService;
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
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderItemController.class)
@Import({OrderItemControllerTest.TestConfig.class, OrderItemControllerTest.TestSecurityConfig.class})
class OrderItemControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private OrderItemService orderItemService;
    @Autowired private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtProvider jwtProvider() {
            return Mockito.mock(JwtProvider.class);
        }
        @Bean
        public OrderItemService orderItemService() {
            return Mockito.mock(OrderItemService.class);
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

    private OrderItemDto sampleOrderItem() {
        return OrderItemDto.builder()
                .id(1L)
                .quantity(3)
                .itemPrice(20000)
                .orderId(10L)
                .productId(100L)
                .productName("테스트 상품")
                .build();
    }

    @Test
    @DisplayName("GET /api/order-items/order/{orderId} - 주문 ID로 항목 목록 조회")
    void getOrderItemsByOrderId() throws Exception {
        given(orderItemService.getOrderItemsByOrderId(10L))
                .willReturn(List.of(sampleOrderItem()));

        mockMvc.perform(get("/api/order-items/order/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productName").value("테스트 상품"));
    }

    @Test
    @DisplayName("GET /api/order-items/{id} - 주문 항목 ID로 조회")
    void getOrderItemById() throws Exception {
        given(orderItemService.getOrderItemById(1L)).willReturn(sampleOrderItem());

        mockMvc.perform(get("/api/order-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(100))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    @DisplayName("PATCH /api/order-items/{id}/quantity - 주문 항목 수량 수정")
    void updateOrderItemQuantity() throws Exception {
        OrderItemDto updated = sampleOrderItem();
        updated.setQuantity(5);

        given(orderItemService.updateOrderItemQuantity(1L, 5)).willReturn(updated);

        mockMvc.perform(patch("/api/order-items/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("quantity", 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    @DisplayName("PATCH /api/order-items/{id}/quantity - 잘못된 요청 (quantity 없음)")
    void updateOrderItemQuantity_badRequest() throws Exception {
        mockMvc.perform(patch("/api/order-items/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/order-items/{id} - 주문 항목 삭제")
    void deleteOrderItem() throws Exception {
        mockMvc.perform(delete("/api/order-items/1"))
                .andExpect(status().isNoContent());
    }
}