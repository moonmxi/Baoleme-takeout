/**
 * 管理员实体类
 * 对应数据库中的administrator表，存储管理员的基本信息
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

/**
 * 管理员实体类
 * 包含管理员的ID和密码信息，用于管理员登录认证
 */
@Data
@TableName("administrator")
public class Admin {

    /**
     * 管理员ID，主键
     * 使用MyBatis-Plus的ID生成策略
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 管理员密码
     * 存储加密后的密码信息
     */
    @TableField("password")
    private String password;
}