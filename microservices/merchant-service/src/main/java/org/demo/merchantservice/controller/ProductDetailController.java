/**
 * 商品详情控制器
 * 处理商品详情查看相关的HTTP请求，包括商品信息和评价
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.demo.merchantservice.client.GatewayApiClient;
import org.demo.merchantservice.common.CommonResponse;
import org.demo.merchantservice.common.ResponseBuilder;
import org.demo.merchantservice.dto.request.product.ProductInfoRequest;
import org.demo.merchantservice.dto.response.product.ProductInfoResponse;
import org.demo.merchantservice.dto.response.product.ProductReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品详情控制器类
 * 提供商品详情查看功能的REST API
 */
@Slf4j
@RestController
@RequestMapping("/products")
public class ProductDetailController {

    /**
     * 网关API客户端
     */
    @Autowired
    private GatewayApiClient gatewayApiClient;

    /**
     * 获取商品详情接口
     * 包含商品基本信息、店铺信息和用户评价
     * 
     * @param productId 商品ID
     * @param tokenHeader JWT认证令牌
     * @return 商品详情响应
     */
    @GetMapping("/{productId}")
    public CommonResponse getProductInfo(@PathVariable Long productId, @RequestHeader("Authorization") String tokenHeader) {
        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            // 通过网关API获取商品基本信息
            Map<String, Object> productData = gatewayApiClient.getProductById(productId, token);
            
            if (productData.isEmpty()) {
                return ResponseBuilder.fail("商品不存在或已下架");
            }
            
            // 构建响应对象
            ProductInfoResponse response = buildProductInfoResponse(productData, token);
            
            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            log.error("获取商品信息失败: productId={}", productId, e);
            return ResponseBuilder.fail("获取商品信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据请求体获取商品详情接口（兼容原有API）
     * 
     * @param request 商品信息请求对象
     * @param tokenHeader JWT认证令牌
     * @return 商品详情响应
     */
    @PostMapping("/info")
    public CommonResponse getProductInfoByRequest(@Valid @RequestBody ProductInfoRequest request, @RequestHeader("Authorization") String tokenHeader) {
        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            // 通过网关API获取商品基本信息
            Map<String, Object> productData = gatewayApiClient.getProductById(request.getId(), token);
            
            if (productData.isEmpty()) {
                return ResponseBuilder.fail("商品不存在或已下架");
            }
            
            // 构建响应对象
            ProductInfoResponse response = buildProductInfoResponse(productData, token);
            
            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            log.error("获取商品信息失败: productId={}", request.getId(), e);
            return ResponseBuilder.fail("获取商品信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品评价列表接口
     * 
     * @param productId 商品ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param tokenHeader JWT认证令牌
     * @return 商品评价列表响应
     */
    @GetMapping("/{productId}/reviews")
    public CommonResponse getProductReviews(@PathVariable Long productId,
                                           @RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
                                           @RequestHeader("Authorization") String tokenHeader) {
        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            // 通过网关API获取商品评价
            List<Map<String, Object>> reviewsData = gatewayApiClient.getProductReviews(productId, page, pageSize, token);
            
            List<ProductReviewResponse> reviews = reviewsData.stream()
                    .map(reviewData -> convertToProductReviewResponse(reviewData, token))
                    .collect(Collectors.toList());
            
            return ResponseBuilder.ok(reviews);
        } catch (Exception e) {
            log.error("获取商品评价失败: productId={}", productId, e);
            return ResponseBuilder.fail("获取商品评价失败: " + e.getMessage());
        }
    }

    /**
     * 获取店铺商品列表接口
     * 
     * @param storeId 店铺ID
     * @param category 商品分类（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @param tokenHeader JWT认证令牌
     * @return 店铺商品列表响应
     */
    @GetMapping("/store/{storeId}")
    public CommonResponse getStoreProducts(@PathVariable Long storeId,
                                          @RequestParam(value = "category", required = false) String category,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
                                          @RequestHeader("Authorization") String tokenHeader) {
        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            // 通过网关API获取店铺商品列表
            List<Map<String, Object>> productsData = gatewayApiClient.getStoreProducts(storeId, category, page, pageSize, token);
            
            Map<String, Object> result = new HashMap<>();
            result.put("products", productsData);
            result.put("current_page", page);
            result.put("page_size", pageSize);
            result.put("total_count", productsData.size());
            
            return ResponseBuilder.ok(result);
        } catch (Exception e) {
            log.error("获取店铺商品失败: storeId={}", storeId, e);
            return ResponseBuilder.fail("获取店铺商品失败: " + e.getMessage());
        }
    }

    /**
     * 构建商品详情响应对象
     * 包含商品基本信息和评价列表，保持与原始项目一致的格式
     * 
     * @param productData 商品数据
     * @param token JWT认证令牌
     * @return ProductInfoResponse 商品详情响应对象
     */
    private ProductInfoResponse buildProductInfoResponse(Map<String, Object> productData, String token) {
        ProductInfoResponse response = new ProductInfoResponse();
        
        // 设置商品基本信息
        response.setId(((Number) productData.get("id")).longValue());
        response.setName((String) productData.get("name"));
        response.setCategory((String) productData.get("category"));
        response.setDescription((String) productData.get("description"));
        response.setImage((String) productData.get("image"));
        
        Object priceObj = productData.get("price");
        if (priceObj != null) {
            response.setPrice(new BigDecimal(priceObj.toString()));
        }
        
        Object ratingObj = productData.get("rating");
        if (ratingObj != null) {
            response.setRating(new BigDecimal(ratingObj.toString()));
        }
        
        Object stockObj = productData.get("stock");
        if (stockObj != null) {
            response.setStock(((Number) stockObj).intValue());
        }
        
        Object statusObj = productData.get("status");
        if (statusObj != null) {
            response.setStatus(((Number) statusObj).intValue());
        }
        
        Object storeIdObj = productData.get("store_id");
        if (storeIdObj != null) {
            response.setStoreId(((Number) storeIdObj).longValue());
        }
        
        Object createdAtObj = productData.get("created_at");
        if (createdAtObj != null) {
            response.setCreatedAt(LocalDateTime.parse(createdAtObj.toString()));
        }
        
        try {
            // 获取商品评价列表（前5条）
            List<Map<String, Object>> reviewsData = gatewayApiClient.getProductReviews(response.getId(), 1, 5, token);
            List<ProductReviewResponse> reviews = reviewsData.stream()
                    .map(reviewData -> convertToProductReviewResponse(reviewData, token))
                    .collect(Collectors.toList());
            response.setReviews(reviews);
            
            // 设置评价统计
             response.setReviewCount(reviews.size());
             if (!reviews.isEmpty()) {
                 double avgRating = reviews.stream()
                         .mapToInt(ProductReviewResponse::getRating)
                         .average()
                         .orElse(0.0);
                 response.setAverageRating(BigDecimal.valueOf(avgRating).setScale(2, BigDecimal.ROUND_HALF_UP));
             } else {
                 response.setAverageRating(BigDecimal.ZERO);
             }
        } catch (Exception e) {
            log.warn("获取商品评价失败，使用默认值: productId={}", response.getId(), e);
            response.setReviews(new ArrayList<>());
            response.setReviewCount(0);
            response.setAverageRating(BigDecimal.ZERO);
        }
        
        return response;
    }

    /**
     * 转换评价数据为响应对象
     * 
     * @param reviewData 评价数据
     * @param token JWT认证令牌，用于获取用户信息
     * @return ProductReviewResponse 评价响应对象
     */
    private ProductReviewResponse convertToProductReviewResponse(Map<String, Object> reviewData, String token) {
        ProductReviewResponse review = new ProductReviewResponse();
        
        Object idObj = reviewData.get("id");
        if (idObj != null) {
            review.setId(((Number) idObj).longValue());
        }
        
        Object userIdObj = reviewData.get("user_id");
        if (userIdObj != null) {
            Long userId = ((Number) userIdObj).longValue();
            review.setUserId(userId);
            
            // 获取用户详细信息
            try {
                Map<String, Object> userData = gatewayApiClient.getUserById(userId, token);
                if (!userData.isEmpty()) {
                    review.setUsername((String) userData.get("username"));
                    review.setUserAvatar((String) userData.get("avatar"));
                }
            } catch (Exception e) {
                log.warn("获取用户信息失败，使用默认值: userId={}", userId, e);
                // 用户信息获取失败时，保持username和userAvatar为null
            }
        }
        
        review.setComment((String) reviewData.get("comment"));
        
        Object ratingObj = reviewData.get("rating");
         if (ratingObj != null) {
             review.setRating(((Number) ratingObj).intValue());
         }
        
        Object createdAtObj = reviewData.get("created_at");
        if (createdAtObj != null) {
            review.setCreatedAt(LocalDateTime.parse(createdAtObj.toString()));
        }
        
        return review;
    }
}