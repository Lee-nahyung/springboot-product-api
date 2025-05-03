package com.example.productapi.service;

import com.example.productapi.domain.entity.Order;
import com.example.productapi.domain.entity.OrderItem;
import com.example.productapi.domain.entity.Product;
import com.example.productapi.domain.repository.OrderItemRepository;
import com.example.productapi.domain.repository.OrderRepository;
import com.example.productapi.domain.repository.ProductRepository;
import com.example.productapi.dto.OrderItemDto;
import com.example.productapi.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 주문 항목(OrderItem) 관련 비즈니스 로직을 처리하는 서비스
 * <p>
 * 이 서비스는 주문 항목의 조회, 수정, 삭제 등 주문 항목과 관련된 비즈니스 로직을 구현합니다.
 * OrderItemRepository, OrderRepository, ProductRepository를 통해 데이터베이스와 상호작용하며,
 * OrderItemMapper를 사용하여 엔티티와 DTO 간의 변환을 처리합니다.
 * 주문 항목 수정 시 상품 재고 관리도 함께 수행합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final OrderItemMapper orderItemMapper;

    /**
     * 특정 주문의 모든 주문 항목을 조회합니다.
     * <p>
     * 입력받은 주문 ID에 해당하는 모든 주문 항목을 찾아 DTO 리스트로 반환합니다.
     * 해당 ID의 주문이 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param orderId 조회할 주문의 ID
     * @return 주문에 포함된 주문 항목 목록
     * @throws ResponseStatusException 주문을 찾을 수 없는 경우 (404 Not Found)
     */
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        // 주문이 존재하는지 확인
        if (!orderRepository.existsById(orderId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order not found with id: " + orderId);
        }
        
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(orderItemMapper::toOrderItemDto)
                .toList();
    }

    /**
     * ID를 기준으로 특정 주문 항목을 조회합니다.
     * <p>
     * 입력받은 ID에 해당하는 주문 항목을 찾아 DTO로 반환합니다.
     * 해당 ID의 주문 항목이 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param id 조회할 주문 항목의 ID
     * @return 조회된 주문 항목 정보
     * @throws ResponseStatusException 주문 항목을 찾을 수 없는 경우 (404 Not Found)
     */
    public OrderItemDto getOrderItemById(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order item not found with id: " + id));
        return orderItemMapper.toOrderItemDto(orderItem);
    }

    /**
     * 주문 항목의 수량을 업데이트합니다.
     * <p>
     * 입력받은 ID의 주문 항목을 찾아 수량을 변경합니다.
     * 수량 변경에 따라 주문 항목 가격과 주문 총액이 자동으로 재계산됩니다.
     * 수량 증가 시 상품 재고를 확인하여 충분한 재고가 있는 경우에만 처리합니다.
     * </p>
     * 
     * @param id 수량을 변경할 주문 항목의 ID
     * @param quantity 변경할 수량
     * @return 업데이트된 주문 항목 정보
     * @throws ResponseStatusException 주문 항목을 찾을 수 없는 경우 (404 Not Found),
     *         수량이 음수인 경우 또는 재고가 부족한 경우 (400 Bad Request)
     */
    @Transactional
    public OrderItemDto updateOrderItemQuantity(Long id, int quantity) {
        if (quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }
        
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order item not found with id: " + id));
        
        Product product = orderItem.getProduct();
        
        // 기존 수량과 새 수량의 차이 계산
        int quantityDifference = quantity - orderItem.getQuantity();
        
        // 재고가 충분한지 확인
        if (quantityDifference > 0 && product.getStock() < quantityDifference) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Insufficient stock for product: " + product.getName());
        }
        
        // 주문 항목 수량 및 가격 업데이트
        orderItem.setQuantity(quantity);
        orderItem.setItemPrice(product.getPrice() * quantity);
        
        // 주문 총액 업데이트
        Order order = orderItem.getOrder();
        int newTotalPrice = order.getOrderItems().stream()
                .mapToInt(item -> item == orderItem ? orderItem.getItemPrice() : item.getItemPrice())
                .sum();
        order.setTotalPrice(newTotalPrice);
        
        // 재고 업데이트
        productService.updateProductStock(product.getId(), product.getStock() - quantityDifference);
        
        // 저장 및 DTO 반환
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return orderItemMapper.toOrderItemDto(updatedOrderItem);
    }

    /**
     * 특정 주문 항목을 삭제합니다.
     * <p>
     * 입력받은 ID의 주문 항목을 찾아 삭제 처리합니다.
     * 해당 주문 항목에 할당된 상품의 재고를 원상복구하고, 주문 총액을 재계산합니다.
     * </p>
     * 
     * @param id 삭제할 주문 항목의 ID
     * @throws ResponseStatusException 주문 항목을 찾을 수 없는 경우 (404 Not Found)
     */
    @Transactional
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order item not found with id: " + id));
        
        // 해당 상품의 재고 복구
        Product product = orderItem.getProduct();
        productService.updateProductStock(product.getId(), product.getStock() + orderItem.getQuantity());
        
        // 주문 총액 업데이트
        Order order = orderItem.getOrder();
        int newTotalPrice = order.getTotalPrice() - orderItem.getItemPrice();
        order.setTotalPrice(Math.max(0, newTotalPrice));
        
        // 주문에서 주문 항목 제거 및 삭제
        order.getOrderItems().remove(orderItem);
        orderItemRepository.delete(orderItem);
    }
}
