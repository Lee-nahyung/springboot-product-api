package com.example.productapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long id;
    private int quantity;
    private int itemPrice;
    private Long orderId;
    private Long productId;
    private String productName;
}
