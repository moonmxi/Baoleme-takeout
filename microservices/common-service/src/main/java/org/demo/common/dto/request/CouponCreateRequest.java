/**
 * 优惠券创建请求DTO
 * 用于接收创建优惠券的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券创建请求类
 * 包含创建优惠券所需的所有参数
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponCreateRequest {
    
    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    @JsonProperty("store_id")
    private Long storeId;

    /**
     * 优惠券类型
     * 1-折扣券 2-满减券
     */
    @NotNull(message = "优惠券类型不能为空")
    private Integer type;
    
    /**
     * 折扣率（type=1时有效）
     */
    private BigDecimal discount;
    
    /**
     * 满减条件金额（type=2时有效）
     */
    @JsonProperty("full_amount")
    private BigDecimal fullAmount;
    
    /**
     * 满减优惠金额（type=2时有效）
     */
    @JsonProperty("reduce_amount")
    private BigDecimal reduceAmount;
}