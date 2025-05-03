package com.example.productapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto implements Serializable {
    private Long id;
    private int totalPrice;
    private LocalDateTime orderedAt;
    private Long userId;
    private String userName;
    private List<OrderItemDto> orderItems = new ArrayList<>();
}
