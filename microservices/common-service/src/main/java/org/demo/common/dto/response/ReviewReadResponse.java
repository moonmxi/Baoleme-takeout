/**
 * 评论详情响应DTO
 * 用于返回单个评论的详细信息
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评论详情响应DTO
 * 包含评论的完整信息，包括用户和商品信息
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReviewReadResponse {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 评论内容
     */
    private String comment;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 用户头像
     */
    private String userAvatar;
    
    /**
     * 评论图片
     */
    private String image;
}