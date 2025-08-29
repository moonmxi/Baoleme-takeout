/**
 * JWT认证过滤器
 * 在网关层统一处理JWT令牌的验证和解析
 * 确保所有API请求都经过身份验证
 * 
 * 主要功能：
 * 1. 从请求头中提取JWT token
 * 2. 验证token的有效性和过期时间
 * 3. 解析token中的用户信息
 * 4. 设置安全上下文
 * 5. 处理认证失败的情况
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.common.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT认证过滤器
 * 继承OncePerRequestFilter确保每个请求只执行一次过滤
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JSON对象映射器
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Redis模板，用于验证token有效性
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 不需要JWT验证的路径白名单
     * 注意：/api/database/* 路径需要JWT验证，因为这些是受保护的数据库操作接口
     */
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
            "/",
            "/api/database/health",
            "/actuator/health",
            "/actuator/info",
            "/error"
    );

    /**
     * JWT认证过滤逻辑
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("JWT过滤器处理请求: {} {}", method, requestPath);
        
        // 检查是否在白名单中
        if (isWhitelistPath(requestPath)) {
            log.debug("请求路径在白名单中，跳过JWT验证: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 提取JWT token
        String token = extractToken(request);
        if (token == null) {
            log.warn("请求缺少JWT token: {} {}", method, requestPath);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "缺少认证令牌");
            return;
        }
        
        // 验证JWT token
        if (!JwtUtils.validateToken(token)) {
            log.warn("JWT token验证失败: {} {} - token前缀: {}", method, requestPath, 
                    token.length() > 10 ? token.substring(0, 10) + "..." : token);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "认证令牌无效或已过期");
            return;
        }
        
        // 解析用户信息
        Map<String, Object> userInfo = JwtUtils.parsePayload(token);
        if (userInfo == null) {
            log.warn("JWT token解析失败: {} {} - token前缀: {}", method, requestPath,
                    token.length() > 10 ? token.substring(0, 10) + "..." : token);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "认证令牌解析失败");
            return;
        }
        
        // 验证Redis中的token（与其他微服务保持一致）
        String role = (String) userInfo.get("role");
        if (role != null) {
            String redisKey = role + ":token:" + token;
            Object storedId = redisTemplate.opsForValue().get(redisKey);
            
            if (storedId == null) {
                log.warn("Redis中未找到有效令牌: {} {} - 角色: {}", method, requestPath, role);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "认证令牌已失效");
                return;
            }
        }
        
        // 设置用户信息到请求属性中（进行类型安全转换）
        Object rawId = userInfo.get("user_id");
        Long userId = (rawId instanceof Number) ? ((Number) rawId).longValue() : null;
        
        request.setAttribute("userId", userId);
        request.setAttribute("role", String.valueOf(userInfo.get("role")));
        request.setAttribute("username", String.valueOf(userInfo.get("username")));
        
        // 设置Spring Security认证上下文
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userInfo.get("user_id"), null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        log.info("JWT验证成功: {} {} - 用户ID={}, 角色={}, 用户名={}", 
                method, requestPath, userInfo.get("user_id"), userInfo.get("role"), userInfo.get("username"));
        
        // 继续处理请求
        filterChain.doFilter(request, response);
    }

    /**
     * 检查请求路径是否在白名单中
     * 
     * @param path 请求路径
     * @return true表示在白名单中，false表示不在
     */
    private boolean isWhitelistPath(String path) {
        return WHITELIST_PATHS.stream().anyMatch(whitePath -> 
                path.equals(whitePath) || path.startsWith(whitePath + "/"));
    }

    /**
     * 从请求头中提取JWT token
     * 
     * @param request HTTP请求对象
     * @return JWT token字符串，未找到返回null
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // 移除"Bearer "前缀
        }
        return null;
    }

    /**
     * 发送错误响应
     * 
     * @param response HTTP响应对象
     * @param status HTTP状态码
     * @param message 错误消息
     * @throws IOException IO异常
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) 
            throws IOException {
        
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("code", status.value());
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        
        log.info("发送JWT认证错误响应: {} - {}", status.value(), message);
    }

    /**
     * 确定是否应该过滤此请求
     * 
     * @param request HTTP请求对象
     * @return true表示应该过滤，false表示跳过
     * @throws ServletException Servlet异常
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 可以在这里添加更复杂的过滤逻辑
        // 目前所有请求都会经过过滤器，在doFilterInternal中处理白名单
        return false;
    }
}