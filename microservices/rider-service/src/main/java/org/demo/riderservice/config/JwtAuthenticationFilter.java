/**
 * JWT认证过滤器
 * 处理JWT令牌的验证和用户身份认证
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.riderservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.demo.riderservice.common.JwtUtils;
import org.demo.riderservice.common.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * JWT认证过滤器
 * 从请求头中提取JWT令牌并验证用户身份
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Redis模板，用于验证令牌有效性
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 执行JWT认证过滤
     * 
     * @param request HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        System.out.println("JWT过滤器处理路径: " + path);
        
        // 清除之前的用户信息
        UserHolder.clear();
        
        // 从请求头获取Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // 解析JWT令牌
            Map<String, Object> payload = JwtUtils.parsePayload(token);
            if (payload != null) {
                Number idNumber = (Number) payload.get("user_id");
                String role = (String) payload.get("role");
                
                if (idNumber != null && role != null) {
                    // 验证Redis中的令牌
                    String redisKey = role + ":token:" + token;
                    Object storedId = redisTemplate.opsForValue().get(redisKey);
                    
                    if (storedId != null) {
                        // 设置用户信息到ThreadLocal
                        Long userId = idNumber.longValue();
                        UserHolder.set(userId, role);
                        
                        // 设置Spring Security认证信息
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        System.out.println("JWT认证成功，用户ID: " + userId + ", 角色: " + role);
                    } else {
                        System.out.println("Redis中未找到有效令牌");
                    }
                } else {
                    System.out.println("JWT载荷中缺少用户ID或角色信息");
                }
            } else {
                System.out.println("JWT令牌解析失败");
            }
        } else {
            System.out.println("请求头中未找到Authorization或格式不正确");
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}