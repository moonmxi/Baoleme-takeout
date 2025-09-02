/**
 * 用户领取优惠券请求DTO
 * 用于接收用户领取优惠券的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.dto.request;

import lombok.Data;

/**
 * 用户领取优惠券请求类
 * 包含领取优惠券所需的参数
 */
@Data
public class UserClaimCouponRequest {
    
    /**
     * 优惠券ID
     */
    private Long id;
}