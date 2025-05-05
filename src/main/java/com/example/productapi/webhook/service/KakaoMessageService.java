package com.example.productapi.webhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMessageService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.access-token}")
    private String accessToken;

    @Value("${kakao.api.refresh-token}")
    private String refreshToken;

    @Value("${kakao.api.client-id}")
    private String clientId;

    @Value("${kakao.api.client-secret}")
    private String clientSecret;

    public boolean sendOrderCompletionMessage(String userId, String orderNumber, String productName, double totalAmount) {
        try {
            return doSendOrderCompletionMessage(userId, orderNumber, productName, totalAmount);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.warn("⚠️ 액세스 토큰 만료됨. 재발급 시도 중...");
                if (refreshAccessToken()) {
                    try {
                        return doSendOrderCompletionMessage(userId, orderNumber, productName, totalAmount);
                    } catch (Exception retryEx) {
                        log.error("❌ 토큰 재발급 후 메시지 전송 실패", retryEx);
                    }
                } else {
                    log.error("❌ 토큰 재발급 실패");
                }
            } else {
                log.error("❌ 메시지 전송 오류: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("❌ 예외 발생", e);
        }
        return false;
    }

    private boolean doSendOrderCompletionMessage(String userId, String orderNumber, String productName, double totalAmount) {
        String apiUrl = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String templateObject = objectMapper.valueToTree(Map.of(
                "object_type", "text",
                "text", String.format("[주문완료]\n주문번호: %s\n상품명: %s\n결제금액: %.0f원", orderNumber, productName, totalAmount),
                "link", Map.of(
                        "web_url", "https://example.com",
                        "mobile_web_url", "https://example.com"
                )
        )).toString();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateObject);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        log.debug("📤 메시지 전송 요청 - URL: {}, Headers: {}, Body: {}", apiUrl, headers, templateObject);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        log.info("📥 메시지 전송 응답 - Status: {}, Body: {}", response.getStatusCode(), response.getBody());

        return response.getStatusCode().is2xxSuccessful();
    }

    public boolean refreshAccessToken() {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", refreshToken);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode json = objectMapper.readTree(response.getBody());
                this.accessToken = json.get("access_token").asText();
                if (json.has("refresh_token")) {
                    this.refreshToken = json.get("refresh_token").asText();
                    log.info("🔁 리프레시 토큰도 함께 갱신됨");
                }
                log.info("✅ 액세스 토큰 갱신 성공");
                return true;
            } else {
                log.error("❌ 액세스 토큰 갱신 실패 - Body: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("❌ 토큰 갱신 중 오류 발생", e);
        }
        return false;
    }
}