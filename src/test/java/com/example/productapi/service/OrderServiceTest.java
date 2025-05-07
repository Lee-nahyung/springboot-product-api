package com.example.productapi.service;

import com.example.productapi.domain.entity.*;
import com.example.productapi.domain.repository.*;
import com.example.productapi.dto.CreateOrderRequest;
import com.example.productapi.dto.OrderDto;
import com.example.productapi.mapper.OrderMapper;
import com.example.productapi.webhook.service.KakaoMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ProductService productService;
    @Mock private OrderMapper orderMapper;
    @Mock private KakaoMessageService kakaoMessageService;

    @InjectMocks private OrderService orderService;

    private User user;
    private Product product;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("User").email("user@example.com").build();
        product = Product.builder().id(1L).name("Product").price(1000).stock(10).build();
        order = Order.builder().id(1L).user(user).totalPrice(0).orderedAt(LocalDateTime.now()).orderItems(new ArrayList<>()).build();
        orderItem = OrderItem.builder().id(1L).order(order).product(product).quantity(2).itemPrice(2000).build();
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_success() {
        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest(1L, 2);
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenReturn(order);
        when(orderMapper.toOrderDto(any())).thenReturn(OrderDto.builder().id(1L).totalPrice(2000).build());

        OrderDto result = orderService.createOrder(request);

        assertEquals(2000, result.getTotalPrice());
        verify(productService).updateProductStock(1L, 8); // 10 - 2
        verify(kakaoMessageService).sendOrderCompletionMessage(any(), any(), any(), eq(2000.0));
    }

    @Test
    @DisplayName("모든 주문 조회")
    void getAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toOrderDto(order)).thenReturn(OrderDto.builder().id(1L).build());

        List<OrderDto> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("주문 단건 조회")
    void getOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDto(order)).thenReturn(OrderDto.builder().id(1L).build());

        OrderDto result = orderService.getOrderById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("사용자 주문 목록 조회")
    void getOrdersByUserId() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
        when(orderMapper.toOrderDto(order)).thenReturn(OrderDto.builder().id(1L).build());

        List<OrderDto> result = orderService.getOrdersByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("주문 생성 실패 - 사용자 없음")
    void createOrder_userNotFound() {
        CreateOrderRequest request = new CreateOrderRequest(99L, List.of());
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(request));
    }
}

