package org.demo.baoleme.dto.response.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手订单状态更新响应
 * 使用下划线命名策略以匹配前端期望的JSON格式
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderStatusRiderUpdateResponse {
    private Long orderId;
    private Integer status;
    private LocalDateTime updatedAt;
}