/**
 * 用户实体类
 * 对应数据库中的user表
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

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 包含用户的基本信息字段
 */
@Data
@TableName("user")
public class User {

    /**
     * 用户ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户描述/个人简介
     */
    private String description;

    /**
     * 用户位置
     */
    private String location;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别
     */
    private String gender;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}