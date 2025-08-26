/**
 * 订单明细实体类
 * 对应数据库中的order_item表
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 订单明细实体类
 * 记录订单中包含的商品信息
 */
@Data
@TableName("order_item")
public class OrderItem {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品数量
     */
    private Integer quantity;
}