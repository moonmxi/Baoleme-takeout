package org.demo.baoleme.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 骑手抢单请求
 * 用于接收骑手抢单时的请求参数
 * 通过全局Jackson配置自动支持下划线到驼峰的转换
 */
@Data
public class OrderGrabRequest {
    /**
     * 订单ID
     * 前端传递order_id会自动映射到orderId字段
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}