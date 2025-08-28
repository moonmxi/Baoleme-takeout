/**
 * 发送消息请求DTO
 * 用于封装发送消息的请求参数
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发送消息请求数据传输对象
 */
@Data
public class SendMessageRequest {
    
    /**
     * 接收方用户ID
     */
    @NotNull(message = "接收方ID不能为空")
    private Long receiverId;
    
    /**
     * 接收方角色（user/merchant/rider）
     */
    @NotBlank(message = "接收方角色不能为空")
    private String receiverRole;
    
    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 1000, message = "消息内容不能超过1000个字符")
    private String content;
}