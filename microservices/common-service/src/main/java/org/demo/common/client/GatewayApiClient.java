/**
 * 网关API客户端服务
 * 用于调用gateway-service获取用户信息
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关API客户端服务实现类
 * 使用WebClient调用网关微服务的REST API
 */
@Slf4j
@Service
public class GatewayApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * 网关服务基础URL
     */
    @Value("${gateway.service.base-url:http://localhost:8080}")
    private String gatewayBaseUrl;

    /**
     * 请求超时时间（秒）
     */
    @Value("${gateway.service.timeout:30}")
    private int requestTimeout;

    /**
     * 构造函数注入依赖
     * 
     * @param webClientBuilder WebClient构建器
     * @param objectMapper JSON对象映射器
     */
    @Autowired
    public GatewayApiClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
    }

    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @param token JWT认证令牌
     * @return 用户信息Map，包含username等字段
     * @throws RuntimeException 当调用失败时抛出异常
     */
    public Map<String, Object> getUserById(Long userId, String token) {
        try {
            log.info("调用网关API获取用户信息: userId={}", userId);
            
            String url = gatewayBaseUrl + "/api/database/user/" + userId;
            
            // 处理token格式，避免重复添加Bearer前缀
            String authHeader = token;
            if (token != null && !token.startsWith("Bearer ")) {
                authHeader = "Bearer " + token;
            }
            
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                return (Map<String, Object>) response.get("data");
            }
            
            log.warn("网关API返回异常响应: {}", response);
            return new HashMap<>();
            
        } catch (Exception e) {
            log.error("调用网关API获取用户信息失败: userId={}", userId, e);
            // 不抛出异常，返回空Map，避免影响主业务流程
            return new HashMap<>();
        }
    }

    /**
     * 根据商品ID获取商品信息
     * 
     * @param productId 商品ID
     * @param token JWT认证令牌
     * @return 商品信息Map，包含name等字段
     * @throws RuntimeException 当调用失败时抛出异常
     */
    public Map<String, Object> getProductById(Long productId, String token) {
        try {
            log.info("调用网关API获取商品信息: productId={}", productId);
            
            String url = gatewayBaseUrl + "/api/database/product/" + productId;
            
            // 处理token格式，避免重复添加Bearer前缀
            String authHeader = token;
            if (token != null && !token.startsWith("Bearer ")) {
                authHeader = "Bearer " + token;
            }
            
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                return (Map<String, Object>) response.get("data");
            }
            
            log.warn("网关API返回异常响应: {}", response);
            return new HashMap<>();
            
        } catch (Exception e) {
            log.error("调用网关API获取商品信息失败: productId={}", productId, e);
            // 不抛出异常，返回空Map，避免影响主业务流程
            return new HashMap<>();
        }
    }
}