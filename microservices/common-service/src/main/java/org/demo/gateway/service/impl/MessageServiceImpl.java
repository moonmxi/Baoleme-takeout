/**
 * 消息服务实现类
 * 实现消息聊天相关的业务逻辑
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.service.impl;

import org.demo.gateway.dto.response.message.ChatMessageResponse;
import org.demo.gateway.mapper.MessageMapper;
import org.demo.gateway.pojo.Message;
import org.demo.gateway.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息服务实现类
 * 提供消息聊天相关的业务逻辑处理
 */
@Service
public class MessageServiceImpl implements MessageService {

    /**
     * 消息数据访问接口
     */
    @Autowired
    private MessageMapper messageMapper;

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
    @Override
    public List<ChatMessageResponse> getChatHistory(Long senderId, String senderRole, 
                                                   Long receiverId, String receiverRole, 
                                                   int page, int pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            List<Message> messages = messageMapper.getChatHistory(senderId, senderRole, receiverId, receiverRole, offset, pageSize);
            
            List<ChatMessageResponse> responses = new ArrayList<>();
            for (Message message : messages) {
                ChatMessageResponse response = new ChatMessageResponse();
                response.setId(message.getId());
                response.setSenderId(message.getSenderId());
                response.setSenderRole(message.getSenderRole());
                response.setReceiverId(message.getReceiverId());
                response.setReceiverRole(message.getReceiverRole());
                response.setContent(message.getContent());
                response.setMessageType(message.getMessageType());
                response.setIsRead(message.getIsRead() == 1);
                response.setCreatedAt(message.getCreatedAt());
                responses.add(response);
            }
            
            return responses;
        } catch (Exception e) {
            throw new RuntimeException("获取聊天记录失败: " + e.getMessage(), e);
        }
    }

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
    @Override
    public ChatMessageResponse sendMessage(Long senderId, String senderRole, 
                                          Long receiverId, String receiverRole, 
                                          String content) {
        try {
            // 检查用户是否存在
            if (!checkUserExists(receiverId, receiverRole)) {
                throw new RuntimeException("接收方用户不存在");
            }
            
            // 创建消息对象
            Message message = new Message();
            message.setSenderId(senderId);
            message.setSenderRole(senderRole);
            message.setReceiverId(receiverId);
            message.setReceiverRole(receiverRole);
            message.setContent(content);
            message.setMessageType("text");
            message.setIsRead(0);
            message.setCreatedAt(LocalDateTime.now());
            
            // 保存消息
            boolean saved = saveMessage(message);
            if (!saved) {
                return null;
            }
            
            // 构建响应
            ChatMessageResponse response = new ChatMessageResponse();
            response.setId(message.getId());
            response.setSenderId(senderId);
            response.setSenderRole(senderRole);
            response.setReceiverId(receiverId);
            response.setReceiverRole(receiverRole);
            response.setContent(content);
            response.setMessageType("text");
            response.setIsRead(false);
            response.setCreatedAt(message.getCreatedAt());
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("发送消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 保存消息到数据库
     * 
     * @param message 消息对象
     * @return boolean 保存是否成功
     */
    @Override
    public boolean saveMessage(Message message) {
        try {
            return messageMapper.insert(message) > 0;
        } catch (Exception e) {
            throw new RuntimeException("保存消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户会话列表
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Map<String, Object>> 会话列表
     */
    @Override
    public List<Map<String, Object>> getConversations(Long userId, String userRole, int page, int pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            return messageMapper.getConversations(userId, userRole, offset, pageSize);
        } catch (Exception e) {
            throw new RuntimeException("获取会话列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 标记消息为已读
     * 
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return boolean 标记是否成功
     */
    @Override
    public boolean markAsRead(Long conversationId, Long userId, String userRole) {
        try {
            return messageMapper.markAsRead(conversationId, userId, userRole) > 0;
        } catch (Exception e) {
            throw new RuntimeException("标记消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取未读消息数量
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return Integer 未读消息数量
     */
    @Override
    public Integer getUnreadMessageCount(Long userId, String userRole) {
        try {
            return messageMapper.getUnreadMessageCount(userId, userRole);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 检查用户是否存在
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return boolean 用户是否存在
     */
    @Override
    public boolean checkUserExists(Long userId, String userRole) {
        try {
            return messageMapper.checkUserExists(userId, userRole) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}