package com.example.productapi.controller;

import com.example.productapi.common.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.example.productapi.dto.auth.ErrorResponse;
import com.example.productapi.dto.auth.LoginDto;
import com.example.productapi.dto.auth.TokenRefreshRequest;
import com.example.productapi.dto.auth.TokenResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            log.info("로그인 시도: {}", loginDto.getEmail());
            
            // 실제 인증 로직 없이 항상 인증 성공 처리
            String email = loginDto.getEmail();
            
            // 기본 권한 부여
            List<String> roles = List.of("ROLE_USER");
            
            // JWT 토큰 생성
            String accessToken = jwtProvider.createAccessToken(email, roles);
            String refreshToken = jwtProvider.createRefreshToken(email);
            
            // 리프레시 토큰을 Redis에 저장
            redisTemplate.opsForValue()
                    .set("refresh_token:" + email, refreshToken, 7, TimeUnit.DAYS);
            
            log.info("로그인 성공: {}", email);
            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("인증 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        
        try {
            // 리프레시 토큰 검증
            io.jsonwebtoken.Claims claims = jwtProvider.parseClaims(refreshToken);
            String email = claims.getSubject();
            
            // Redis에 저장된 리프레시 토큰과 비교
            String storedToken = redisTemplate.opsForValue().get("refresh_token:" + email);
            if (storedToken == null || !storedToken.equals(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("유효하지 않은 리프레시 토큰입니다."));
            }
            
            // 사용자 권한
            List<String> roles = List.of("ROLE_USER");
            
            // 새 액세스 토큰 생성
            String newAccessToken = jwtProvider.createAccessToken(email, roles);
            
            return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
        } catch (Exception e) {
            log.error("토큰 갱신 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("리프레시 토큰이 유효하지 않습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                io.jsonwebtoken.Claims claims = jwtProvider.parseClaims(token);
                String email = claims.getSubject();
                
                // Redis에서 리프레시 토큰 삭제
                redisTemplate.delete("refresh_token:" + email);
                
                return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
            }
            
            return ResponseEntity.badRequest().body(new ErrorResponse("유효하지 않은 토큰 형식입니다."));
        } catch (Exception e) {
            log.error("로그아웃 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("로그아웃 중 오류가 발생했습니다."));
        }
    }
}