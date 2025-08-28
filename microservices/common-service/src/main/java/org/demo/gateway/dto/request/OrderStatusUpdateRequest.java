/**
 * 订单状态更新请求DTO
 * 用于更新订单状态的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单状态更新请求类
 * 包含更新订单状态所需的参数
 */
@Data
public class OrderStatusUpdateRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 目标状态
     * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
     */
    @NotNull(message = "目标状态不能为空")
    private Integer targetStatus;

    /**
     * 取消原因（当状态为取消时必填）
     */
    private String cancelReason;
}