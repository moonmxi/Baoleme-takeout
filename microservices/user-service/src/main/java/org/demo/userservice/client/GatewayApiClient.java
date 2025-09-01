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
import java.math.BigDecimal;

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
     * 安全地将对象转换为List<Map<String, Object>>
     * @param obj 要转换的对象
     * @return 转换后的列表，如果转换失败则返回空列表
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> safeListCast(Object obj) {
        if (obj == null) {
            return new ArrayList<>();
        }
        
        try {
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                // 验证列表中的元素是否为Map类型
                for (Object item : list) {
                    if (item != null && !(item instanceof Map)) {
                        log.warn("列表中包含非Map类型元素: {}", item.getClass().getSimpleName());
                        return new ArrayList<>();
                    }
                }
                return (List<Map<String, Object>>) obj;
            } else {
                log.warn("对象不是List类型: {}", obj.getClass().getSimpleName());
                return new ArrayList<>();
            }
        } catch (ClassCastException e) {
            log.error("类型转换失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 分页获取店铺列表
     */
    public List<Map<String, Object>> getStoreList(int page, int pageSize, String keyword, 
                                                   String category, Integer status, String token) {
        log.info("调用网关API获取店铺列表: page={}, pageSize={}", page, pageSize);
        
        // 构建查询条件
        Map<String, Object> conditions = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.put("name_like", keyword);
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
        
        String endpoint = "/api/database/store/page?page=" + page + "&pageSize=" + pageSize;
        return callGatewayPostApi(endpoint, requestBody, token);
    }

    /**
     * 根据店铺ID获取商品列表
     */
    public List<Map<String, Object>> getProductList(Long storeId, int page, int pageSize, String category, String token) {
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
        
        String endpoint = "/api/database/product/page?page=" + page + "&pageSize=" + pageSize;
        return callGatewayPostApi(endpoint, requestBody, token);
    }

    /**
     * 根据商品ID获取商品详情
     */
    public Map<String, Object> getProductById(Long productId, String token) {
        log.info("调用网关API获取商品详情: productId={}", productId);
        String endpoint = "/api/database/product/" + productId;
        return callGatewayGetApi(endpoint, token);
    }

    /**
     * 根据店铺ID获取店铺详情
     */
    public Map<String, Object> getStoreById(Long storeId, String token) {
        log.info("调用网关API获取店铺详情: storeId={}", storeId);
        String endpoint = "/api/database/store/" + storeId;
        return callGatewayGetApi(endpoint, token);
    }

    /**
     * 获取用户订单列表
     */
    public List<Map<String, Object>> getUserOrderList(Long userId, Integer status, int page, int pageSize, String token) {
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
        
        String endpoint = "/api/database/order/page?page=" + page + "&pageSize=" + pageSize;
        return callGatewayPostApi(endpoint, requestBody, token);
    }

    /**
     * 根据订单ID获取订单详情
     */
    public Map<String, Object> getOrderById(Long orderId, String token) {
        log.info("调用网关API获取订单详情: orderId={}", orderId);
        String endpoint = "/api/database/order/" + orderId;
        return callGatewayGetApi(endpoint, token);
    }

    /**
     * 获取商品评价列表
     */
    public List<Map<String, Object>> getProductReviewList(Long productId, int page, int pageSize, String token) {
        log.info("调用网关API获取商品评价列表: productId={}, page={}, pageSize={}", productId, page, pageSize);
        
        Map<String, Object> conditions = new HashMap<>();
        if (productId != null) {
            conditions.put("product_id", productId);
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        if (!conditions.isEmpty()) {
            requestBody.put("condition", conditions);
        }
        
        String endpoint = "/api/database/review/page?page=" + page + "&pageSize=" + pageSize;
        return callGatewayPostApi(endpoint, requestBody, token);
    }

    /**
     * 提交商品评价
     */
    public boolean submitReview(Map<String, Object> reviewData, String token) {
        log.info("调用网关API提交商品评价: {}", reviewData);
        String endpoint = "/api/database/review";
        return callGatewaySubmitApi(endpoint, reviewData, token);
    }

    /**
     * 带过滤条件的店铺搜索
     */
    public List<Map<String, Object>> searchStoreWithFilters(String keyword, BigDecimal distance, 
                                                           BigDecimal wishPrice, BigDecimal startRating, 
                                                           BigDecimal endRating, int page, 
                                                           int pageSize, String token) {
        log.info("调用网关API带过滤条件搜索店铺: keyword={}, page={}, pageSize={}", keyword, page, pageSize);

        // 构建查询条件
        Map<String, Object> conditions = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.put("name_like", keyword);
        }
        if (distance != null) {
            conditions.put("distance", distance);
        }
        if (wishPrice != null) {
            conditions.put("wish_price", wishPrice);
        }
        if (startRating != null) {
            conditions.put("start_rating", startRating);
        }
        if (endRating != null) {
            conditions.put("end_rating", endRating);
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        if (!conditions.isEmpty()) {
            requestBody.put("condition", conditions);
        }

        String endpoint = "/api/database/store/page?page=" + page + "&pageSize=" + pageSize;
        return callGatewayPostApi(endpoint, requestBody, token);
    }

    /**
     * 获取用户收藏店铺列表
     */
    public List<Map<String, Object>> getUserFavoriteStores(Long userId, int page, int pageSize, String token) {
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

        String favoriteEndpoint = "/api/database/favorite/page?page=" + page + "&pageSize=" + pageSize;
        List<Map<String, Object>> favoriteRecords = callGatewayPostApi(favoriteEndpoint, favoriteRequestBody, token);
        
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
    }

    /**
     * 更新用户浏览历史
     */
    public boolean updateUserViewHistory(Long userId, Long storeId, String token) {
        log.info("调用网关API更新用户浏览历史: userId={}, storeId={}", userId, storeId);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);
        requestBody.put("store_id", storeId);
        requestBody.put("view_time", LocalDateTime.now().toString());
        
        String endpoint = "/api/database/view_history";
        return callGatewaySubmitApi(endpoint, requestBody, token);
    }

    /**
     * 获取用户浏览历史列表
     */
    public List<Map<String, Object>> getUserViewHistory(Long userId, int page, int pageSize, String token) {
        log.info("调用网关API获取用户浏览历史列表: userId={}, page={}, pageSize={}", userId, page, pageSize);
        
        Map<String, Object> conditions = new HashMap<>();
        if (userId != null) {
            conditions.put("user_id", userId);
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        if (!conditions.isEmpty()) {
            requestBody.put("condition", conditions);
        }
        
        String endpoint = "/api/database/browse_history/page?page=" + page + "&pageSize=" + pageSize;
        return callGatewayPostApi(endpoint, requestBody, token);
    }

    /**
     * 调用网关API的通用POST方法（分页查询）
     */
    private List<Map<String, Object>> callGatewayPostApi(String endpoint, Map<String, Object> requestBody, String token) {
        try {
            String url = gatewayBaseUrl + endpoint;
            log.debug("调用网关API POST: {} with body: {}", url, requestBody);

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
                    return safeListCast(data.get("records"));
                }
            }
            
            log.warn("网关API返回异常响应: {}", response);
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("网关API调用异常: {}", e.getMessage());
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 调用网关API的通用GET方法（单个资源查询）
     */
    private Map<String, Object> callGatewayGetApi(String endpoint, String token) {
        try {
            String url = gatewayBaseUrl + endpoint;
            log.debug("调用网关API GET: {}", url);

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
            log.error("网关API调用异常: {}", e.getMessage());
            throw new RuntimeException("网关API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 调用网关API的通用POST方法（数据提交）
     */
    private boolean callGatewaySubmitApi(String endpoint, Map<String, Object> requestBody, String token) {
        try {
            String url = gatewayBaseUrl + endpoint;
            log.debug("调用网关API提交: {} with body: {}", url, requestBody);

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
            log.error("网关API调用异常: {}", e.getMessage());
            return false;
        }
    }
}