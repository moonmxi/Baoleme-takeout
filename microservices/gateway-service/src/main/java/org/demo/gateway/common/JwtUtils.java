/**
 * JWT工具类 - 网关服务版本
 * 提供JWT token的创建、解析和验证功能，与admin-service保持一致
 * 用于网关层统一处理JWT令牌验证
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关服务JWT工具类
 * 与admin-service使用相同的密钥和算法，确保token兼容性
 */
@Slf4j
public class JwtUtils {

    /**
     * JWT签名密钥 - 与admin-service保持一致
     */
    private static final String SECRET = "baoleme_secret_key_1234567890123456"; // 至少 32 字符
    
    /**
     * Token过期时间（24小时）
     */
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    /**
     * 签名密钥对象
     */
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 创建JWT token
     * 
     * @param userId 用户ID
     * @param role 用户角色
     * @param username 用户名
     * @return JWT token字符串
     */
    public static String createToken(Long userId, String role, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("role", role);
        claims.put("username", username);

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRE_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT token载荷
     * 
     * @param token JWT token字符串
     * @return 载荷信息Map，解析失败返回null
     */
    public static Map<String, Object> parsePayload(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Map<String, Object> payload = new HashMap<>();
            payload.put("user_id", claims.get("user_id", Number.class));
            payload.put("role", claims.get("role", String.class));
            payload.put("username", claims.get("username", String.class));
            return payload;
        } catch (JwtException e) {
            log.warn("JWT token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查JWT token是否过期
     * 
     * @param token JWT token字符串
     * @return true表示已过期，false表示未过期
     */
    public static boolean isExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.warn("JWT token过期检查失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 验证JWT token是否有效
     * 
     * @param token JWT token字符串
     * @return true表示有效，false表示无效
     */
    public static boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return !isExpired(token);
        } catch (JwtException e) {
            log.warn("JWT token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从token中提取用户ID
     * 
     * @param token JWT token字符串
     * @return 用户ID，提取失败返回null
     */
    public static Long extractUserId(String token) {
        Map<String, Object> payload = parsePayload(token);
        if (payload != null && payload.get("user_id") != null) {
            return ((Number) payload.get("user_id")).longValue();
        }
        return null;
    }

    /**
     * 从token中提取用户角色
     * 
     * @param token JWT token字符串
     * @return 用户角色，提取失败返回null
     */
    public static String extractRole(String token) {
        Map<String, Object> payload = parsePayload(token);
        if (payload != null) {
            return (String) payload.get("role");
        }
        return null;
    }

    /**
     * 从token中提取用户名
     * 
     * @param token JWT token字符串
     * @return 用户名，提取失败返回null
     */
    public static String extractUsername(String token) {
        Map<String, Object> payload = parsePayload(token);
        if (payload != null) {
            return (String) payload.get("username");
        }
        return null;
    }
}