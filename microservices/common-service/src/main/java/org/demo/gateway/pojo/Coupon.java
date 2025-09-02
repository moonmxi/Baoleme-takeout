/**
 * 优惠券实体类
 * 对应数据库中的coupon表，包含优惠券的详细信息
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 * 包含优惠券的基本信息，如类型、折扣、有效期等
 */
@Data
@TableName("coupon")
public class Coupon {
    
    /**
     * 优惠券类型 - 折扣券
     */
    public static final int TYPE_DISCOUNT = 1;

    /**
     * 优惠券类型 - 满减券
     */
    public static final int TYPE_FULL_REDUCTION = 2;

    /**
     * 优惠券ID，主键自增
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
     * 优惠券类型
     * 1-折扣券 2-满减券
     */
    @TableField("type")
    private Integer type;

    /**
     * 折扣率（type=1时有效）
     */
    @TableField("discount")
    private BigDecimal discount;

    /**
     * 满减条件金额（type=2时有效）
     */
    @TableField("full_amount")
    private BigDecimal fullAmount;

    /**
     * 满减优惠金额（type=2时有效）
     */
    @TableField("reduce_amount")
    private BigDecimal reduceAmount;

    /**
     * 优惠券过期时间
     */
    @TableField("expiration_date")
    private LocalDateTime expirationDate;

    /**
     * 优惠券创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 是否已使用
     */
    @TableField("is_used")
    private Boolean isUsed;

    // 业务方法

    /**
     * 检查优惠券是否已过期
     * 
     * @return boolean 是否过期
     */
    public boolean isExpired() {
        return expirationDate != null && LocalDateTime.now().isAfter(expirationDate);
    }

    /**
     * 检查优惠券是否有效（未过期且未使用）
     * 
     * @return boolean 是否有效
     */
    public boolean isValid() {
        return !isExpired() && (isUsed == null || !isUsed);
    }

    /**
     * 应用优惠券计算实际金额
     * 
     * @param originalAmount 原始金额
     * @return BigDecimal 优惠后金额
     */
    public BigDecimal applyDiscount(BigDecimal originalAmount) {
        if (!isValid()) {
            return originalAmount;
        }

        if (type == TYPE_DISCOUNT) {
            return originalAmount.multiply(discount);
        } else if (type == TYPE_FULL_REDUCTION) {
            if (originalAmount.compareTo(fullAmount) >= 0) {
                return originalAmount.subtract(reduceAmount);
            }
        }
        return originalAmount;
    }

    // 兼容旧代码的方法
    
    /**
     * 获取优惠券类型（兼容方法）
     * 
     * @return int 优惠券类型
     */
    public int getType() {
        return type != null ? type : 0;
    }

    /**
     * 获取满减条件金额（兼容方法）
     * 
     * @return BigDecimal 满减条件金额
     */
    public BigDecimal getFullAmount() {
        return fullAmount != null ? fullAmount : BigDecimal.ZERO;
    }

    /**
     * 获取满减优惠金额（兼容方法）
     * 
     * @return BigDecimal 满减优惠金额
     */
    public BigDecimal getReduceAmount() {
        return reduceAmount != null ? reduceAmount : BigDecimal.ZERO;
    }
}