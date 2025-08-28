/**
 * 消息服务接口
 * 定义消息聊天相关的业务操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.service;

import org.demo.gateway.dto.response.message.ChatMessageResponse;
import org.demo.gateway.pojo.Message;

import java.util.List;
import java.util.Map;

/**
 * 消息服务接口
 * 提供消息聊天相关的业务逻辑处理方法
 */
public interface MessageService {

    /**
     * 获取聊天记录
     * 
     * @param senderId 发送方ID
     * @param senderRole 发送方角色
     * @param receiverId 接收方ID
     * @param receiverRole 接收方角色
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<ChatMessageResponse> 聊天记录列表
     */
    List<ChatMessageResponse> getChatHistory(Long senderId, String senderRole, 
                                            Long receiverId, String receiverRole, 
                                            int page, int pageSize);

    /**
     * 发送消息
     * 
     * @param senderId 发送方ID
     * @param senderRole 发送方角色
     * @param receiverId 接收方ID
     * @param receiverRole 接收方角色
     * @param content 消息内容
     * @return ChatMessageResponse 发送的消息信息
     */
    ChatMessageResponse sendMessage(Long senderId, String senderRole, 
                                   Long receiverId, String receiverRole, 
                                   String content);

    /**
     * 保存消息到数据库
     * 
     * @param message 消息对象
     * @return boolean 保存是否成功
     */
    boolean saveMessage(Message message);

    /**
     * 获取用户会话列表
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Map<String, Object>> 会话列表
     */
    List<Map<String, Object>> getConversations(Long userId, String userRole, int page, int pageSize);

    /**
     * 标记消息为已读
     * 
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return boolean 标记是否成功
     */
    boolean markAsRead(Long conversationId, Long userId, String userRole);

    /**
     * 获取未读消息数量
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return Integer 未读消息数量
     */
    Integer getUnreadMessageCount(Long userId, String userRole);

    /**
     * 检查用户是否存在
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return boolean 用户是否存在
     */
    boolean checkUserExists(Long userId, String userRole);
}