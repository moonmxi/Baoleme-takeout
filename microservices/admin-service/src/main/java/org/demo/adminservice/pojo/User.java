/**
 * 用户实体类
 * 对应数据库中的user表，包含用户的基本信息
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
 * 用户实体类
 * 包含用户的基本信息，如用户名、密码、描述、位置、手机号等
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
     * 用户名，唯一标识
     */
    private String username;

    /**
     * 用户密码，加密存储
     */
    private String password;

    /**
     * 用户描述信息
     */
    private String description;

    /**
     * 用户位置信息
     */
    private String location;

    /**
     * 用户手机号，唯一标识
     */
    private String phone;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}