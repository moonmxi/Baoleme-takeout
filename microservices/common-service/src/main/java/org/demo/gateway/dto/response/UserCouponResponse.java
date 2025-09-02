/**
 * 用户优惠券响应DTO
 * 用于返回用户优惠券信息
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户优惠券响应类
 * 包含用户优惠券的详细信息
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCouponResponse {
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 优惠券类型
     */
    private String type;
    
    /**
     * 折扣率
     */
    private BigDecimal discount;
    
    /**
     * 过期时间
     */
    private Date expirationDate;
    
    /**
     * 满减条件金额
     */
    private Integer fullAmount;
    
    /**
     * 满减优惠金额
     */
    private Integer reduceAmount;
}