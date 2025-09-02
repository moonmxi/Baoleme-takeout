/**
 * 请求日志拦截器
 * 记录所有HTTP请求的详细信息，包括请求参数、响应时间、状态码等
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 请求日志拦截器类
 * 拦截所有HTTP请求，记录详细的请求和响应信息
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    /**
     * 请求开始时间属性名
     */
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    /**
     * 请求ID属性名
     */
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";

    /**
     * 请求前置处理
     * 记录请求开始时间和基本信息
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @return boolean 是否继续处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        
        // 记录请求开始日志
        logRequestStart(request, requestId);
        
        return true;
    }

    /**
     * 请求完成后处理
     * 记录请求完成信息和性能指标
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @param ex 异常对象（如果有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
        
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            logRequestCompletion(request, response, requestId, duration, ex);
        }
    }

    /**
     * 记录请求开始日志
     * 
     * @param request HTTP请求对象
     * @param requestId 请求ID
     */
    private void logRequestStart(HttpServletRequest request, String requestId) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String remoteAddr = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n=== 请求开始 [ID: ").append(requestId).append("] ===")
                     .append("\n时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                     .append("\n方法: ").append(method)
                     .append("\n路径: ").append(uri);
            
            if (queryString != null && !queryString.isEmpty()) {
                logMessage.append("\n查询参数: ").append(queryString);
            }
            
            logMessage.append("\n客户端IP: ").append(remoteAddr);
            
            if (userAgent != null) {
                logMessage.append("\n用户代理: ").append(userAgent);
            }
            
            // 记录请求头（仅记录重要的头信息）
            Map<String, String> importantHeaders = getImportantHeaders(request);
            if (!importantHeaders.isEmpty()) {
                logMessage.append("\n重要请求头:");
                importantHeaders.forEach((key, value) -> 
                    logMessage.append("\n  ").append(key).append(": ").append(value));
            }
            
            log.info(logMessage.toString());
            
        } catch (Exception e) {
            log.warn("记录请求开始日志失败: {}", e.getMessage());
        }
    }

    /**
     * 记录请求完成日志
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param requestId 请求ID
     * @param duration 请求耗时
     * @param ex 异常对象
     */
    private void logRequestCompletion(HttpServletRequest request, HttpServletResponse response, 
                                    String requestId, long duration, Exception ex) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();
            String contentType = response.getContentType();
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n=== 请求完成 [ID: ").append(requestId).append("] ===")
                     .append("\n时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                     .append("\n方法: ").append(method)
                     .append("\n路径: ").append(uri)
                     .append("\n状态码: ").append(status)
                     .append("\n耗时: ").append(duration).append("ms");
            
            if (contentType != null) {
                logMessage.append("\n响应类型: ").append(contentType);
            }
            
            // 根据状态码和耗时判断日志级别
            if (ex != null) {
                logMessage.append("\n异常: ").append(ex.getClass().getSimpleName())
                         .append(" - ").append(ex.getMessage());
                log.error(logMessage.toString());
            } else if (status >= 500) {
                log.error(logMessage.toString());
            } else if (status >= 400) {
                log.warn(logMessage.toString());
            } else if (duration > 5000) {
                logMessage.append("\n⚠️ 请求耗时较长");
                log.warn(logMessage.toString());
            } else if (duration > 1000) {
                log.info(logMessage.toString());
            } else {
                log.debug(logMessage.toString());
            }
            
            // 记录性能指标
            recordPerformanceMetrics(method, uri, status, duration);
            
        } catch (Exception e) {
            log.warn("记录请求完成日志失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求对象
     * @return String 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", 
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        };
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 取第一个IP地址
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 获取重要的请求头信息
     * 
     * @param request HTTP请求对象
     * @return Map 重要请求头映射
     */
    private Map<String, String> getImportantHeaders(HttpServletRequest request) {
        Map<String, String> importantHeaders = new HashMap<>();
        
        String[] importantHeaderNames = {
            "Authorization", "Content-Type", "Accept", "Origin", 
            "Referer", "X-Requested-With", "Cache-Control"
        };
        
        for (String headerName : importantHeaderNames) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isEmpty()) {
                // 对敏感信息进行脱敏处理
                if ("Authorization".equalsIgnoreCase(headerName) && headerValue.length() > 20) {
                    headerValue = headerValue.substring(0, 20) + "...";
                }
                importantHeaders.put(headerName, headerValue);
            }
        }
        
        return importantHeaders;
    }

    /**
     * 记录性能指标
     * 
     * @param method HTTP方法
     * @param uri 请求URI
     * @param status 状态码
     * @param duration 耗时
     */
    private void recordPerformanceMetrics(String method, String uri, int status, long duration) {
        try {
            // 这里可以集成监控系统，如Micrometer、Prometheus等
            // 记录请求计数、响应时间分布、错误率等指标
            
            if (duration > 10000) {
                log.warn("⚠️ 慢请求告警: {} {} 耗时 {}ms", method, uri, duration);
            }
            
            if (status >= 500) {
                log.error("🚨 服务器错误告警: {} {} 状态码 {}", method, uri, status);
            }
            
        } catch (Exception e) {
            log.debug("记录性能指标失败: {}", e.getMessage());
        }
    }
}