/**
 * 消息控制器
 * 处理用户、商家、骑手之间的聊天消息相关的HTTP请求
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.common.controller;

import jakarta.validation.Valid;
import org.demo.common.dto.response.message.ChatMessageResponse;
import org.demo.common.common.CommonResponse;
import org.demo.common.common.ResponseBuilder;
import org.demo.common.common.UserHolder;
import org.demo.common.dto.request.message.ChatHistoryRequest;
import org.demo.common.dto.request.message.SendMessageRequest;
import org.demo.common.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息控制器类
 * 提供聊天消息功能的REST API
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    /**
     * 消息服务接口
     */
    @Autowired
    private MessageService messageService;

    /**
     * 获取聊天记录接口
     * 
     * @param request 聊天记录请求对象
     * @return 聊天记录响应
     */
    @PostMapping("/history")
    public CommonResponse getChatHistory(@Valid @RequestBody ChatHistoryRequest request) {
        try {
            // 获取当前用户身份信息
            Long senderId = UserHolder.getId();
            String senderRole = UserHolder.getRole();

            if (senderId == null || senderRole == null) {
                return ResponseBuilder.fail("用户身份验证失败");
            }

            // 调用服务层获取历史消息
            List<ChatMessageResponse> messages = messageService.getChatHistory(
                    senderId,
                    senderRole,
                    request.getReceiverId(),
                    request.getReceiverRole(),
                    request.getPage(),
                    request.getPageSize()
            );

            return ResponseBuilder.ok(Map.of(
                    "messages", messages,
                    "current_page", request.getPage(),
                    "page_size", request.getPageSize(),
                    "total_count", messages.size()
            ));
        } catch (Exception e) {
            return ResponseBuilder.fail("获取聊天记录失败: " + e.getMessage());
        }
    }

    /**
     * 发送消息接口
     * 
     * @param request 发送消息请求对象
     * @return 发送结果响应
     */
    @PostMapping("/send")
    public CommonResponse sendMessage(@Valid @RequestBody SendMessageRequest request) {
        try {
            // 获取当前用户身份信息
            Long senderId = UserHolder.getId();
            String senderRole = UserHolder.getRole();

            if (senderId == null || senderRole == null) {
                return ResponseBuilder.fail("用户身份验证失败");
            }

            // 调用服务层发送消息
            ChatMessageResponse message = messageService.sendMessage(
                    senderId,
                    senderRole,
                    request.getReceiverId(),
                    request.getReceiverRole(),
                    request.getContent()
            );

            if (message == null) {
                return ResponseBuilder.fail("消息发送失败");
            }

            return ResponseBuilder.ok(Map.of(
                    "message", "消息发送成功",
                    "data", message
            ));
        } catch (Exception e) {
            return ResponseBuilder.fail("发送消息失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户会话列表接口
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @return 会话列表响应
     */
    @GetMapping("/conversations")
    public CommonResponse getConversations(@RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        try {
            // 获取当前用户身份信息
            Long userId = UserHolder.getId();
            String userRole = UserHolder.getRole();

            if (userId == null || userRole == null) {
                return ResponseBuilder.fail("用户身份验证失败");
            }

            // 调用服务层获取会话列表
            List<Map<String, Object>> conversations = messageService.getConversations(userId, userRole, page, pageSize);

            return ResponseBuilder.ok(Map.of(
                    "conversations", conversations,
                    "current_page", page,
                    "page_size", pageSize
            ));
        } catch (Exception e) {
            return ResponseBuilder.fail("获取会话列表失败: " + e.getMessage());
        }
    }

    /**
     * 标记消息为已读接口
     * 
     * @param conversationId 会话ID
     * @return 标记结果响应
     */
    @PutMapping("/conversations/{conversationId}/read")
    public CommonResponse markAsRead(@PathVariable Long conversationId) {
        try {
            // 获取当前用户身份信息
            Long userId = UserHolder.getId();
            String userRole = UserHolder.getRole();

            if (userId == null || userRole == null) {
                return ResponseBuilder.fail("用户身份验证失败");
            }

            // 调用服务层标记消息为已读
            boolean success = messageService.markAsRead(conversationId, userId, userRole);

            if (!success) {
                return ResponseBuilder.fail("标记消息失败");
            }

            return ResponseBuilder.ok("消息已标记为已读");
        } catch (Exception e) {
            return ResponseBuilder.fail("标记消息失败: " + e.getMessage());
        }
    }
}