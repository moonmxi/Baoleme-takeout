/**
 * 购物车实体类
 * 对应数据库中的cart表，用于存储用户购物车信息
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 * 存储用户购物车中的商品信息
 */
@Data
@TableName("cart")
public class Cart {

    /**
     * 购物车项ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，外键关联用户表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 商品ID，外键关联商品表
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
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}