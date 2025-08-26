/**
 * 销量实体类
 * 对应数据库中的sales表
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销量实体类
 * 记录商品销售数据和统计信息
 */
@Data
@TableName("sales")
public class Sale {
    
    /**
     * 销量记录ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 店铺ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 销售日期
     */
    @TableField("sale_date")
    private LocalDate saleDate;

    /**
     * 销售数量，默认为1
     */
    private Integer quantity = 1;

    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 总金额（数据库自动计算，插入/更新时无需手动设置）
     */
    @TableField(value = "total_amount", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private BigDecimal totalAmount;

    /**
     * 支付方式
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}