package org.demo.adminservice.dto.response.admin;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员搜索响应DTO
 * 与用户搜索响应保持一致的字段结构
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
@Data
public class AdminSearchResponse {
    /**
     * 店铺ID
     */
    private Long storeId;
    
    /**
     * 店铺名称
     */
    private String name;
    
    /**
     * 店铺类型
     */
    private String type;
    
    /**
     * 店铺描述
     */
    private String description;
    
    /**
     * 店铺位置
     */
    private String location;
    
    /**
     * 店铺评分
     */
    private BigDecimal rating;
    
    /**
     * 店铺状态
     */
    private Integer status;
    
    /**
     * 店铺图片
     */
    private String image;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}