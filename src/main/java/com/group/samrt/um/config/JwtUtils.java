package com.group.samrt.um.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private Key key;

    // Secret đủ 512-bit, đã kiểm tra
    private final String secret = "fa2fa7502b5e4edd4d2d544ee85271ac7d4a294b35e508313dc096c152d3a8884fb60ffc0abae5edc89479ce9b6fd9ef9f9517d625b1bbe8780e5b08e54b64f9";

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Tạo JWT token
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L); // 7 ngày

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract username từ token (lấy 'sub' trong payload)
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Kiểm tra token hợp lệ hay không
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Parse claims từ token
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
