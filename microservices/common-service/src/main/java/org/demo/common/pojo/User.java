/**
 * 用户实体类
 * 用于存储用户基本信息
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 包含用户的基本信息和状态
 */
@Data
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
     * 手机号
     */
    private String phone;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 用户描述
     */
    private String description;

    /**
     * 用户位置
     */
    private String location;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}