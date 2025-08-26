/**
 * 商家实体类
 * 对应数据库中的merchant表
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家实体类
 * 数据库约束：
 * not null: id,username,password,phone；
 * unique: id,username, phone
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
     * 商家用户名
     */
    private String username;

    /**
     * 密码（数据库中保存加密后的字符串）
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 头像URL
     */
    private String avatar;
}