/**
 * JWT工具类
 * 用于生成和解析JWT令牌
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 提供JWT令牌的生成、解析和验证功能
 */
public class JwtUtils {

    /**
     * JWT密钥
     */
    private static final String SECRET = "baoleme_gateway_service_jwt_secret_key_2024";

    /**
     * JWT过期时间（24小时）
     */
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;

    /**
     * 获取签名密钥
     * 
     * @return SecretKey 签名密钥
     */
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param role 用户角色
     * @param storeId 店铺ID（可选）
     * @return String JWT令牌
     */
    public static String createToken(Long userId, String role, Long storeId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        if (storeId != null) {
            claims.put("storeId", storeId);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌
     * 
     * @param token JWT令牌
     * @return Claims 令牌声明
     * @throws Exception 解析失败时抛出异常
     */
    public static Claims parseToken(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证JWT令牌是否有效
     * 
     * @param token JWT令牌
     * @return boolean 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从JWT令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return Long 用户ID
     * @throws Exception 解析失败时抛出异常
     */
    public static Long getUserId(String token) throws Exception {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从JWT令牌中获取用户角色
     * 
     * @param token JWT令牌
     * @return String 用户角色
     * @throws Exception 解析失败时抛出异常
     */
    public static String getRole(String token) throws Exception {
        Claims claims = parseToken(token);
        return claims.get("role").toString();
    }

    /**
     * 从JWT令牌中获取店铺ID
     * 
     * @param token JWT令牌
     * @return Long 店铺ID
     * @throws Exception 解析失败时抛出异常
     */
    public static Long getStoreId(String token) throws Exception {
        Claims claims = parseToken(token);
        Object storeId = claims.get("storeId");
        return storeId != null ? Long.valueOf(storeId.toString()) : null;
    }
}