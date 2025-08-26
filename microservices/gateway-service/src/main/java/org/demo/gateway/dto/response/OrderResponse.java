/**
 * 订单响应DTO
 * 用于返回订单信息的响应对象
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应类
 * 包含订单的详细信息
 */
@Data
public class OrderResponse {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * 订单状态
     * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
     */
    private Integer status;

    /**
     * 用户位置
     */
    private String userLocation;

    /**
     * 店铺位置
     */
    private String storeLocation;

    /**
     * 订单总价格
     */
    private BigDecimal totalPrice;

    /**
     * 实际支付价格
     */
    private BigDecimal actualPrice;

    /**
     * 配送费用
     */
    private BigDecimal deliveryPrice;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 订单创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 订单截止时间
     */
    private LocalDateTime deadline;

    /**
     * 订单结束时间
     */
    private LocalDateTime endedAt;

    /**
     * 订单项列表
     */
    private List<OrderItemResponse> items;

    /**
     * 订单项响应类
     */
    @Data
    public static class OrderItemResponse {
        /**
         * 商品ID
         */
        private Long productId;

        /**
         * 商品名称
         */
        private String productName;

        /**
         * 商品数量
         */
        private Integer quantity;

        /**
         * 商品单价
         */
        private BigDecimal price;
    }
}