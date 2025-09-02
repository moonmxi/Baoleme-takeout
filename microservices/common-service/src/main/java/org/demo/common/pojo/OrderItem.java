/**
 * 订单项实体类
 * 对应数据库中的order_item表，包含订单项的详细信息
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项实体类
 * 包含订单项的基本信息，如订单ID、商品ID、数量、价格等
 */
@Data
@TableName("order_item")
public class OrderItem {

    /**
     * 订单ID，外键关联order表
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 商品ID，外键关联product表
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 商品单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 商品名称（冗余字段，避免商品删除后无法显示）
     */
    @TableField("product_name")
    private String productName;
}