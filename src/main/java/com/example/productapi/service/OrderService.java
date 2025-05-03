package com.example.productapi.service;

import com.example.productapi.domain.entity.Order;
import com.example.productapi.domain.entity.OrderItem;
import com.example.productapi.domain.entity.Product;
import com.example.productapi.domain.entity.User;
import com.example.productapi.domain.repository.OrderItemRepository;
import com.example.productapi.domain.repository.OrderRepository;
import com.example.productapi.domain.repository.ProductRepository;
import com.example.productapi.domain.repository.UserRepository;
import com.example.productapi.dto.CreateOrderRequest;
import com.example.productapi.dto.OrderDto;
import com.example.productapi.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문(Order) 관련 비즈니스 로직을 처리하는 서비스
 * <p>
 * 이 서비스는 주문의 생성, 조회, 취소 등 주문과 관련된 비즈니스 로직을 구현합니다.
 * OrderRepository, OrderItemRepository, UserRepository, ProductRepository를 통해
 * 데이터베이스와 상호작용하며, OrderMapper를 사용하여 엔티티와 DTO 간의 변환을 처리합니다.
 * 주문 생성 시 상품 재고 관리도 함께 수행합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final OrderMapper orderMapper;

    /**
     * 새로운 주문을 생성합니다.
     * <p>
     * 요청에 포함된 사용자 ID와 주문 항목 정보를 바탕으로 새로운 주문을 생성합니다.
     * 각 주문 항목에 대해 상품 재고를 확인하고, 충분한 재고가 있는 경우에만 주문을 처리합니다.
     * 주문 처리 시 상품 재고가 자동으로 감소합니다.
     * </p>
     *
     * @param request 주문 생성 요청 정보(사용자 ID, 주문 항목 목록)
     * @return 생성된 주문 정보
     * @throws ResponseStatusException 사용자를 찾을 수 없는 경우 (404 Not Found) 또는 상품 재고가 부족한 경우 (400 Bad Request)
     */
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        // 신규 등록은 캐시 처리 없음

        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + request.getUserId()));

        // 빈 주문 생성
        Order order = Order.builder()
                .user(user)
                .orderedAt(LocalDateTime.now())
                .totalPrice(0)
                .orderItems(new ArrayList<>())
                .build();

        Order savedOrder = orderRepository.save(order);

        int totalPrice = 0;

        // 주문 항목 생성 및 저장
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Product not found with id: " + itemRequest.getProductId()));

            // 재고 확인
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Insufficient stock for product: " + product.getName());
            }

            int itemPrice = product.getPrice() * itemRequest.getQuantity();
            totalPrice += itemPrice;

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .itemPrice(itemPrice)
                    .build();

            orderItemRepository.save(orderItem);
            savedOrder.getOrderItems().add(orderItem);

            // 재고 감소
            productService.updateProductStock(product.getId(), product.getStock() - itemRequest.getQuantity());
        }

        // 총 가격 업데이트
        savedOrder.setTotalPrice(totalPrice);
        Order updatedOrder = orderRepository.save(savedOrder);

        return orderMapper.toOrderDto(updatedOrder);
    }

    /**
     * 모든 주문 목록을 조회합니다.
     * <p>
     * 데이터베이스에 저장된 모든 주문 정보를 가져와 DTO 리스트로 반환합니다.
     * </p>
     *
     * @return 전체 주문 목록
     */
    @Cacheable(value = "orders", key = "'all'")
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    /**
     * ID를 기준으로 특정 주문을 조회합니다.
     * <p>
     * 입력받은 ID에 해당하는 주문을 찾아 DTO로 반환합니다.
     * 해당 ID의 주문이 없는 경우 예외가 발생합니다.
     * </p>
     *
     * @param id 조회할 주문의 ID
     * @return 조회된 주문 정보
     * @throws ResponseStatusException 주문을 찾을 수 없는 경우 (404 Not Found)
     */
    @Cacheable(value = "orders", key = "#id")
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found with id: " + id));
        return orderMapper.toOrderDto(order);
    }

    /**
     * 특정 사용자의 모든 주문을 조회합니다.
     * <p>
     * 입력받은 사용자 ID에 해당하는 모든 주문을 찾아 DTO 리스트로 반환합니다.
     * 해당 ID의 사용자가 없는 경우 예외가 발생합니다.
     * </p>
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자의 주문 목록
     * @throws ResponseStatusException 사용자를 찾을 수 없는 경우 (404 Not Found)
     */
    @Cacheable(value = "orders", key = "'user:' + #userId")
    public List<OrderDto> getOrdersByUserId(Long userId) {
        // 사용자가 존재하는지 확인
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found with id: " + userId);
        }

        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    /**
     * 주문을 취소(삭제)합니다.
     * <p>
     * 입력받은 ID의 주문을 찾아 취소 처리합니다.
     * 주문에 포함된 모든 상품의 재고를 원상복구하고, 주문 정보를 삭제합니다.
     * </p>
     *
     * @param id 취소할 주문의 ID
     * @throws ResponseStatusException 주문을 찾을 수 없는 경우 (404 Not Found)
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found with id: " + id));

        // 각 주문 항목에 대한 재고 복구
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            productService.updateProductStock(product.getId(), product.getStock() + item.getQuantity());
        }

        orderRepository.deleteById(id);
    }
}
