/**
 * 商品实体类
 * 对应数据库中的product表
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * unique: id
 * not null: id, store_id, name, price, stock
 */
@Data
@TableName("product")
public class Product {

    /**
     * 商品ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 商品评分
     */
    private BigDecimal rating;

    /**
     * 商品状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 商品图片
     */
    private String image;
}