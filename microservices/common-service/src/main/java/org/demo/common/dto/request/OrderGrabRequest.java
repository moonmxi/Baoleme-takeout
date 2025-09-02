/**
 * 订单抢单请求DTO
 * 用于骑手抢单操作的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单抢单请求类
 * 包含骑手抢单所需的参数
 */
@Data
public class OrderGrabRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}