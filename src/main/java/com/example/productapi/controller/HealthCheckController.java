package com.example.productapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 시스템 상태 확인을 위한 헬스 체크 컨트롤러
 * <p>
 * 이 컨트롤러는 애플리케이션의 상태를 모니터링하기 위한 엔드포인트를 제공합니다.
 * 서버 상태 확인, 로드 밸런서 연동, 모니터링 시스템 등에서 활용됩니다.
 * </p>
 */
@RestController
public class HealthCheckController {

    /**
     * 애플리케이션의 상태를 확인합니다.
     * <p>
     * 애플리케이션이 정상적으로 작동 중인지 확인하는 간단한 엔드포인트입니다.
     * 애플리케이션이 정상이면 "OK" 문자열을, 그렇지 않으면 오류 응답을 반환합니다.
     * </p>
     * 
     * @return 애플리케이션이 정상인 경우 "OK" 문자열
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}