/**
 * JWT工具类
 * 用于生成和解析JWT令牌
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.riderservice.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和解析JWT令牌
 */
public class JwtUtils {

    /**
     * JWT密钥
     */
    private static final String SECRET = "baoleme_secret_key_1234567890123456";
    
    /**
     * JWT过期时间（24小时）
     */
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;
    
    /**
     * 签名密钥
     */
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 创建JWT令牌
     * 
     * @param userId 用户ID
     * @param role 用户角色
     * @param username 用户名
     * @return JWT令牌字符串
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
     * 解析JWT令牌载荷
     * 
     * @param token JWT令牌
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
            return null;
        }
    }

    /**
     * 检查JWT令牌是否过期
     * 
     * @param token JWT令牌
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
            return true;
        }
    }
}