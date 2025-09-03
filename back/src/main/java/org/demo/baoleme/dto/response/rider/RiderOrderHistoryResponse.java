package org.demo.baoleme.dto.response.rider;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手历史订单响应项
 * 使用下划线命名策略以匹配前端期望的JSON格式
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RiderOrderHistoryResponse {
    private Long orderId;
    private Long userId;
    private Integer status;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private LocalDateTime createdAt;
    private String storeLocation;
    private String userLocation;
}
