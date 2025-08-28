/**
 * 聊天记录请求DTO
 * 用于封装获取聊天记录的请求参数
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.dto.request.message;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 聊天记录请求数据传输对象
 */
@Data
public class ChatHistoryRequest {
    
    /**
     * 接收方用户ID
     */
    @NotNull(message = "接收方ID不能为空")
    private Long receiverId;
    
    /**
     * 接收方角色（user/merchant/rider）
     */
    @NotNull(message = "接收方角色不能为空")
    private String receiverRole;
    
    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page;
    
    /**
     * 每页大小
     */
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize;
}