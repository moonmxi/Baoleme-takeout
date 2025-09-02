/**
 * 商品实体类
 * 用于存储商品信息
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.gateway.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 包含商品的基本信息和状态
 */
@Data
public class Product {

    /**
     * 商品ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺ID，关联店铺表
     */
    private Long storeId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片URL
     */
    private String image;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 商品评分
     */
    private BigDecimal rating;

    /**
     * 商品状态（0-下架，1-上架）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}