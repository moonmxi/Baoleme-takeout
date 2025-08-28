/**
 * 商品评价响应DTO
 * 用于封装商品评价信息
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.dto.response.product;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品评价响应数据传输对象
 */
@Data
public class ProductReviewResponse {
    
    /**
     * 评价ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名称
     */
    private String username;
    
    /**
     * 用户头像
     */
    private String userAvatar;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 店铺ID
     */
    private Long storeId;
    
    /**
     * 评分（1-5星）
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 评价图片列表
     */
    private List<String> images;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 是否匿名评价
     */
    private Boolean anonymous;
}