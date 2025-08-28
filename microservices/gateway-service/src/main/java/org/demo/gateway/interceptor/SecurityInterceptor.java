/**
 * 安全认证拦截器
 * 在HTTP请求处理前进行认证验证，确保只有授权的请求能够访问网关接口
 * 
 * 拦截特性：
 * 1. 请求头认证验证
 * 2. 白名单路径支持
 * 3. 详细的认证日志
 * 4. 统一的错误响应格式
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全认证拦截器
 * 拦截所有HTTP请求并进行安全认证验证
 */
@Slf4j
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    /**
     * 安全配置
     */
    @Autowired
    private SecurityConfig securityConfig;

    /**
     * JSON对象映射器
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 认证令牌请求头名称
     */
    private static final String AUTH_HEADER = "X-Gateway-Auth";

    /**
     * 服务名称请求头名称
     */
    private static final String SERVICE_HEADER = "X-Service-Name";

    /**
     * 操作类型请求头名称
     */
    private static final String OPERATION_HEADER = "X-Operation-Type";

    /**
     * 白名单路径（不需要认证的路径）
     */
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
        "/api/database/health",
        "/actuator/health",
        "/actuator/info",
        "/swagger-ui",
        "/v3/api-docs",
        "/favicon.ico"
    );

    /**
     * 请求预处理
     * 在控制器方法执行前进行认证验证
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return boolean 是否继续处理请求
     * @throws Exception 处理异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIpAddress(request);
        
        log.debug("收到请求 - 路径: {}, 方法: {}, 客户端IP: {}", requestPath, method, clientIp);

        // 检查是否在白名单中
        if (isWhitelistPath(requestPath)) {
            log.debug("白名单路径，跳过认证 - 路径: {}", requestPath);
            return true;
        }

        // 检查安全认证是否启用
        if (!securityConfig.isSecurityEnabled()) {
            log.debug("安全认证已禁用，跳过验证");
            return true;
        }

        // 获取认证信息
        String authToken = request.getHeader(AUTH_HEADER);
        String serviceName = request.getHeader(SERVICE_HEADER);
        String operationType = request.getHeader(OPERATION_HEADER);

        // 验证认证令牌
        if (authToken == null || authToken.trim().isEmpty()) {
            log.warn("缺少认证令牌 - 路径: {}, 客户端IP: {}", requestPath, clientIp);
            sendAuthErrorResponse(response, "缺少认证令牌", HttpStatus.UNAUTHORIZED);
            return false;
        }

        // 执行详细的令牌验证
        SecurityConfig.AuthResult authResult = securityConfig.validateTokenDetailed(authToken, serviceName, operationType);
        
        if (!authResult.isSuccess()) {
            log.warn("认证失败 - 路径: {}, 客户端IP: {}, 原因: {}", requestPath, clientIp, authResult.getMessage());
            sendAuthErrorResponse(response, authResult.getMessage(), HttpStatus.FORBIDDEN);
            return false;
        }

        // 记录成功的认证
        log.info("认证成功 - 路径: {}, 服务: {}, 操作: {}, 客户端IP: {}", 
                requestPath, authResult.getServiceName(), authResult.getOperation(), clientIp);

        // 将认证信息添加到请求属性中，供后续处理使用
        request.setAttribute("auth.serviceName", authResult.getServiceName());
        request.setAttribute("auth.operation", authResult.getOperation());
        request.setAttribute("auth.timestamp", authResult.getTimestamp());
        request.setAttribute("auth.clientIp", clientIp);

        return true;
    }

    /**
     * 请求完成后处理
     * 记录请求处理时间等信息
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @param ex 异常（如果有）
     * @throws Exception 处理异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestPath = request.getRequestURI();
        String serviceName = (String) request.getAttribute("auth.serviceName");
        int statusCode = response.getStatus();
        
        if (serviceName != null) {
            log.debug("请求完成 - 路径: {}, 服务: {}, 状态码: {}", requestPath, serviceName, statusCode);
        }
        
        if (ex != null) {
            log.error("请求处理异常 - 路径: {}, 异常: {}", requestPath, ex.getMessage(), ex);
        }
    }

    /**
     * 检查路径是否在白名单中
     * 
     * @param path 请求路径
     * @return boolean 是否在白名单中
     */
    private boolean isWhitelistPath(String path) {
        return WHITELIST_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求
     * @return String 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 发送认证错误响应
     * 
     * @param response HTTP响应
     * @param message 错误消息
     * @param status HTTP状态码
     * @throws IOException IO异常
     */
    private void sendAuthErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("code", status.value());
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("path", "/api/database");
        
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * 获取拦截器统计信息
     * 
     * @return Map<String, Object> 统计信息
     */
    public Map<String, Object> getInterceptorStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("authHeader", AUTH_HEADER);
        stats.put("serviceHeader", SERVICE_HEADER);
        stats.put("operationHeader", OPERATION_HEADER);
        stats.put("whitelistPaths", WHITELIST_PATHS);
        stats.put("securityEnabled", securityConfig.isSecurityEnabled());
        return stats;
    }

    /**
     * 认证统计信息类
     */
    public static class AuthStats {
        private long totalRequests = 0;
        private long successfulAuth = 0;
        private long failedAuth = 0;
        private long whitelistRequests = 0;
        
        // 增加统计计数的方法
        public void incrementTotalRequests() { totalRequests++; }
        public void incrementSuccessfulAuth() { successfulAuth++; }
        public void incrementFailedAuth() { failedAuth++; }
        public void incrementWhitelistRequests() { whitelistRequests++; }
        
        // Getters
        public long getTotalRequests() { return totalRequests; }
        public long getSuccessfulAuth() { return successfulAuth; }
        public long getFailedAuth() { return failedAuth; }
        public long getWhitelistRequests() { return whitelistRequests; }
        
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("totalRequests", totalRequests);
            map.put("successfulAuth", successfulAuth);
            map.put("failedAuth", failedAuth);
            map.put("whitelistRequests", whitelistRequests);
            map.put("authSuccessRate", totalRequests > 0 ? (double) successfulAuth / totalRequests : 0.0);
            return map;
        }
    }

    /**
     * 认证统计信息实例
     */
    private final AuthStats authStats = new AuthStats();

    /**
     * 获取认证统计信息
     * 
     * @return AuthStats 统计信息
     */
    public AuthStats getAuthStats() {
        return authStats;
    }
}