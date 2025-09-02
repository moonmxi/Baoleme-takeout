/**
 * 网关API客户端服务
 * 简化设计，直接通过HTTP调用网关微服务接口
 * 参考admin-service实现
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.client;

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
import java.util.*;

/**
 * 网关API客户端服务实现类
 * 使用WebClient调用网关微服务的REST API
 */
@Slf4j
@Service
public class GatewayApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gateway.service.base-url:http://localhost:8080}")
    private String gatewayBaseUrl;

    @Value("${gateway.service.timeout:30}")
    private int requestTimeout;

    /**
     * 构造函数
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
     * 根据ID获取商品详细信息
     * 
     * @param productId 商品ID
     * @param token JWT认证令牌
     * @return 商品详细信息Map
     * @throws RuntimeException 当API调用失败时抛出异常
     */
    public Map<String, Object> getProductById(Long productId, String token) {
        try {
            log.info("调用网关API获取商品详情: productId={}", productId);
            
            // 修复API路径：使用正确的GET请求路径
            String url = gatewayBaseUrl + "/api/database/product/" + productId;
            
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Object data = response.get("data");
                if (data instanceof Map) {
                    return (Map<String, Object>) data;
                }
            }
            
            log.warn("网关API返回异常响应: {}", response);
            return new HashMap<>();
            
        } catch (Exception e) {
            log.error("调用网关API获取商品详情失败: productId={}", productId, e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 根据用户ID获取用户详细信息
     * 
     * @param userId 用户ID
     * @param token JWT认证令牌
     * @return 用户详细信息Map
     * @throws RuntimeException 当API调用失败时抛出异常
     */
    public Map<String, Object> getUserById(Long userId, String token) {
        try {
            log.info("调用网关API获取用户详情: userId={}", userId);
            
            // 使用正确的GET请求路径
            String url = gatewayBaseUrl + "/api/database/user/" + userId;
            
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Object data = response.get("data");
                if (data instanceof Map) {
                    return (Map<String, Object>) data;
                }
            }
            
            log.warn("网关API返回异常响应: {}", response);
            return new HashMap<>();
            
        } catch (Exception e) {
            log.error("调用网关API获取用户详情失败: userId={}", userId, e);
            // 不抛出异常，返回空Map，避免因为用户信息获取失败影响整个评论显示
            return new HashMap<>();
        }
    }

    /**
     * 根据商品ID获取评论列表
     * 
     * @param productId 商品ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param token JWT认证令牌
     * @return 评论列表
     * @throws RuntimeException 当API调用失败时抛出异常
     */
    public List<Map<String, Object>> getProductReviews(Long productId, int page, int pageSize, String token) {
        try {
            log.info("调用网关API获取商品评论: productId={}, page={}, pageSize={}", productId, page, pageSize);
            
            // 构建查询条件
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("product_id", productId);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("condition", conditions);
            
            // 修复API路径：使用正确的分页查询路径
            String url = gatewayBaseUrl + "/api/database/review/page?page=" + page + "&pageSize=" + pageSize;
            
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    return (List<Map<String, Object>>) data.get("records");
                }
            }
            
            log.warn("网关API返回异常响应: {}", response);
            return new ArrayList<>();
            
        } catch (Exception e) {
            log.error("调用网关API获取商品评论失败: productId={}", productId, e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 根据店铺ID获取商品列表
     * 
     * @param storeId 店铺ID
     * @param category 商品分类（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @param token JWT认证令牌
     * @return 商品列表
     * @throws RuntimeException 当API调用失败时抛出异常
     */
    public List<Map<String, Object>> getStoreProducts(Long storeId, String category, int page, int pageSize, String token) {
        try {
            log.info("调用网关API获取店铺商品: storeId={}, category={}, page={}, pageSize={}", storeId, category, page, pageSize);
            
            // 构建查询条件
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("store_id", storeId);
            if (category != null && !category.trim().isEmpty()) {
                conditions.put("category", category);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("condition", conditions);
            
            String url = gatewayBaseUrl + "/api/database/product/page?page=" + page + "&pageSize=" + pageSize;
            
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    return (List<Map<String, Object>>) data.get("records");
                }
            }
            
            log.warn("网关API返回异常响应: {}", response);
            return new ArrayList<>();
            
        } catch (Exception e) {
            log.error("调用网关API获取店铺商品失败: storeId={}", storeId, e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 调用网关API的通用方法
     * 
     * @param endpoint API端点
     * @param requestBody 请求体
     * @param token JWT认证令牌
     * @param tableName 表名（可选）
     * @return API响应数据列表
     * @throws RuntimeException 当API调用失败时抛出异常
     */
    private List<Map<String, Object>> callGatewayApi(String endpoint, Map<String, Object> requestBody, String token, String tableName) {
        try {
            if (tableName != null) {
                requestBody.put("tableName", tableName);
            }

            String url = gatewayBaseUrl + endpoint;
            log.debug("调用网关API: {} with body: {}", url, requestBody);

            String response = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();

            log.debug("网关API响应: {}", response);

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            
            if (Boolean.TRUE.equals(responseMap.get("success"))) {
                Object data = responseMap.get("data");
                if (data instanceof List) {
                    return (List<Map<String, Object>>) data;
                } else if (data instanceof Map) {
                    return Arrays.asList((Map<String, Object>) data);
                }
                return new ArrayList<>();
            } else {
                String errorMsg = (String) responseMap.get("message");
                log.error("网关API调用失败: {}", errorMsg);
                throw new RuntimeException("网关API调用失败: " + errorMsg);
            }

        } catch (Exception e) {
            log.error("网关API调用异常: {}", e.getMessage());
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }
}