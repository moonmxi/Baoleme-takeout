package org.demo.adminservice.dto.request.admin;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 管理员搜索请求DTO
 * 与用户搜索请求保持一致的字段结构
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
@Data
public class AdminSearchRequest {
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 距离范围
     */
    private BigDecimal distance;
    
    /**
     * 期望价格
     */
    private BigDecimal wishPrice;
    
    /**
     * 最低评分
     */
    private BigDecimal startRating;
    
    /**
     * 最高评分
     */
    private BigDecimal endRating;
    
    /**
     * 页码，默认为1
     */
    private Integer page = 1;
    
    /**
     * 每页大小，默认为10
     */
    private Integer pageSize = 10;
}