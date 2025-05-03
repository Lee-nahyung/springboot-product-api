package com.example.productapi.controller;

import com.example.productapi.dto.CreateOrderRequest;
import com.example.productapi.dto.OrderDto;
import com.example.productapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주문(Order) 관련 API를 처리하는 컨트롤러
 * <p>
 * 이 컨트롤러는 주문의 생성, 조회, 취소 등 주문과 관련된 모든 작업을 담당합니다.
 * 사용자별 주문 조회 및 전체 주문 관리 기능을 제공합니다.
 * </p>
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 새로운 주문을 생성합니다.
     * 
     * @param request 주문 생성에 필요한 정보가 담긴 요청 객체
     * @return 생성된 주문 정보와 HTTP 201 Created 응답
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDto createdOrder = orderService.createOrder(request);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * 모든 주문 목록을 조회합니다.
     * 
     * @return 전체 주문 목록과 HTTP 200 OK 응답
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * ID로 특정 주문을 조회합니다.
     * 
     * @param id 조회할 주문의 ID
     * @return 주문 정보와 HTTP 200 OK 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * 특정 사용자의 모든 주문을 조회합니다.
     * 
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자의 주문 목록과 HTTP 200 OK 응답
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderDto> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * 특정 주문을 취소(삭제)합니다.
     * 
     * @param id 취소할 주문의 ID
     * @return HTTP 204 No Content 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
