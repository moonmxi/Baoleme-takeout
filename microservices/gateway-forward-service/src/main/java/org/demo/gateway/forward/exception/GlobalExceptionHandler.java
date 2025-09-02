/**
 * 全局异常处理器
 * 统一处理应用程序中的异常，提供友好的错误响应和完整的日志记录
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器类
 * 捕获并处理各种类型的异常，返回统一格式的错误响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理HTTP客户端错误异常（4xx）
     * 
     * @param ex HTTP客户端错误异常
     * @param request HTTP请求对象
     * @return ResponseEntity 错误响应
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpClientErrorException(
            HttpClientErrorException ex, HttpServletRequest request) {
        
        log.warn("HTTP客户端错误: {} {} - 状态码: {}, 响应: {}", 
                request.getMethod(), request.getRequestURI(), 
                ex.getStatusCode(), ex.getResponseBodyAsString());
        
        Map<String, Object> errorResponse = createErrorResponse(
                "HTTP_CLIENT_ERROR",
                "目标服务返回客户端错误: " + ex.getStatusCode(),
                request.getRequestURI(),
                ex.getStatusCode().value(),
                ex.getResponseBodyAsString()
        );
        
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    /**
     * 处理HTTP服务器错误异常（5xx）
     * 
     * @param ex HTTP服务器错误异常
     * @param request HTTP请求对象
     * @return ResponseEntity 错误响应
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpServerErrorException(
            HttpServerErrorException ex, HttpServletRequest request) {
        
        log.error("HTTP服务器错误: {} {} - 状态码: {}, 响应: {}", 
                request.getMethod(), request.getRequestURI(), 
                ex.getStatusCode(), ex.getResponseBodyAsString());
        
        Map<String, Object> errorResponse = createErrorResponse(
                "HTTP_SERVER_ERROR",
                "目标服务返回服务器错误: " + ex.getStatusCode(),
                request.getRequestURI(),
                ex.getStatusCode().value(),
                ex.getResponseBodyAsString()
        );
        
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    /**
     * 处理资源访问异常（连接超时、网络异常等）
     * 
     * @param ex 资源访问异常
     * @param request HTTP请求对象
     * @return ResponseEntity 错误响应
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAccessException(
            ResourceAccessException ex, HttpServletRequest request) {
        
        String errorType;
        String errorMessage;
        
        if (ex.getCause() instanceof SocketTimeoutException) {
            errorType = "TIMEOUT_ERROR";
            errorMessage = "请求超时，目标服务响应时间过长";
            log.error("请求超时: {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        } else if (ex.getCause() instanceof ConnectException) {
            errorType = "CONNECTION_ERROR";
            errorMessage = "无法连接到目标服务";
            log.error("连接失败: {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        } else {
            errorType = "NETWORK_ERROR";
            errorMessage = "网络访问异常: " + ex.getMessage();
            log.error("网络异常: {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        }
        
        Map<String, Object> errorResponse = createErrorResponse(
                errorType,
                errorMessage,
                request.getRequestURI(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * 处理404异常（未找到处理器）
     * 
     * @param ex 未找到处理器异常
     * @param request HTTP请求对象
     * @return ResponseEntity 错误响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        log.warn("未找到处理器: {} {}", request.getMethod(), request.getRequestURI());
        
        Map<String, Object> errorResponse = createErrorResponse(
                "NOT_FOUND",
                "未找到匹配的路由规则",
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * 处理非法参数异常
     * 
     * @param ex 非法参数异常
     * @param request HTTP请求对象
     * @return ResponseEntity 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.warn("参数异常: {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                "INVALID_PARAMETER",
                "请求参数无效: " + ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理运行时异常
     * 
     * @param ex 运行时异常
     * @param request HTTP请求对象
     * @return ResponseEntity 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        log.error("运行时异常: {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                "RUNTIME_ERROR",
                "服务内部错误: " + ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理通用异常
     * 
     * @param ex 异常
     * @param request HTTP请求对象
     * @return ResponseEntity 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("未知异常: {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                "UNKNOWN_ERROR",
                "服务异常，请稍后重试",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 创建统一格式的错误响应
     * 
     * @param errorType 错误类型
     * @param message 错误消息
     * @param path 请求路径
     * @param status HTTP状态码
     * @param details 详细信息
     * @return Map 错误响应对象
     */
    private Map<String, Object> createErrorResponse(String errorType, String message, 
                                                   String path, int status, String details) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorType", errorType);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        errorResponse.put("status", status);
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        if (details != null && !details.isEmpty()) {
            errorResponse.put("details", details);
        }
        
        return errorResponse;
    }
}