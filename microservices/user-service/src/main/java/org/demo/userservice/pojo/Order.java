/**
 * 订单实体类
 * 对应数据库中的order表
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
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
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 店铺ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 骑手ID
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
     * 用户位置
     */
    @TableField("user_location")
    private String userLocation;

    /**
     * 店铺位置
     */
    @TableField("store_location")
    private String storeLocation;

    /**
     * 订单总价
     */
    @TableField("total_price")
    private BigDecimal totalPrice;

    /**
     * 实际支付价格
     */
    @TableField("actual_price")
    private BigDecimal actualPrice;

    /**
     * 配送费
     */
    @TableField("delivery_price")
    private BigDecimal deliveryPrice;

    /**
     * 订单备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 截止时间
     */
    @TableField("deadline")
    private LocalDateTime deadline;

    /**
     * 结束时间
     */
    @TableField("ended_at")
    private LocalDateTime endedAt;
}