/**
 * 商家查看评论请求DTO
 * 用于接收商家查看店铺评论的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 商家查看评论请求DTO
 * 包含分页和筛选参数
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReviewReadRequest {
    
    /**
     * 店铺ID
     */
    private Long storeId;
    
    /**
     * 页码，从1开始
     */
    private int page = 1;
    
    /**
     * 每页数量
     */
    private int pageSize = 10;
    
    /**
     * 筛选类型（可选）
     */
    private ReviewFilterType type;
    
    /**
     * 是否有图片筛选（可选）
     */
    private Boolean hasImage;
    
    /**
     * 评论筛选类型枚举
     */
    public enum ReviewFilterType {
        /**
         * 好评（4-5分）
         */
        POSITIVE,
        
        /**
         * 差评（1-2分）
         */
        NEGATIVE
    }
}