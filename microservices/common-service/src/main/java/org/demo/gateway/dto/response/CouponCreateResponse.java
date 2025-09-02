/**
 * 优惠券创建响应DTO
 * 用于返回优惠券创建结果
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 优惠券创建响应类
 * 包含创建成功后的优惠券信息
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponCreateResponse {
    
    /**
     * 优惠券ID
     */
    private Long couponId;
}