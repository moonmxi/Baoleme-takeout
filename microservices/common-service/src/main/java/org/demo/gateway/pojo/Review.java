/**
 * 评论实体类
 * 用于存储用户对商品和店铺的评价信息
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
 * 评论实体类
 * 包含用户评价的所有相关信息
 */
@Data
public class Review {

    /**
     * 评论ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联用户表
     */
    private Long userId;

    /**
     * 店铺ID，关联店铺表
     */
    private Long storeId;

    /**
     * 商品ID，关联商品表（可选）
     */
    private Long productId;

    /**
     * 评分，1-5分
     */
    private BigDecimal rating;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 评论图片URL
     */
    private String image;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}