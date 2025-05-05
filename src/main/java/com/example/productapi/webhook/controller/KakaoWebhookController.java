package com.example.productapi.webhook.controller;

import com.example.productapi.webhook.dto.KakaoMessageRequest;
import com.example.productapi.webhook.dto.KakaoMessageResponse;
import com.example.productapi.webhook.service.KakaoMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook/kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoWebhookController {
    
    private final KakaoMessageService kakaoMessageService;
    
    /**
     * 주문 완료 메시지 발송 엔드포인트
     */
    @PostMapping("/send-order-completion")
    public ResponseEntity<KakaoMessageResponse> sendOrderCompletionMessage(@RequestBody KakaoMessageRequest request) {
        log.info("주문 완료 메시지 발송 요청: {}", request);
        
        // userId 파라미터 추가
        boolean success = kakaoMessageService.sendOrderCompletionMessage(
                request.getUserId(),   // 사용자 ID 추가
                request.getOrderNumber(), 
                request.getProductName(),
                request.getTotalAmount()
        );
        
        KakaoMessageResponse response = new KakaoMessageResponse();
        if (success) {
            response.setSuccess(true);
            response.setMessage("메시지 발송이 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setMessage("메시지 발송 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 토큰 리프레시 엔드포인트
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken() {
        boolean success = kakaoMessageService.refreshAccessToken();
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "토큰 갱신이 성공적으로 처리되었습니다."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "토큰 갱신 중 오류가 발생했습니다."
            ));
        }
    }
}