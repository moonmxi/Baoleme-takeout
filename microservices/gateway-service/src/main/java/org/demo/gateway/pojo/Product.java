/**
 * 商品实体类
 * 对应数据库中的product表，用于存储商品信息
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 存储商品的基本信息
 */
@Data
@TableName("product")
public class Product {

    /**
     * 商品ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺ID，外键关联店铺表
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;

    /**
     * 商品价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 商品分类
     */
    @TableField("category")
    private String category;

    /**
     * 商品库存
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 商品评分
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 商品状态（0-下架，1-上架）
     */
    @TableField("status")
    private Integer status;

    /**
     * 商品图片URL
     */
    @TableField("image")
    private String image;

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