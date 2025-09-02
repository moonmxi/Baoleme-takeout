/**
 * 用户提交评论请求DTO
 * 用于接收用户提交评论的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 用户提交评论请求DTO
 * 包含评论所需的所有参数
 */
@Data
public class UserReviewRequest {
    
    /**
     * 店铺ID
     */
    private Long storeId;
    
    /**
     * 商品ID（可选）
     */
    private Long productId;
    
    /**
     * 评分，1-5分
     */
    private int rating;
    
    /**
     * 评论内容
     */
    private String comment;
    
    /**
     * 评论图片列表
     */
    private List<String> images;
}