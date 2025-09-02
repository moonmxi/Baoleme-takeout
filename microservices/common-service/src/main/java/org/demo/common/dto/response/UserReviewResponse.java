/**
 * 用户提交评论响应DTO
 * 用于返回用户提交评论后的结果信息
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 用户提交评论响应DTO
 * 包含评论提交成功后的相关信息
 */
@Data
public class UserReviewResponse {
    
    /**
     * 评分
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