package com.example.productapi.service;

import com.example.productapi.domain.entity.Order;
import com.example.productapi.domain.entity.OrderItem;
import com.example.productapi.domain.entity.Product;
import com.example.productapi.domain.repository.OrderItemRepository;
import com.example.productapi.domain.repository.OrderRepository;
import com.example.productapi.domain.repository.ProductRepository;
import com.example.productapi.dto.OrderItemDto;
import com.example.productapi.mapper.OrderItemMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock OrderItemRepository orderItemRepository;
    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock ProductService productService;
    @Mock OrderItemMapper orderItemMapper;

    @InjectMocks OrderItemService orderItemService;

    Order order;
    Product product;
    OrderItem orderItem;

    @BeforeEach
    void setup() {
        product = Product.builder().id(1L).name("Test Product").price(1000).stock(10).build();
        order = Order.builder().id(1L).totalPrice(1000).orderItems(List.of()).build();
        orderItem = OrderItem.builder().id(1L).quantity(1).itemPrice(1000).product(product).order(order).build();
    }

    @Test
    @DisplayName("주문 ID로 주문 항목 조회")
    void getOrderItemsByOrderId() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderItemDto dto = new OrderItemDto();
        when(orderItemMapper.toOrderItemDto(any())).thenReturn(dto);

        List<OrderItemDto> result = orderItemService.getOrderItemsByOrderId(1L);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("주문 항목 ID로 조회")
    void getOrderItemById() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toOrderItemDto(any())).thenReturn(new OrderItemDto());

        OrderItemDto result = orderItemService.getOrderItemById(1L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("주문 항목 수량 변경")
    void updateOrderItemQuantity() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(any())).thenReturn(orderItem);
        when(orderItemMapper.toOrderItemDto(any())).thenReturn(new OrderItemDto());

        OrderItemDto result = orderItemService.updateOrderItemQuantity(1L, 2);
        assertNotNull(result);
        verify(productService).updateProductStock(eq(1L), eq(9));
    }

    @Test
    @DisplayName("잘못된 수량으로 수량 변경 시 예외")
    void updateOrderItemQuantity_invalidQuantity() {
        assertThrows(ResponseStatusException.class, () -> orderItemService.updateOrderItemQuantity(1L, 0));
    }
}
