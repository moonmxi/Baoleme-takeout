/**
 * 骑手实体类
 * 对应数据库中的rider表
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.riderservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手实体类
 * 包含骑手的基本信息和工作状态
 */
@Data
@TableName("rider")
public class Rider {

    /**
     * 骑手ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 骑手用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 订单状态（1-在线，0-离线，-1-忙碌）
     */
    private Integer orderStatus;

    /**
     * 派单模式（1-自动接单，0-手动接单）
     */
    private Integer dispatchMode;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 账户余额（单位：分）
     */
    private Long balance;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}