/**
 * 店铺实体类
 * 对应数据库中的store表，包含店铺的基本信息
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.adminservice.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺实体类
 * 包含店铺的基本信息，如名称、描述、位置、类型、评分、状态等
 * unique: id, name
 * not null: id, merchant_id, name
 */
@Data
@TableName("store")
public class Store {
    
    /**
     * 店铺ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商家ID，外键关联merchant表
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 店铺名称，唯一标识
     */
    private String name;

    /**
     * 店铺描述信息
     */
    private String description;

    /**
     * 店铺位置信息
     */
    private String location;

    /**
     * 店铺类型
     */
    private String type;

    /**
     * 评分（decimal(2,1), 默认5.0）
     */
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    /**
     * 状态（1-开启，0-关闭）
     */
    private Integer status = 0;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 店铺图片URL
     */
    private String image;
}