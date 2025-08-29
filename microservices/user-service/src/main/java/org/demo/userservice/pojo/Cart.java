/**
 * 购物车实体类
 * 对应数据库中的cart表
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
@TableName("cart")
public class Cart {

    /**
     * 用户ID（联合主键之一）
     */
    @TableId("user_id")
    private Long userId;

    /**
     * 商品ID（联合主键之一）
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}