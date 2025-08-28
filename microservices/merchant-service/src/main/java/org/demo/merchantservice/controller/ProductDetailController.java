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
import org.demo.merchantservice.common.CommonResponse;
import org.demo.merchantservice.common.ResponseBuilder;
import org.demo.merchantservice.dto.request.product.ProductInfoRequest;
import org.demo.merchantservice.dto.response.product.ProductInfoResponse;
import org.demo.merchantservice.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.demo.merchantservice.dto.response.product.ProductReviewResponse;

/**
 * 商品详情控制器类
 * 提供商品详情查看功能的REST API
 */
@RestController
@RequestMapping("/products")
public class ProductDetailController {

    /**
     * 商品详情服务接口
     */
    @Autowired
    private ProductDetailService productDetailService;

    /**
     * 获取商品详情接口
     * 包含商品基本信息、店铺信息和用户评价
     * 
     * @param productId 商品ID
     * @return 商品详情响应
     */
    @GetMapping("/{productId}")
    public CommonResponse getProductInfo(@PathVariable Long productId) {
        try {
            ProductInfoResponse response = productDetailService.getProductInfo(productId);
            
            if (response == null) {
                return ResponseBuilder.fail("商品不存在或已下架");
            }
            
            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            return ResponseBuilder.fail("获取商品信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据请求体获取商品详情接口（兼容原有API）
     * 
     * @param request 商品信息请求对象
     * @return 商品详情响应
     */
    @PostMapping("/info")
    public CommonResponse getProductInfoByRequest(@Valid @RequestBody ProductInfoRequest request) {
        try {
            ProductInfoResponse response = productDetailService.getProductInfo(request.getId());
            
            if (response == null) {
                return ResponseBuilder.fail("商品不存在或已下架");
            }
            
            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            return ResponseBuilder.fail("获取商品信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品评价列表接口
     * 
     * @param productId 商品ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 商品评价列表响应
     */
    @GetMapping("/{productId}/reviews")
    public CommonResponse getProductReviews(@PathVariable Long productId,
                                           @RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        try {
            List<ProductReviewResponse> reviews = productDetailService.getProductReviews(productId, page, pageSize);
            return ResponseBuilder.ok(reviews);
        } catch (Exception e) {
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
     * @return 店铺商品列表响应
     */
    @GetMapping("/store/{storeId}")
    public CommonResponse getStoreProducts(@PathVariable Long storeId,
                                          @RequestParam(value = "category", required = false) String category,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> products = productDetailService.getStoreProducts(storeId, category, page, pageSize);
            return ResponseBuilder.ok(products);
        } catch (Exception e) {
            return ResponseBuilder.fail("获取店铺商品失败: " + e.getMessage());
        }
    }
}