/**
 * 订单创建请求DTO
 * 用于接收用户创建订单的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单创建请求类
 * 包含创建订单所需的所有参数
 */
@Data
public class OrderCreateRequest {

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long storeId;

    /**
     * 用户位置信息
     */
    @NotNull(message = "用户位置不能为空")
    private String userLocation;

    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 优惠券ID（可选）
     */
    private Long couponId;
    
    /**
     * 配送费
     */
    private BigDecimal deliveryPrice;
    
    /**
     * 订单截止时间
     */
    private LocalDateTime deadline;

    /**
     * 订单项列表
     */
    @NotNull(message = "订单项不能为空")
    @Size(min = 1, message = "至少需要一个订单项")
    private List<CartItemDTO> items;

    /**
     * 购物车项DTO
     */
    @Data
    public static class CartItemDTO {
        /**
         * 商品ID
         */
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        /**
         * 商品数量
         */
        @NotNull(message = "商品数量不能为空")
        private Integer quantity;

        /**
         * 商品单价
         */
        @NotNull(message = "商品价格不能为空")
        private BigDecimal price;

        /**
         * 商品名称
         */
        @NotNull(message = "商品名称不能为空")
        private String productName;
    }
}