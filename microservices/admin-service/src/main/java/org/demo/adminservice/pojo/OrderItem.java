/**
 * 订单项实体类
 * 对应数据库中的order_item表，包含订单项的基本信息
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.adminservice.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项实体类
 * 包含订单项的基本信息，如订单ID、商品ID、数量等
 */
@Data
@TableName("order_item")
public class OrderItem {

    /**
     * 订单ID，外键关联order表
     */
    private Long orderId;

    /**
     * 商品ID，外键关联product表
     */
    private Long productId;

    /**
     * 商品数量
     */
    private Integer quantity;
}