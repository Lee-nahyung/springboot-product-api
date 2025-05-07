package com.example.productapi.controller;

import com.example.productapi.common.security.JwtProvider;
import com.example.productapi.dto.CreateOrderRequest;
import com.example.productapi.dto.OrderDto;
import com.example.productapi.dto.OrderItemDto;
import com.example.productapi.service.OrderService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import({OrderControllerTest.TestConfig.class, OrderControllerTest.TestSecurityConfig.class})
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private OrderService orderService;
    @Autowired private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtProvider jwtProvider() {
            return Mockito.mock(JwtProvider.class);
        }
        @Bean
        public OrderService orderService() {
            return Mockito.mock(OrderService.class);
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
                .id(100L)
                .quantity(2)
                .itemPrice(15000)
                .orderId(1L)
                .productId(10L)
                .productName("샘플 상품")
                .build();
    }

    @Test
    @DisplayName("POST /api/orders - 주문 생성")
    void createOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(
        ));

        OrderDto response = OrderDto.builder()
                .id(1L)
                .totalPrice(30000)
                .orderedAt(LocalDateTime.now())
                .userId(1L)
                .userName("홍길동")
                .orderItems(List.of(sampleOrderItem()))
                .build();

        given(orderService.createOrder(any(CreateOrderRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("홍길동"))
                .andExpect(jsonPath("$.orderItems[0].productName").value("샘플 상품"))
                .andExpect(jsonPath("$.orderItems[0].itemPrice").value(15000));
    }

    @Test
    @DisplayName("GET /api/orders - 전체 주문 목록 조회")
    void getAllOrders() throws Exception {
        OrderDto order = OrderDto.builder()
                .id(2L)
                .totalPrice(20000)
                .orderedAt(LocalDateTime.now())
                .userId(2L)
                .userName("김영희")
                .orderItems(List.of(sampleOrderItem()))
                .build();

        given(orderService.getAllOrders()).willReturn(List.of(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userName").value("김영희"))
                .andExpect(jsonPath("$[0].orderItems[0].productId").value(10));
    }

    @Test
    @DisplayName("GET /api/orders/{id} - 주문 ID로 조회")
    void getOrderById() throws Exception {
        OrderDto order = OrderDto.builder()
                .id(1L)
                .totalPrice(25000)
                .orderedAt(LocalDateTime.now())
                .userId(3L)
                .userName("이철수")
                .orderItems(List.of(sampleOrderItem()))
                .build();

        given(orderService.getOrderById(1L)).willReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("이철수"))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2));
    }

    @Test
    @DisplayName("GET /api/orders/user/{userId} - 사용자 ID로 주문 조회")
    void getOrdersByUserId() throws Exception {
        OrderDto order = OrderDto.builder()
                .id(3L)
                .totalPrice(18000)
                .orderedAt(LocalDateTime.now())
                .userId(5L)
                .userName("박영수")
                .orderItems(List.of(sampleOrderItem()))
                .build();

        given(orderService.getOrdersByUserId(5L)).willReturn(List.of(order));

        mockMvc.perform(get("/api/orders/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userName").value("박영수"));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - 주문 취소")
    void cancelOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }
}