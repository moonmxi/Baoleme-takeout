/**
 * 订单详情查询请求DTO
 * 用于接收订单详情查询的请求参数
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单详情查询请求类
 * 包含查询订单详情所需的参数
 */
@Data
public class OrderDetailRequest {
    
    /**
     * 订单ID
     * 不能为空，用于标识要查询的订单
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
}