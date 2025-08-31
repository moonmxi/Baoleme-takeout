/**
 * 评论分页响应DTO
 * 用于返回分页查询的评论列表结果
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.gateway.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 评论分页响应DTO
 * 包含分页信息和评论列表
 */
@Data
public class ReviewPageResponse {
    
    /**
     * 当前页码
     */
    private Integer currentPage;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 总记录数
     */
    private Integer totalCount;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 上一页页码
     */
    private Integer prePage;
    
    /**
     * 下一页页码
     */
    private Integer nextPage;
    
    /**
     * 评论列表
     */
    private List<ReviewReadResponse> reviews;
}