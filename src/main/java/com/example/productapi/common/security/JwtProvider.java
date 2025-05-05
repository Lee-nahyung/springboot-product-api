package com.example.productapi.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretString;
    
    private SecretKey key;

    @PostConstruct
    public void init() {
        try {
            // 문자열 비밀키가 충분히 길지 않으면 사용할 수 있는 길이로 패딩
            StringBuilder keyString = new StringBuilder(secretString);
            while (keyString.length() < 32) {  // HMAC-SHA256에는 최소 32바이트 필요
                keyString.append(secretString);
            }
            keyString = new StringBuilder(keyString.substring(0, 32));  // 정확히 32바이트로 자름
            
            this.key = Keys.hmacShaKeyFor(keyString.toString().getBytes(StandardCharsets.UTF_8));
            log.info("JWT 비밀키가 성공적으로 초기화되었습니다.");
        } catch (Exception e) {
            log.error("JWT 비밀키 초기화 중 오류 발생: {}", e.getMessage());
            // 폴백 방법으로 무작위 키 생성
            this.key = Jwts.SIG.HS256.key().build();
            log.info("대체 비밀키가 생성되었습니다.");
        }
    }
    
    public String createAccessToken(String email, List<String> roles) {
        // 15분
        long accessExp = 1000 * 60 * 15;
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .expiration(new Date(System.currentTimeMillis() + accessExp))
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String email) {
        // 7일
        long refreshExp = 1000 * 60 * 60 * 24 * 7;
        return Jwts.builder()
                .subject(email)
                .expiration(new Date(System.currentTimeMillis() + refreshExp))
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.error("JWT 토큰 파싱 오류: {}", e.getMessage());
            throw e;
        }
    }
}