/**
 * 用户表数据访问接口
 * 提供用户表的基本CRUD操作，连接到用户数据库
 * 
 * 数据库：baoleme_user
 * 表名：user
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户表Mapper接口
 * 提供用户数据的数据库操作方法
 */
@Mapper
@Repository
public interface UserMapper {

    /**
     * 根据ID查询用户信息
     * 
     * @param id 用户ID
     * @return Map<String, Object> 用户信息
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据手机号查询用户信息
     * 
     * @param phone 手机号
     * @return Map<String, Object> 用户信息
     */
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    Map<String, Object> selectByPhone(@Param("phone") String phone);

    /**
     * 查询所有用户信息（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 用户列表
     */
    @Select("SELECT * FROM user ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询用户
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 用户列表
     */
    @SelectProvider(type = UserSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新用户
     * 
     * @param user 用户信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO user (username, password, phone, avatar, created_at) " +
            "VALUES (#{username}, #{password}, #{phone}, #{avatar}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("user") Map<String, Object> user);

    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param user 更新的用户信息
     * @return int 影响行数
     */
    @UpdateProvider(type = UserSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("user") Map<String, Object> user);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 统计用户数量
     * 
     * @return long 用户总数
     */
    @Select("SELECT COUNT(*) FROM user")
    long count();

    /**
     * 根据条件统计用户数量
     * 
     * @param conditions 查询条件
     * @return long 用户数量
     */
    @SelectProvider(type = UserSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入用户
     * 
     * @param users 用户列表
     * @return int 影响行数
     */
    @InsertProvider(type = UserSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("users") List<Map<String, Object>> users);

    /**
     * 批量更新用户
     * 
     * @param users 用户列表
     * @return int 影响行数
     */
    @UpdateProvider(type = UserSqlProvider.class, method = "batchUpdate")
    int batchUpdate(@Param("users") List<Map<String, Object>> users);

    /**
     * 用户SQL提供者类
     * 动态生成SQL语句
     */
    class UserSqlProvider {

        /**
         * 根据条件查询用户的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM user WHERE 1=1");
            
            if (conditions.containsKey("username")) {
                sql.append(" AND username LIKE CONCAT('%', #{conditions.username}, '%')");
            }
            if (conditions.containsKey("phone")) {
                sql.append(" AND phone = #{conditions.phone}");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
            }
            if (conditions.containsKey("limit")) {
                sql.append(" ORDER BY created_at DESC LIMIT #{conditions.limit}");
            }
            
            return sql.toString();
        }

        /**
         * 根据条件统计用户数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM user WHERE 1=1");
            
            if (conditions.containsKey("username")) {
                sql.append(" AND username LIKE CONCAT('%', #{conditions.username}, '%')");
            }
            if (conditions.containsKey("phone")) {
                sql.append(" AND phone = #{conditions.phone}");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
            }
            
            return sql.toString();
        }

        /**
         * 更新用户信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) params.get("user");
            
            StringBuilder sql = new StringBuilder("UPDATE user SET ");
            boolean first = true;
            
            if (user.containsKey("username")) {
                if (!first) sql.append(", ");
                sql.append("username = #{user.username}");
                first = false;
            }
            if (user.containsKey("password")) {
                if (!first) sql.append(", ");
                sql.append("password = #{user.password}");
                first = false;
            }
            if (user.containsKey("phone")) {
                if (!first) sql.append(", ");
                sql.append("phone = #{user.phone}");
                first = false;
            }
            if (user.containsKey("avatar")) {
                if (!first) sql.append(", ");
                sql.append("avatar = #{user.avatar}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入用户的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> users = (List<Map<String, Object>>) params.get("users");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO user (username, password, phone, avatar, created_at) VALUES ");
            
            for (int i = 0; i < users.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{users[").append(i).append("].username}, ")
                   .append("#{users[").append(i).append("].password}, ")
                   .append("#{users[").append(i).append("].phone}, ")
                   .append("#{users[").append(i).append("].avatar}, ")
                   .append("#{users[").append(i).append("].createdAt})")
                ;
            }
            
            return sql.toString();
        }

        /**
         * 批量更新用户的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchUpdate(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> users = (List<Map<String, Object>>) params.get("users");
            
            StringBuilder sql = new StringBuilder();
            
            for (int i = 0; i < users.size(); i++) {
                if (i > 0) sql.append("; ");
                sql.append("UPDATE user SET ")
                   .append("username = #{users[").append(i).append("].username}, ")
                   .append("password = #{users[").append(i).append("].password}, ")
                   .append("phone = #{users[").append(i).append("].phone}, ")
                   .append("avatar = #{users[").append(i).append("].avatar} ")
                   .append("WHERE id = #{users[").append(i).append("].id}");
            }
            
            return sql.toString();
        }
    }
}