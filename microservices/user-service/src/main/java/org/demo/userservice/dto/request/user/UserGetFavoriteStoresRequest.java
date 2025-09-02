/**
 * 用户获取收藏店铺请求DTO
 * 用于用户查询收藏店铺列表的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.userservice.dto.request.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户获取收藏店铺请求类
 * 包含查询收藏店铺所需的筛选条件和分页参数
 */
@Data
public class UserGetFavoriteStoresRequest {
    
    /**
     * 店铺类型（可选）
     */
    private String type;
    
    /**
     * 距离范围（可选）
     */
    private BigDecimal distance;
    
    /**
     * 期望价格（可选）
     */
    private BigDecimal wishPrice;
    
    /**
     * 最低评分（可选）
     */
    private BigDecimal startRating;
    
    /**
     * 最高评分（可选）
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
