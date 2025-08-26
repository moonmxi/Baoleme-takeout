/**
 * 骑手实体类
 * 对应数据库中的rider表，包含骑手的基本信息和状态
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.adminservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 骑手实体类
 * 包含骑手的基本信息、订单状态、派单模式、余额等
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
     * 骑手用户名，唯一标识
     */
    private String username;

    /**
     * 骑手密码，加密存储
     */
    private String password;

    /**
     * 骑手手机号
     */
    private String phone;

    /**
     * 订单状态：-1=未激活, 0=接单中, 1=空闲
     */
    private Integer status;

    /**
     * 派单模式：0=手动，1=自动
     */
    private Integer dispatchMode;

    /**
     * 账户余额
     */
    private Long balance;

    /**
     * 骑手头像URL
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}