/**
 * 商家实体类
 * 对应数据库中的merchant表，包含商家的基本信息
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
 * 商家实体类
 * 包含商家的基本信息，如用户名、密码、手机号等
 * 数据库约束：not null: id,username,password,phone；unique: id,username, phone
 */
@Data
@TableName("merchant")
public class Merchant {
    
    /**
     * 商家ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商家用户名，唯一标识
     */
    private String username;

    /**
     * 商家密码，数据库中保存加密后的字符串
     */
    private String password;

    /**
     * 商家手机号，唯一标识
     */
    private String phone;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 商家头像URL
     */
    private String avatar;
}