/**
 * 订单实体类
 * 对应数据库中的order表，包含订单的基本信息
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.adminservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 包含订单的基本信息，如用户ID、店铺ID、骑手ID、状态、价格等
 */
@Data
@TableName("`order`")
public class Order {

    /**
     * 订单ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，外键关联user表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 店铺ID，外键关联store表
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 骑手ID，外键关联rider表
     */
    @TableField("rider_id")
    private Long riderId;

    /**
     * 订单状态
     * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
     */
    @TableField("status")
    private Integer status;

    /**
     * 用户位置信息
     */
    @TableField("user_location")
    private String userLocation;

    /**
     * 店铺位置信息
     */
    @TableField("store_location")
    private String storeLocation;

    /**
     * 订单总价格
     */
    @TableField("total_price")
    private BigDecimal totalPrice;

    /**
     * 实际支付价格
     */
    @TableField("actual_price")
    private BigDecimal actualPrice;

    /**
     * 配送费用
     */
    @TableField("delivery_price")
    private BigDecimal deliveryPrice;

    /**
     * 订单备注信息
     */
    @TableField("remark")
    private String remark;

    /**
     * 订单创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 订单截止时间
     */
    @TableField("deadline")
    private LocalDateTime deadline;

    /**
     * 订单结束时间
     */
    @TableField("ended_at")
    private LocalDateTime endedAt;
}