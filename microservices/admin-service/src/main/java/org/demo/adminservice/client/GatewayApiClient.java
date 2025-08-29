/**
 * 网关API客户端服务
 * 简化设计，直接通过HTTP调用网关微服务接口
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.adminservice.client;

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
import java.time.LocalDateTime;
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

    @Autowired
    public GatewayApiClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
    }

    /**
     * 分页获取用户列表
     */
    public List<Map<String, Object>> getUserList(int page, int pageSize, String keyword, 
                                                  String gender, Long startId, Long endId, String token) {
        try {
            log.info("调用网关API获取用户列表: page={}, pageSize={}", page, pageSize);
            
            // 构建查询条件
            Map<String, Object> conditions = new HashMap<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                conditions.put("username", keyword);
            }
            if (gender != null && !gender.trim().isEmpty()) {
                conditions.put("gender", gender);
            }
            if (startId != null) {
                conditions.put("id_gte", startId);
            }
            if (endId != null) {
                conditions.put("id_lte", endId);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            if (!conditions.isEmpty()) {
                requestBody.put("condition", conditions);
            }
            
            String url = gatewayBaseUrl + "/api/database/user/page?page=" + page + "&pageSize=" + pageSize;
            
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
            log.error("调用网关API获取用户列表失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 分页获取骑手列表
     */
    public List<Map<String, Object>> getRiderList(int page, int pageSize, String keyword, Long startId, Long endId,
                                                   Integer status, Integer dispatchMode, Long startBalance, Long endBalance, String token) {
        try {
            log.info("调用网关API获取骑手列表: page={}, pageSize={}", page, pageSize);
            
            // 构建查询条件
            Map<String, Object> conditions = new HashMap<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                conditions.put("username", keyword);
            }
            if (startId != null) {
                conditions.put("id_gte", startId);
            }
            if (endId != null) {
                conditions.put("id_lte", endId);
            }
            if (status != null) {
                conditions.put("status", status);
            }
            if (dispatchMode != null) {
                conditions.put("dispatch_mode", dispatchMode);
            }
            if (startBalance != null) {
                conditions.put("balance_gte", startBalance);
            }
            if (endBalance != null) {
                conditions.put("balance_lte", endBalance);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            if (!conditions.isEmpty()) {
                requestBody.put("condition", conditions);
            }
            
            String url = gatewayBaseUrl + "/api/database/rider/page?page=" + page + "&pageSize=" + pageSize;
            
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
            log.error("调用网关API获取骑手列表失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 分页获取商家列表
     */
    public List<Map<String, Object>> getMerchantList(int page, int pageSize, String keyword, 
                                                      Long startId, Long endId, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("page", page);
        requestBody.put("pageSize", pageSize);
        if (keyword != null) requestBody.put("keyword", keyword);
        if (startId != null) requestBody.put("startId", startId);
        if (endId != null) requestBody.put("endId", endId);

        return callGatewayApi("/database/query", requestBody, token, "merchant");
    }

    /**
     * 分页获取店铺列表
     */
    public List<Map<String, Object>> getStoreList(int page, int pageSize, String keyword, 
                                                   Long merchantId, Integer status, String token) {
        try {
            log.info("调用网关API获取店铺列表: page={}, pageSize={}", page, pageSize);
            
            // 构建查询条件
            Map<String, Object> conditions = new HashMap<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                conditions.put("name", keyword);
            }
            if (merchantId != null) {
                conditions.put("merchant_id", merchantId);
            }
            if (status != null) {
                conditions.put("status", status);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            if (!conditions.isEmpty()) {
                requestBody.put("condition", conditions);
            }
            
            String url = gatewayBaseUrl + "/api/database/store/page?page=" + page + "&pageSize=" + pageSize;
            
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
            log.error("调用网关API获取店铺列表失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 根据店铺ID获取商品列表
     */
    public List<Map<String, Object>> getProductList(Long storeId, int page, int pageSize, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("storeId", storeId);
        requestBody.put("page", page);
        requestBody.put("pageSize", pageSize);

        return callGatewayApi("/database/query", requestBody, token, "product");
    }

    /**
     * 分页获取订单列表
     */
    public List<Map<String, Object>> getOrderList(Long userId, Long storeId, Long riderId, Integer status,
                                                   LocalDateTime createdAt, LocalDateTime endedAt, int page, int pageSize, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        if (userId != null) requestBody.put("userId", userId);
        if (storeId != null) requestBody.put("storeId", storeId);
        if (riderId != null) requestBody.put("riderId", riderId);
        if (status != null) requestBody.put("status", status);
        if (createdAt != null) requestBody.put("createdAt", createdAt.toString());
        if (endedAt != null) requestBody.put("endedAt", endedAt.toString());
        requestBody.put("page", page);
        requestBody.put("pageSize", pageSize);

        return callGatewayApi("/database/query", requestBody, token, "order");
    }

    /**
     * 根据条件获取评论列表
     */
    public List<Map<String, Object>> getReviewList(Long userId, Long storeId, Long productId, LocalDateTime startTime,
                                                    LocalDateTime endTime, int page, int pageSize, Integer startRating, Integer endRating, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        if (userId != null) requestBody.put("userId", userId);
        if (storeId != null) requestBody.put("storeId", storeId);
        if (productId != null) requestBody.put("productId", productId);
        if (startTime != null) requestBody.put("startTime", startTime.toString());
        if (endTime != null) requestBody.put("endTime", endTime.toString());
        if (startRating != null) requestBody.put("startRating", startRating);
        if (endRating != null) requestBody.put("endRating", endRating);
        requestBody.put("page", page);
        requestBody.put("pageSize", pageSize);

        return callGatewayApi("/database/query", requestBody, token, "review");
    }

    /**
     * 根据关键词搜索店铺和商品
     */
    public List<Map<String, Object>> searchStoreAndProduct(String keyword, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("keyword", keyword);
        requestBody.put("searchType", "store_and_product");

        return callGatewayApi("/database/search", requestBody, token, null);
    }

    /**
     * 根据ID获取订单详情
     */
    public Map<String, Object> getOrderById(Long orderId, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", orderId);

        List<Map<String, Object>> result = callGatewayApi("/database/get", requestBody, token, "order");
        return result.isEmpty() ? new HashMap<>() : result.get(0);
    }

    /**
     * 根据ID获取评论详情
     */
    public Map<String, Object> getReviewById(Long reviewId, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", reviewId);

        List<Map<String, Object>> result = callGatewayApi("/database/get", requestBody, token, "review");
        return result.isEmpty() ? new HashMap<>() : result.get(0);
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(String username, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);

        try {
            callGatewayApi("/database/delete", requestBody, token, "user");
            return true;
        } catch (Exception e) {
            log.error("删除用户失败: {}", username, e);
            return false;
        }
    }

    /**
     * 删除骑手
     */
    public boolean deleteRider(String username, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);

        try {
            callGatewayApi("/database/delete", requestBody, token, "rider");
            return true;
        } catch (Exception e) {
            log.error("删除骑手失败: {}", username, e);
            return false;
        }
    }

    /**
     * 删除商家
     */
    public boolean deleteMerchant(String username, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);

        try {
            callGatewayApi("/database/delete", requestBody, token, "merchant");
            return true;
        } catch (Exception e) {
            log.error("删除商家失败: {}", username, e);
            return false;
        }
    }

    /**
     * 删除店铺
     */
    public boolean deleteStore(String storeName, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", storeName);

        try {
            callGatewayApi("/database/delete", requestBody, token, "store");
            return true;
        } catch (Exception e) {
            log.error("删除店铺失败: {}", storeName, e);
            return false;
        }
    }

    /**
     * 删除商品
     */
    public boolean deleteProduct(String productName, String storeName, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("productName", productName);
        requestBody.put("storeName", storeName);

        try {
            callGatewayApi("/database/delete", requestBody, token, "product");
            return true;
        } catch (Exception e) {
            log.error("删除商品失败: {} from {}", productName, storeName, e);
            return false;
        }
    }

    /**
     * 调用网关API的通用方法
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