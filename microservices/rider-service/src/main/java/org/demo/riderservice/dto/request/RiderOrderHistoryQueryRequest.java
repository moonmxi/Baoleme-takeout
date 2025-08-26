package org.demo.riderservice.dto.request.rider;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手订单历史查询请求
 */
@Data
public class RiderOrderHistoryQueryRequest {
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer page;
    private Integer pageSize;
}