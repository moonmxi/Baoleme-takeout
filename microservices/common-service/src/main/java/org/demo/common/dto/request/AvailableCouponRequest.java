/**
 * 可用优惠券查询请求DTO
 * 用于接收查询可用优惠券的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 可用优惠券查询请求类
 * 包含查询可用优惠券所需的参数
 */
@Data
public class AvailableCouponRequest {
    
    /**
     * 店铺ID
     */
    @JsonProperty("store_id")
    private Long storeId;
}