/**
 * 用户浏览历史响应类
 * 用于返回用户浏览历史信息
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.dto.response.user;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户浏览历史响应类
 */
@Data
public class UserViewHistoryResponse {
    
    /**
     * 店铺ID
     */
    private Long storeId;
    
    /**
     * 店铺名称
     */
    private String storeName;
    
    /**
     * 店铺类型
     */
    private String storeType;
    
    /**
     * 店铺描述
     */
    private String storeDescription;
    
    /**
     * 店铺位置
     */
    private String storeLocation;
    
    /**
     * 店铺评分
     */
    private BigDecimal rating;
    
    /**
     * 店铺状态
     */
    private Integer status;
    
    /**
     * 平均价格
     */
    private BigDecimal avgPrice;
    
    /**
     * 店铺图片
     */
    private String image;
    
    /**
     * 浏览时间
     */
    private LocalDateTime viewTime;
}