/**
 * 消息实体类
 * 对应数据库中的message表，用于存储聊天消息信息
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.common.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息实体类
 * 存储用户、商家、骑手之间的聊天消息
 */
@Data
@TableName("message")
public class Message {

    /**
     * 消息ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 发送方用户ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 接收方用户ID
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 发送方角色（user/merchant/rider）
     */
    @TableField("sender_role")
    private String senderRole;

    /**
     * 接收方角色（user/merchant/rider）
     */
    @TableField("receiver_role")
    private String receiverRole;

    /**
     * 消息类型（text/image/file等）
     */
    @TableField("message_type")
    private String messageType;

    /**
     * 是否已读（0-未读，1-已读）
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}