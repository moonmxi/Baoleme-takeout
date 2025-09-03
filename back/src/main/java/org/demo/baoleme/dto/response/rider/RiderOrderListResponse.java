package org.demo.baoleme.dto.response.rider;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 骑手订单历史列表响应
 * 使用下划线命名策略以匹配前端期望的JSON格式
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RiderOrderListResponse {
    private List<OrderRecord> orders;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OrderRecord {
        private Long orderId;
        private String status;
        private BigDecimal totalAmount;
        private BigDecimal deliveryFee;
        private LocalDateTime completedAt;
    }
}