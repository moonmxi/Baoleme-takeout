/**
 * 骑手订单历史查询请求DTO
 * 用于骑手查询订单历史记录的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 骑手订单历史查询请求类
 * 支持按状态、时间范围等条件查询订单历史
 */
@Data
public class RiderOrderHistoryQueryRequest {

    /**
     * 订单状态（可选）
     * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
     */
    private Integer status;

    /**
     * 开始时间（可选）
     * 格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-ddTHH:mm:ss
     */
    private String startTime;

    /**
     * 结束时间（可选）
     * 格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-ddTHH:mm:ss
     */
    private String endTime;

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page;

    /**
     * 每页大小
     */
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize;
}