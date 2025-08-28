/**
 * 管理员实体类
 * 对应数据库中的admin表，存储管理员的基本信息
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.adminservice.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 包含管理员的ID和密码信息，用于管理员登录认证
 * 重构后仅包含admin-service自有的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin")
public class Admin {

    /**
     * 管理员ID，主键
     * 使用自动递增策略
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 管理员密码
     * 存储加密后的密码信息
     */
    @TableField("password")
    private String password;

    /**
     * 创建时间
     * 记录管理员账户创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 记录管理员账户最后更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 是否删除标记
     * 0-未删除，1-已删除
     */
    @TableField("deleted")
    private Integer deleted;
}