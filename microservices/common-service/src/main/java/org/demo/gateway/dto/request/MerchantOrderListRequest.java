/**
 * 商家订单列表查询请求DTO
 * 用于商家查询店铺订单列表的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商家订单列表查询请求类
 * 包含查询店铺订单所需的参数
 */
@Data
public class MerchantOrderListRequest {

    /**
     * 店铺ID
     */
    @JsonProperty("store_id")
    @NotNull(message = "店铺ID不能为空")
    private Long storeId;

    /**
     * 订单状态（可选）
     * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
     */
    private Integer status;

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @JsonProperty("page_size")
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 10;
}