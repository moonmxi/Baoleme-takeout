package org.demo.baoleme.dto.request.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 骑手更新订单状态请求
 * 使用下划线命名策略以匹配前端期望的JSON格式
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderStatusRiderUpdateRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "目标状态不能为空")
    private Integer targetStatus;

}