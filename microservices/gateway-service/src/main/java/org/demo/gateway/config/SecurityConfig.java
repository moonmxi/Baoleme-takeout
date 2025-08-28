/**
 * 安全认证配置类
 * 提供简单的加密字符串验证机制，确保只有本项目能够访问网关接口
 * 
 * 安全特性：
 * 1. 基于加密字符串的认证机制
 * 2. 时间戳验证防止重放攻击
 * 3. 签名验证确保请求完整性
 * 4. 可配置的密钥和过期时间
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 安全认证配置和工具类
 * 提供认证令牌的生成、验证和管理功能
 */
@Slf4j
@Component
@Configuration
public class SecurityConfig {

    /**
     * 认证密钥，从配置文件读取
     */
    @Value("${gateway.security.secret-key:baoleme-gateway-secret-2025}")
    private String secretKey;

    /**
     * 令牌有效期（秒），默认5分钟
     */
    @Value("${gateway.security.token-expiry:300}")
    private long tokenExpirySeconds;

    /**
     * 是否启用安全认证
     */
    @Value("${gateway.security.enabled:true}")
    private boolean securityEnabled;

    /**
     * 已使用的令牌缓存，防止重放攻击
     */
    private final ConcurrentMap<String, Long> usedTokens = new ConcurrentHashMap<>();

    /**
     * HMAC算法名称
     */
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    /**
     * 令牌分隔符
     */
    private static final String TOKEN_SEPARATOR = ".";

    /**
     * 生成认证令牌
     * 
     * @param serviceName 服务名称
     * @param operation 操作类型
     * @return String 认证令牌
     * @throws RuntimeException 当生成令牌失败时抛出
     */
    public String generateToken(String serviceName, String operation) {
        if (!securityEnabled) {
            return "SECURITY_DISABLED";
        }

        try {
            long timestamp = Instant.now().getEpochSecond();
            String payload = serviceName + ":" + operation + ":" + timestamp;
            String signature = generateSignature(payload);
            
            String token = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8)) 
                         + TOKEN_SEPARATOR + signature;
            
            log.debug("生成认证令牌 - 服务: {}, 操作: {}, 时间戳: {}", serviceName, operation, timestamp);
            return token;
            
        } catch (Exception e) {
            log.error("生成认证令牌失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成认证令牌失败", e);
        }
    }

    /**
     * 验证认证令牌
     * 
     * @param token 认证令牌
     * @param expectedService 期望的服务名称（可选）
     * @param expectedOperation 期望的操作类型（可选）
     * @return boolean 验证结果
     */
    public boolean validateToken(String token, String expectedService, String expectedOperation) {
        if (!securityEnabled) {
            return true;
        }

        if (token == null || token.trim().isEmpty()) {
            log.warn("认证令牌为空");
            return false;
        }

        try {
            // 解析令牌
            String[] parts = token.split("\\" + TOKEN_SEPARATOR);
            if (parts.length != 2) {
                log.warn("认证令牌格式错误");
                return false;
            }

            String encodedPayload = parts[0];
            String providedSignature = parts[1];

            // 解码载荷
            String payload = new String(Base64.getDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            String[] payloadParts = payload.split(":");
            
            if (payloadParts.length != 3) {
                log.warn("认证令牌载荷格式错误");
                return false;
            }

            String serviceName = payloadParts[0];
            String operation = payloadParts[1];
            long timestamp = Long.parseLong(payloadParts[2]);

            // 验证时间戳
            long currentTime = Instant.now().getEpochSecond();
            if (currentTime - timestamp > tokenExpirySeconds) {
                log.warn("认证令牌已过期 - 时间戳: {}, 当前时间: {}, 过期时间: {}秒", timestamp, currentTime, tokenExpirySeconds);
                return false;
            }

            // 验证签名
            String expectedSignature = generateSignature(payload);
            if (!expectedSignature.equals(providedSignature)) {
                log.warn("认证令牌签名验证失败");
                return false;
            }

            // 验证服务名称（如果指定）
            if (expectedService != null && !expectedService.equals(serviceName)) {
                log.warn("服务名称不匹配 - 期望: {}, 实际: {}", expectedService, serviceName);
                return false;
            }

            // 验证操作类型（如果指定）
            if (expectedOperation != null && !expectedOperation.equals(operation)) {
                log.warn("操作类型不匹配 - 期望: {}, 实际: {}", expectedOperation, operation);
                return false;
            }

            // 防止重放攻击
            if (usedTokens.containsKey(token)) {
                log.warn("检测到重放攻击 - 令牌已被使用");
                return false;
            }

            // 记录已使用的令牌
            usedTokens.put(token, currentTime);
            
            // 清理过期的令牌记录
            cleanupExpiredTokens();

            log.debug("认证令牌验证成功 - 服务: {}, 操作: {}", serviceName, operation);
            return true;

        } catch (Exception e) {
            log.error("验证认证令牌失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 简化的令牌验证（不检查服务名称和操作类型）
     * 
     * @param token 认证令牌
     * @return boolean 验证结果
     */
    public boolean validateToken(String token) {
        return validateToken(token, null, null);
    }

    /**
     * 生成HMAC签名
     * 
     * @param data 待签名数据
     * @return String Base64编码的签名
     * @throws NoSuchAlgorithmException 算法不存在异常
     * @throws InvalidKeyException 密钥无效异常
     */
    private String generateSignature(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        
        byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature);
    }

    /**
     * 清理过期的令牌记录
     */
    private void cleanupExpiredTokens() {
        long currentTime = Instant.now().getEpochSecond();
        usedTokens.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > tokenExpirySeconds * 2
        );
    }

    /**
     * 获取认证配置信息
     * 
     * @return Map<String, Object> 配置信息
     */
    public Map<String, Object> getSecurityInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("securityEnabled", securityEnabled);
        info.put("tokenExpirySeconds", tokenExpirySeconds);
        info.put("usedTokensCount", usedTokens.size());
        info.put("algorithm", HMAC_ALGORITHM);
        return info;
    }

    /**
     * 清空已使用的令牌缓存
     */
    public void clearUsedTokens() {
        usedTokens.clear();
        log.info("已清空令牌缓存");
    }

    /**
     * 检查安全认证是否启用
     * 
     * @return boolean 是否启用
     */
    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    /**
     * 生成用于测试的令牌
     * 
     * @return String 测试令牌
     */
    public String generateTestToken() {
        return generateToken("test-service", "database-operation");
    }

    /**
     * 认证结果类
     */
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final String serviceName;
        private final String operation;
        private final long timestamp;

        public AuthResult(boolean success, String message, String serviceName, String operation, long timestamp) {
            this.success = success;
            this.message = message;
            this.serviceName = serviceName;
            this.operation = operation;
            this.timestamp = timestamp;
        }

        public AuthResult(boolean success, String message) {
            this(success, message, null, null, 0);
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getServiceName() { return serviceName; }
        public String getOperation() { return operation; }
        public long getTimestamp() { return timestamp; }
    }

    /**
     * 详细的令牌验证（返回详细结果）
     * 
     * @param token 认证令牌
     * @param expectedService 期望的服务名称
     * @param expectedOperation 期望的操作类型
     * @return AuthResult 验证结果
     */
    public AuthResult validateTokenDetailed(String token, String expectedService, String expectedOperation) {
        if (!securityEnabled) {
            return new AuthResult(true, "安全认证已禁用");
        }

        if (token == null || token.trim().isEmpty()) {
            return new AuthResult(false, "认证令牌为空");
        }

        try {
            // 解析令牌
            String[] parts = token.split("\\" + TOKEN_SEPARATOR);
            if (parts.length != 2) {
                return new AuthResult(false, "认证令牌格式错误");
            }

            String encodedPayload = parts[0];
            String providedSignature = parts[1];

            // 解码载荷
            String payload = new String(Base64.getDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            String[] payloadParts = payload.split(":");
            
            if (payloadParts.length != 3) {
                return new AuthResult(false, "认证令牌载荷格式错误");
            }

            String serviceName = payloadParts[0];
            String operation = payloadParts[1];
            long timestamp = Long.parseLong(payloadParts[2]);

            // 验证时间戳
            long currentTime = Instant.now().getEpochSecond();
            if (currentTime - timestamp > tokenExpirySeconds) {
                return new AuthResult(false, "认证令牌已过期", serviceName, operation, timestamp);
            }

            // 验证签名
            String expectedSignature = generateSignature(payload);
            if (!expectedSignature.equals(providedSignature)) {
                return new AuthResult(false, "认证令牌签名验证失败", serviceName, operation, timestamp);
            }

            // 验证服务名称（如果指定）
            if (expectedService != null && !expectedService.equals(serviceName)) {
                return new AuthResult(false, "服务名称不匹配", serviceName, operation, timestamp);
            }

            // 验证操作类型（如果指定）
            if (expectedOperation != null && !expectedOperation.equals(operation)) {
                return new AuthResult(false, "操作类型不匹配", serviceName, operation, timestamp);
            }

            // 防止重放攻击
            if (usedTokens.containsKey(token)) {
                return new AuthResult(false, "检测到重放攻击", serviceName, operation, timestamp);
            }

            // 记录已使用的令牌
            usedTokens.put(token, currentTime);
            
            // 清理过期的令牌记录
            cleanupExpiredTokens();

            return new AuthResult(true, "认证成功", serviceName, operation, timestamp);

        } catch (Exception e) {
            log.error("验证认证令牌失败: {}", e.getMessage(), e);
            return new AuthResult(false, "令牌验证异常: " + e.getMessage());
        }
    }
}