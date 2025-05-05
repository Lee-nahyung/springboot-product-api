package com.example.productapi.webhook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoMessageRequest {
    private String userId;
    private String orderNumber;
    private String productName;
    private double totalAmount;
}
