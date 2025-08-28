/**
 * 商品详情服务接口
 * 定义商品详情查看相关的业务操作方法，包括商品详情查看、评价查询等功能
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.service;

import org.demo.merchantservice.dto.response.product.ProductInfoResponse;
import org.demo.merchantservice.dto.response.product.ProductReviewResponse;
import org.demo.merchantservice.pojo.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品详情服务接口
 * 提供商品详情查看相关的业务逻辑处理方法
 */
public interface ProductDetailService {

    /**
     * 获取商品详情信息
     * 包含商品基本信息、店铺信息和评价列表
     * 
     * @param productId 商品ID
     * @return ProductInfoResponse 商品详情响应对象，不存在返回null
     */
    ProductInfoResponse getProductInfo(Long productId);

    /**
     * 获取商品评价列表
     * 
     * @param productId 商品ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<ProductReviewResponse> 商品评价列表
     */
    List<ProductReviewResponse> getProductReviews(Long productId, int page, int pageSize);

    /**
     * 获取店铺商品列表
     * 
     * @param storeId 店铺ID
     * @param category 商品分类（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return Map<String, Object> 店铺商品列表响应
     */
    Map<String, Object> getStoreProducts(Long storeId, String category, int page, int pageSize);

    /**
     * 根据ID获取商品基本信息
     * 
     * @param productId 商品ID
     * @return Product 商品对象，不存在返回null
     */
    Product getProductById(Long productId);

    /**
     * 检查商品是否存在且可用
     * 
     * @param productId 商品ID
     * @return boolean 商品是否可用
     */
    boolean isProductAvailable(Long productId);

    /**
     * 获取商品评价统计信息
     * 
     * @param productId 商品ID
     * @return Map<String, Object> 评价统计信息
     */
    Map<String, Object> getProductReviewStats(Long productId);
}