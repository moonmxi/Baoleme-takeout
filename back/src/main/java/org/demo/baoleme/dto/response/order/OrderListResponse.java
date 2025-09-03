package org.demo.baoleme.dto.response.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 可抢订单列表响应
 * 使用下划线命名策略以匹配前端期望的JSON格式
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderListResponse {
    private List<OrderBrief> orders;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OrderBrief {
        private Long orderId;
        private String shopName;
        private String shopLocation;
        private String deliveryAddress;
        private BigDecimal totalAmount;
        private Integer estimatedTime;
    }
}