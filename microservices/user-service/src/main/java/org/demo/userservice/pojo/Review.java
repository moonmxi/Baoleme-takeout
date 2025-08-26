/**
 * 评价实体类
 * 对应数据库中的review表
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评价实体类
 * 用于存储用户对商品和店铺的评价信息
 */
@Data
@TableName("review")
public class Review {

    /**
     * 评价ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 评价图片
     */
    private String image;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}