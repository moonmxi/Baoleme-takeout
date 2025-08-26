/**
 * 聊天消息响应DTO
 * 用于封装聊天消息信息
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.dto.response.message;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天消息响应数据传输对象
 */
@Data
public class ChatMessageResponse {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 发送方用户ID
     */
    private Long senderId;
    
    /**
     * 发送方角色
     */
    private String senderRole;
    
    /**
     * 发送方姓名
     */
    private String senderName;
    
    /**
     * 发送方头像
     */
    private String senderAvatar;
    
    /**
     * 接收方用户ID
     */
    private Long receiverId;
    
    /**
     * 接收方角色
     */
    private String receiverRole;
    
    /**
     * 接收方姓名
     */
    private String receiverName;
    
    /**
     * 接收方头像
     */
    private String receiverAvatar;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型（text/image/file等）
     */
    private String messageType;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}