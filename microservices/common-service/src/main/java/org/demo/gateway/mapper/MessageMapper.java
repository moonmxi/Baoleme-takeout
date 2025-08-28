/**
 * 消息数据访问接口
 * 提供消息相关的数据库操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.gateway.pojo.Message;

import java.util.List;
import java.util.Map;

/**
 * 消息数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 * 同时定义消息特有的数据访问方法
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 获取聊天记录
     * 
     * @param senderId 发送方ID
     * @param senderRole 发送方角色
     * @param receiverId 接收方ID
     * @param receiverRole 接收方角色
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Message> 消息列表
     */
    @Select("SELECT * FROM message " +
            "WHERE (sender_id = #{senderId} AND sender_role = #{senderRole} AND receiver_id = #{receiverId} AND receiver_role = #{receiverRole}) " +
            "   OR (sender_id = #{receiverId} AND sender_role = #{receiverRole} AND receiver_id = #{senderId} AND receiver_role = #{senderRole}) " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Message> getChatHistory(@Param("senderId") Long senderId,
                                @Param("senderRole") String senderRole,
                                @Param("receiverId") Long receiverId,
                                @Param("receiverRole") String receiverRole,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    /**
     * 获取用户会话列表
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 会话列表
     */
    @Select("SELECT DISTINCT " +
            "  CASE " +
            "    WHEN sender_id = #{userId} AND sender_role = #{userRole} THEN receiver_id " +
            "    ELSE sender_id " +
            "  END as contact_id, " +
            "  CASE " +
            "    WHEN sender_id = #{userId} AND sender_role = #{userRole} THEN receiver_role " +
            "    ELSE sender_role " +
            "  END as contact_role, " +
            "  MAX(created_at) as last_message_time, " +
            "  COUNT(CASE WHEN receiver_id = #{userId} AND receiver_role = #{userRole} AND is_read = 0 THEN 1 END) as unread_count " +
            "FROM message " +
            "WHERE (sender_id = #{userId} AND sender_role = #{userRole}) " +
            "   OR (receiver_id = #{userId} AND receiver_role = #{userRole}) " +
            "GROUP BY contact_id, contact_role " +
            "ORDER BY last_message_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> getConversations(@Param("userId") Long userId,
                                              @Param("userRole") String userRole,
                                              @Param("offset") int offset,
                                              @Param("limit") int limit);

    /**
     * 标记消息为已读
     * 
     * @param conversationId 会话ID（这里用接收方ID代替）
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return int 更新的记录数
     */
    @Update("UPDATE message SET is_read = 1 " +
            "WHERE receiver_id = #{userId} AND receiver_role = #{userRole} " +
            "  AND sender_id = #{conversationId} AND is_read = 0")
    int markAsRead(@Param("conversationId") Long conversationId,
                   @Param("userId") Long userId,
                   @Param("userRole") String userRole);

    /**
     * 获取未读消息数量
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return Integer 未读消息数量
     */
    @Select("SELECT COUNT(*) FROM message " +
            "WHERE receiver_id = #{userId} AND receiver_role = #{userRole} AND is_read = 0")
    Integer getUnreadMessageCount(@Param("userId") Long userId,
                                 @Param("userRole") String userRole);

    /**
     * 检查用户是否存在
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return Integer 用户存在数量
     */
    @Select("SELECT COUNT(*) FROM " +
            "(SELECT id FROM user WHERE id = #{userId} AND #{userRole} = 'user' " +
            " UNION ALL " +
            " SELECT id FROM merchant WHERE id = #{userId} AND #{userRole} = 'merchant' " +
            " UNION ALL " +
            " SELECT id FROM rider WHERE id = #{userId} AND #{userRole} = 'rider' " +
            " UNION ALL " +
            " SELECT id FROM admin WHERE id = #{userId} AND #{userRole} = 'admin') as users")
    Integer checkUserExists(@Param("userId") Long userId,
                           @Param("userRole") String userRole);
}