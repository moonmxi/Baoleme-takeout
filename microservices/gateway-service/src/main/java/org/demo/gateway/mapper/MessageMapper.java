/**
 * 消息表数据访问接口
 * 提供消息表的基本CRUD操作，连接到网关数据库
 * 
 * 数据库：baoleme_gateway
 * 表名：message
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 消息表Mapper接口
 * 提供消息数据的数据库操作方法
 */
@Mapper
@Repository
public interface MessageMapper {

    /**
     * 根据ID查询消息信息
     * 
     * @param id 消息ID
     * @return Map<String, Object> 消息信息
     */
    @Select("SELECT * FROM message WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据发送者ID查询消息列表
     * 
     * @param senderId 发送者ID
     * @return List<Map<String, Object>> 消息列表
     */
    @Select("SELECT * FROM message WHERE sender_id = #{senderId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectBySenderId(@Param("senderId") Long senderId);

    /**
     * 根据接收者ID查询消息列表
     * 
     * @param receiverId 接收者ID
     * @return List<Map<String, Object>> 消息列表
     */
    @Select("SELECT * FROM message WHERE receiver_id = #{receiverId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 查询两个用户之间的聊天记录
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return List<Map<String, Object>> 聊天记录列表
     */
    @Select("SELECT * FROM message WHERE " +
            "(sender_id = #{userId1} AND receiver_id = #{userId2}) OR " +
            "(sender_id = #{userId2} AND receiver_id = #{userId1}) " +
            "ORDER BY created_at ASC")
    List<Map<String, Object>> selectChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 查询用户的会话列表（最近联系人）
     * 
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return List<Map<String, Object>> 会话列表
     */
    @Select("SELECT DISTINCT " +
            "CASE WHEN sender_id = #{userId} THEN receiver_id ELSE sender_id END as contact_id, " +
            "CASE WHEN sender_id = #{userId} THEN receiver_role ELSE sender_role END as contact_role, " +
            "MAX(created_at) as last_message_time " +
            "FROM message " +
            "WHERE (sender_id = #{userId} AND sender_role = #{userRole}) " +
            "OR (receiver_id = #{userId} AND receiver_role = #{userRole}) " +
            "GROUP BY contact_id, contact_role " +
            "ORDER BY last_message_time DESC")
    List<Map<String, Object>> selectConversations(@Param("userId") Long userId, @Param("userRole") String userRole);

    /**
     * 查询用户未读消息数量
     * 
     * @param receiverId 接收者ID
     * @param receiverRole 接收者角色
     * @return long 未读消息数量
     */
    @Select("SELECT COUNT(*) FROM message WHERE receiver_id = #{receiverId} AND receiver_role = #{receiverRole}")
    long countUnreadMessages(@Param("receiverId") Long receiverId, @Param("receiverRole") String receiverRole);

    /**
     * 查询所有消息（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 消息列表
     */
    @Select("SELECT * FROM message ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询消息
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 消息列表
     */
    @SelectProvider(type = MessageSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新消息
     * 
     * @param message 消息信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO message (content, sender_id, receiver_id, sender_role, receiver_role, created_at) " +
            "VALUES (#{content}, #{senderId}, #{receiverId}, #{senderRole}, #{receiverRole}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("message") Map<String, Object> message);

    /**
     * 更新消息内容
     * 
     * @param id 消息ID
     * @param content 新内容
     * @return int 影响行数
     */
    @Update("UPDATE message SET content = #{content} WHERE id = #{id}")
    int updateContent(@Param("id") Long id, @Param("content") String content);

    /**
     * 删除消息
     * 
     * @param id 消息ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM message WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 删除会话中的所有消息
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM message WHERE " +
            "(sender_id = #{userId1} AND receiver_id = #{userId2}) OR " +
            "(sender_id = #{userId2} AND receiver_id = #{userId1})")
    int deleteConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 清理过期消息
     * 
     * @param days 保留天数
     * @return int 影响行数
     */
    @Delete("DELETE FROM message WHERE created_at < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int cleanupOldMessages(@Param("days") int days);

    /**
     * 统计消息数量
     * 
     * @return long 消息总数
     */
    @Select("SELECT COUNT(*) FROM message")
    long count();

    /**
     * 根据条件统计消息数量
     * 
     * @param conditions 查询条件
     * @return long 消息数量
     */
    @SelectProvider(type = MessageSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入消息
     * 
     * @param messages 消息列表
     * @return int 影响行数
     */
    @InsertProvider(type = MessageSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("messages") List<Map<String, Object>> messages);

    /**
     * 消息SQL提供者类
     * 动态生成SQL语句
     */
    class MessageSqlProvider {

        /**
         * 根据条件查询消息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM message WHERE 1=1");
            
            if (conditions.containsKey("senderId")) {
                sql.append(" AND sender_id = #{conditions.senderId}");
            }
            if (conditions.containsKey("receiverId")) {
                sql.append(" AND receiver_id = #{conditions.receiverId}");
            }
            if (conditions.containsKey("senderRole")) {
                sql.append(" AND sender_role LIKE CONCAT('%', #{conditions.senderRole}, '%')");
            }
            if (conditions.containsKey("receiverRole")) {
                sql.append(" AND receiver_role LIKE CONCAT('%', #{conditions.receiverRole}, '%')");
            }
            if (conditions.containsKey("content")) {
                sql.append(" AND content LIKE CONCAT('%', #{conditions.content}, '%')");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
            }
            if (conditions.containsKey("recentDays")) {
                sql.append(" AND created_at >= DATE_SUB(NOW(), INTERVAL #{conditions.recentDays} DAY)");
            }
            if (conditions.containsKey("userId")) {
                sql.append(" AND (sender_id = #{conditions.userId} OR receiver_id = #{conditions.userId})");
            }
            if (conditions.containsKey("userRole")) {
                sql.append(" AND (sender_role = #{conditions.userRole} OR receiver_role = #{conditions.userRole})");
            }
            if (conditions.containsKey("conversationWith")) {
                Long conversationWith = (Long) conditions.get("conversationWith");
                Long userId = (Long) conditions.get("userId");
                if (userId != null && conversationWith != null) {
                    sql.append(" AND ((sender_id = #{conditions.userId} AND receiver_id = #{conditions.conversationWith}) ")
                       .append("OR (sender_id = #{conditions.conversationWith} AND receiver_id = #{conditions.userId}))");
                }
            }
            
            // 排序
            if (conditions.containsKey("orderBy")) {
                sql.append(" ORDER BY #{conditions.orderBy}");
                if (conditions.containsKey("orderDirection")) {
                    sql.append(" #{conditions.orderDirection}");
                }
            } else {
                sql.append(" ORDER BY created_at DESC");
            }
            
            if (conditions.containsKey("limit")) {
                sql.append(" LIMIT #{conditions.limit}");
            }
            
            return sql.toString();
        }

        /**
         * 根据条件统计消息数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM message WHERE 1=1");
            
            if (conditions.containsKey("senderId")) {
                sql.append(" AND sender_id = #{conditions.senderId}");
            }
            if (conditions.containsKey("receiverId")) {
                sql.append(" AND receiver_id = #{conditions.receiverId}");
            }
            if (conditions.containsKey("senderRole")) {
                sql.append(" AND sender_role LIKE CONCAT('%', #{conditions.senderRole}, '%')");
            }
            if (conditions.containsKey("receiverRole")) {
                sql.append(" AND receiver_role LIKE CONCAT('%', #{conditions.receiverRole}, '%')");
            }
            if (conditions.containsKey("content")) {
                sql.append(" AND content LIKE CONCAT('%', #{conditions.content}, '%')");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
            }
            if (conditions.containsKey("recentDays")) {
                sql.append(" AND created_at >= DATE_SUB(NOW(), INTERVAL #{conditions.recentDays} DAY)");
            }
            if (conditions.containsKey("userId")) {
                sql.append(" AND (sender_id = #{conditions.userId} OR receiver_id = #{conditions.userId})");
            }
            if (conditions.containsKey("userRole")) {
                sql.append(" AND (sender_role = #{conditions.userRole} OR receiver_role = #{conditions.userRole})");
            }
            if (conditions.containsKey("conversationWith")) {
                Long conversationWith = (Long) conditions.get("conversationWith");
                Long userId = (Long) conditions.get("userId");
                if (userId != null && conversationWith != null) {
                    sql.append(" AND ((sender_id = #{conditions.userId} AND receiver_id = #{conditions.conversationWith}) ")
                       .append("OR (sender_id = #{conditions.conversationWith} AND receiver_id = #{conditions.userId}))");
                }
            }
            
            return sql.toString();
        }

        /**
         * 批量插入消息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> messages = (List<Map<String, Object>>) params.get("messages");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO message (content, sender_id, receiver_id, sender_role, receiver_role, created_at) VALUES ");
            
            for (int i = 0; i < messages.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{messages[").append(i).append("].content}, ")
                   .append("#{messages[").append(i).append("].senderId}, ")
                   .append("#{messages[").append(i).append("].receiverId}, ")
                   .append("#{messages[").append(i).append("].senderRole}, ")
                   .append("#{messages[").append(i).append("].receiverRole}, ")
                   .append("#{messages[").append(i).append("].createdAt})")
                ;
            }
            
            return sql.toString();
        }
    }
}