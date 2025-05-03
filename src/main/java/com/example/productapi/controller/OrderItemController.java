package com.example.productapi.controller;

import com.example.productapi.dto.OrderItemDto;
import com.example.productapi.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 주문 항목(OrderItem) 관련 API를 처리하는 컨트롤러
 * <p>
 * 이 컨트롤러는 주문에 포함된 개별 상품 항목에 대한 CRUD 작업을 담당합니다.
 * 주문 항목의 조회, 수정, 삭제 기능을 제공합니다.
 * </p>
 */
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * 특정 주문에 속한 모든 주문 항목을 조회합니다.
     * 
     * @param orderId 조회할 주문의 ID
     * @return 주문 항목 목록과 HTTP 200 OK 응답
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemDto>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemDto> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * ID로 특정 주문 항목을 조회합니다.
     * 
     * @param id 조회할 주문 항목의 ID
     * @return 주문 항목 정보와 HTTP 200 OK 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getOrderItemById(@PathVariable Long id) {
        OrderItemDto orderItem = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(orderItem);
    }

    /**
     * 주문 항목의 수량을 업데이트합니다.
     * 
     * @param id 수량을 변경할 주문 항목의 ID
     * @param payload 수량 정보를 담은 요청 본문 (key: "quantity")
     * @return 업데이트된 주문 항목 정보와 HTTP 200 OK 응답, 또는 잘못된 요청일 경우 HTTP 400 Bad Request
     */
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<OrderItemDto> updateOrderItemQuantity(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> payload) {
        
        Integer quantity = payload.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().build();
        }
        
        OrderItemDto updatedOrderItem = orderItemService.updateOrderItemQuantity(id, quantity);
        return ResponseEntity.ok(updatedOrderItem);
    }

    /**
     * 특정 주문 항목을 삭제합니다.
     * 
     * @param id 삭제할 주문 항목의 ID
     * @return HTTP 204 No Content 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
