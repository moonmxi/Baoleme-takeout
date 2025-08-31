/**
 * 网关API客户端服务
 * 简化设计，直接通过HTTP调用网关微服务接口
 * 专为用户服务设计，提供用户相关的跨数据库操作
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.userservice.client;

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
 * 专为用户服务提供店铺、商品、订单、评价等跨数据库查询功能
 */
@Slf4j
@Service
public class GatewayApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("http://localhost:8080")
    private String gatewayBaseUrl;

    @Value("30")
    private int requestTimeout;

    @Autowired
    public GatewayApiClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
    }

    /**
     * 分页获取店铺列表
     */
    public List<Map<String, Object>> getStoreList(int page, int pageSize, String keyword, 
                                                   String category, Integer status, String token) {
        try {
            log.info("调用网关API获取店铺列表: page={}, pageSize={}", page, pageSize);
            
            // 构建查询条件
            Map<String, Object> conditions = new HashMap<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                conditions.put("name", keyword);
            }
            if (category != null && !category.trim().isEmpty()) {
                conditions.put("category", category);
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
    public List<Map<String, Object>> getProductList(Long storeId, int page, int pageSize, String category, String token) {
        try {
            log.info("调用网关API获取商品列表: storeId={}, page={}, pageSize={}", storeId, page, pageSize);
            
            Map<String, Object> conditions = new HashMap<>();
            if (storeId != null) {
                conditions.put("store_id", storeId);
            }
            if (category != null && !category.trim().isEmpty()) {
                conditions.put("category", category);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            if (!conditions.isEmpty()) {
                requestBody.put("condition", conditions);
            }
            
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
            log.error("调用网关API获取商品列表失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 根据商品ID获取商品详情
     */
    public Map<String, Object> getProductById(Long productId, String token) {
        try {
            log.info("调用网关API获取商品详情: productId={}", productId);
            
            String url = gatewayBaseUrl + "/api/database/product/" + productId;
            
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
            log.error("调用网关API获取商品详情失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 根据店铺ID获取店铺详情
     */
    public Map<String, Object> getStoreById(Long storeId, String token) {
        try {
            log.info("调用网关API获取店铺详情: storeId={}", storeId);
            
            String url = gatewayBaseUrl + "/api/database/store/" + storeId;
            
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
            log.error("调用网关API获取店铺详情失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户订单列表
     */
    public List<Map<String, Object>> getUserOrderList(Long userId, Integer status, int page, int pageSize, String token) {
        try {
            log.info("调用网关API获取用户订单列表: userId={}, page={}, pageSize={}", userId, page, pageSize);
            
            Map<String, Object> conditions = new HashMap<>();
            if (userId != null) {
                conditions.put("user_id", userId);
            }
            if (status != null) {
                conditions.put("status", status);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            if (!conditions.isEmpty()) {
                requestBody.put("condition", conditions);
            }
            
            String url = gatewayBaseUrl + "/api/database/order/page?page=" + page + "&pageSize=" + pageSize;
            
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
            log.error("调用网关API获取用户订单列表失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 根据订单ID获取订单详情
     */
    public Map<String, Object> getOrderById(Long orderId, String token) {
        try {
            log.info("调用网关API获取订单详情: orderId={}", orderId);
            
            String url = gatewayBaseUrl + "/api/database/order/" + orderId;
            
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
            log.error("调用网关API获取订单详情失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 获取商品评价列表
     */
    public List<Map<String, Object>> getProductReviewList(Long productId, int page, int pageSize, String token) {
        try {
            log.info("调用网关API获取商品评价列表: productId={}, page={}, pageSize={}", productId, page, pageSize);
            
            Map<String, Object> conditions = new HashMap<>();
            if (productId != null) {
                conditions.put("product_id", productId);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            if (!conditions.isEmpty()) {
                requestBody.put("condition", conditions);
            }
            
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
            log.error("调用网关API获取商品评价列表失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 提交商品评价
     */
    public boolean submitReview(Map<String, Object> reviewData, String token) {
        try {
            log.info("调用网关API提交商品评价: {}", reviewData);
            
            String url = gatewayBaseUrl + "/api/database/review";
            
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .bodyValue(reviewData)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            return response != null && Boolean.TRUE.equals(response.get("success"));
            
        } catch (Exception e) {
            log.error("调用网关API提交商品评价失败", e);
            return false;
        }
    }

    /**
     * 根据关键词搜索店铺和商品
     */
    public List<Map<String, Object>> searchStore(int page, int pageSize, String keyword, String token) {
        try {
            log.info("调用网关API获取店铺列表: page={}, pageSize={}", page, pageSize);

            // 构建查询条件
            Map<String, Object> conditions = new HashMap<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                conditions.put("name", keyword);
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
     * 获取用户收藏店铺列表
     */
    public List<Map<String, Object>> getUserFavoriteStores(Long userId, int page, int pageSize, String token) {
        try {
            log.info("调用网关API获取用户收藏店铺列表: userId={}, page={}, pageSize={}", userId, page, pageSize);
            
            // 第一步：查询favorite表获取storeId列表
            Map<String, Object> favoriteConditions = new HashMap<>();
            if (userId != null) {
                favoriteConditions.put("user_id", userId);
            }
            
            Map<String, Object> favoriteRequestBody = new HashMap<>();
            if (!favoriteConditions.isEmpty()) {
                favoriteRequestBody.put("condition", favoriteConditions);
            }

            String favoriteUrl = gatewayBaseUrl + "/api/database/favorite/page?page=" + page + "&pageSize=" + pageSize;
            
            Map<String, Object> favoriteResponse = webClient.post()
                    .uri(favoriteUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .bodyValue(favoriteRequestBody)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            if (favoriteResponse == null || !Boolean.TRUE.equals(favoriteResponse.get("success"))) {
                log.warn("查询favorite表返回异常响应: {}", favoriteResponse);
                return new ArrayList<>();
            }
            
            Map<String, Object> favoriteData = (Map<String, Object>) favoriteResponse.get("data");
            if (favoriteData == null) {
                return new ArrayList<>();
            }
            
            List<Map<String, Object>> favoriteRecords = (List<Map<String, Object>>) favoriteData.get("records");
            if (favoriteRecords == null || favoriteRecords.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 第二步：提取storeId列表，查询store表获取详细信息
            List<Map<String, Object>> storeDetailsList = new ArrayList<>();
            
            for (Map<String, Object> favoriteRecord : favoriteRecords) {
                Object storeIdObj = favoriteRecord.get("store_id");
                if (storeIdObj != null) {
                    Long storeId = null;
                    if (storeIdObj instanceof Number) {
                        storeId = ((Number) storeIdObj).longValue();
                    } else if (storeIdObj instanceof String) {
                        try {
                            storeId = Long.parseLong((String) storeIdObj);
                        } catch (NumberFormatException e) {
                            log.warn("无法解析storeId: {}", storeIdObj);
                            continue;
                        }
                    }
                    
                    if (storeId != null) {
                        // 查询store表获取店铺详细信息
                        Map<String, Object> storeDetails = getStoreById(storeId, token);
                        if (storeDetails != null && !storeDetails.isEmpty()) {
                            // 将收藏时间等信息也添加到结果中
                            storeDetails.put("favorite_time", favoriteRecord.get("created_time"));
                            storeDetails.put("favorite_id", favoriteRecord.get("id"));
                            storeDetailsList.add(storeDetails);
                        }
                    }
                }
            }
            
            log.info("成功获取用户收藏店铺详细信息，共{}条", storeDetailsList.size());
            return storeDetailsList;
            
        } catch (Exception e) {
            log.error("调用网关API获取用户收藏店铺列表失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 更新用户浏览历史
     */
    public boolean updateUserViewHistory(Long userId, Long storeId, String token) {
        try {
            log.info("调用网关API更新用户浏览历史: userId={}, storeId={}", userId, storeId);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);
            requestBody.put("store_id", storeId);
            requestBody.put("view_time", LocalDateTime.now().toString());
            
            String url = gatewayBaseUrl + "/api/database/view_history";
            
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(requestTimeout))
                    .block();
            
            return response != null && Boolean.TRUE.equals(response.get("success"));
            
        } catch (Exception e) {
            log.error("调用网关API更新用户浏览历史失败", e);
            return false;
        }
    }

    /**
     * 获取用户浏览历史列表
     */
    public List<Map<String, Object>> getUserViewHistory(Long userId, int page, int pageSize, String token) {
        try {
            log.info("调用网关API获取用户浏览历史列表: userId={}, page={}, pageSize={}", userId, page, pageSize);
            
            Map<String, Object> conditions = new HashMap<>();
            if (userId != null) {
                conditions.put("user_id", userId);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            if (!conditions.isEmpty()) {
                requestBody.put("condition", conditions);
            }
            
            String url = gatewayBaseUrl + "/api/database/view_history/page?page=" + page + "&pageSize=" + pageSize;
            
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
            log.error("调用网关API获取用户浏览历史列表失败", e);
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
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