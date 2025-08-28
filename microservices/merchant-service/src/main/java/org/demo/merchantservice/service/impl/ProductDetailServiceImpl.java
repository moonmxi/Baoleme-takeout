/**
 * 商品详情服务实现类
 * 实现商品详情查看相关的业务逻辑，包括商品详情查看、评价查询等功能
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.service.impl;

import org.demo.merchantservice.dto.response.product.ProductInfoResponse;
import org.demo.merchantservice.dto.response.product.ProductReviewResponse;
import org.demo.merchantservice.mapper.ProductDetailMapper;
import org.demo.merchantservice.pojo.Product;
import org.demo.merchantservice.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品详情服务实现类
 * 提供商品详情查看相关的业务逻辑处理
 */
@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    /**
     * 商品详情数据访问接口
     */
    @Autowired
    private ProductDetailMapper productDetailMapper;

    /**
     * 获取商品详情信息
     * 包含商品基本信息、店铺信息和评价列表
     * 
     * @param productId 商品ID
     * @return ProductInfoResponse 商品详情响应对象，不存在返回null
     */
    @Override
    public ProductInfoResponse getProductInfo(Long productId) {
        try {
            // 获取商品基本信息
            Product product = productDetailMapper.selectById(productId);
            if (product == null || product.getStatus() != 1) {
                return null;
            }

            // 获取商品详细信息（包含店铺信息）
            ProductInfoResponse response = productDetailMapper.getProductDetailInfo(productId);
            if (response == null) {
                return null;
            }

            // 获取商品评价列表（前5条）
            List<ProductReviewResponse> reviews = productDetailMapper.getProductReviews(productId, 0, 5);
            response.setReviews(reviews);

            // 获取评价统计信息
            Map<String, Object> reviewStats = getProductReviewStats(productId);
            response.setReviewCount((Integer) reviewStats.get("review_count"));
            response.setAverageRating((BigDecimal) reviewStats.get("average_rating"));

            return response;
        } catch (Exception e) {
            throw new RuntimeException("获取商品详情失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取商品评价列表
     * 
     * @param productId 商品ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<ProductReviewResponse> 商品评价列表
     */
    @Override
    public List<ProductReviewResponse> getProductReviews(Long productId, int page, int pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            return productDetailMapper.getProductReviews(productId, offset, pageSize);
        } catch (Exception e) {
            throw new RuntimeException("获取商品评价失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取店铺商品列表
     * 
     * @param storeId 店铺ID
     * @param category 商品分类（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return Map<String, Object> 店铺商品列表响应
     */
    @Override
    public Map<String, Object> getStoreProducts(Long storeId, String category, int page, int pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            List<Product> products = productDetailMapper.getStoreProducts(storeId, category, offset, pageSize);
            
            // 获取总数
            Integer totalCount = productDetailMapper.getStoreProductCount(storeId, category);
            
            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("total_count", totalCount);
            result.put("current_page", page);
            result.put("page_size", pageSize);
            result.put("total_pages", (totalCount + pageSize - 1) / pageSize);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("获取店铺商品失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID获取商品基本信息
     * 
     * @param productId 商品ID
     * @return Product 商品对象，不存在返回null
     */
    @Override
    public Product getProductById(Long productId) {
        try {
            return productDetailMapper.selectById(productId);
        } catch (Exception e) {
            throw new RuntimeException("获取商品信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查商品是否存在且可用
     * 
     * @param productId 商品ID
     * @return boolean 商品是否可用
     */
    @Override
    public boolean isProductAvailable(Long productId) {
        try {
            Product product = productDetailMapper.selectById(productId);
            return product != null && product.getStatus() == 1 && product.getStock() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取商品评价统计信息
     * 
     * @param productId 商品ID
     * @return Map<String, Object> 评价统计信息
     */
    @Override
    public Map<String, Object> getProductReviewStats(Long productId) {
        try {
            Map<String, Object> stats = productDetailMapper.getProductReviewStats(productId);
            if (stats == null) {
                stats = new HashMap<>();
                stats.put("review_count", 0);
                stats.put("average_rating", BigDecimal.ZERO);
            }
            return stats;
        } catch (Exception e) {
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("review_count", 0);
            defaultStats.put("average_rating", BigDecimal.ZERO);
            return defaultStats;
        }
    }
}