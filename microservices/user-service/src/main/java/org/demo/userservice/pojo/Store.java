/**
 * 店铺实体类
 * 对应数据库中的store表
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺实体类
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
     * 商家ID
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * 店铺位置
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
     * 店铺图片
     */
    private String image;
}