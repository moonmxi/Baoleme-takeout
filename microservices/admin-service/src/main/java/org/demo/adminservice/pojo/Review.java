/**
 * 评论实体类
 * 对应数据库中的review表，包含评论的基本信息
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.adminservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评论实体类
 * 包含评论的基本信息，如用户ID、店铺ID、商品ID、评分、评论内容等
 */
@Data
public class Review {

    /**
     * 评论ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，外键关联user表
     */
    private Long userId;

    /**
     * 店铺ID，外键关联store表
     */
    private Long storeId;

    /**
     * 商品ID，外键关联product表
     */
    private Long productId;

    /**
     * 评分，使用BigDecimal精确表示
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
     * 评论创建时间
     */
    private LocalDateTime createdAt;
}