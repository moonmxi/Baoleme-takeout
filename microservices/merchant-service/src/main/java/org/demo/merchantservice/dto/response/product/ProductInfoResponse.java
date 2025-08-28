/**
 * 商品信息响应DTO
 * 用于封装商品详情信息，包括商品基本信息、店铺信息和评价列表
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.dto.response.product;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品信息响应数据传输对象
 */
@Data
public class ProductInfoResponse {
    
    /**
     * 商品ID
     */
    private Long id;
    
    /**
     * 店铺ID
     */
    private Long storeId;
    
    /**
     * 店铺名称
     */
    private String storeName;
    
    /**
     * 店铺地址
     */
    private String storeLocation;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品分类
     */
    private String category;
    
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 商品图片URL
     */
    private String image;
    
    /**
     * 商品库存
     */
    private Integer stock;
    
    /**
     * 商品评分
     */
    private BigDecimal rating;
    
    /**
     * 商品状态
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 商品评价列表
     */
    private List<ProductReviewResponse> reviews;
    
    /**
     * 评价总数
     */
    private Integer reviewCount;
    
    /**
     * 平均评分
     */
    private BigDecimal averageRating;
}