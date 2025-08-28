/**
 * 管理员表数据访问接口
 * 提供管理员表的基本CRUD操作，连接到管理员数据库
 * 
 * 数据库：baoleme_admin
 * 表名：admin
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
 * 管理员表Mapper接口
 * 提供管理员数据的数据库操作方法
 */
@Mapper
@Repository
public interface AdminMapper {

    /**
     * 根据ID查询管理员信息
     * 
     * @param id 管理员ID
     * @return Map<String, Object> 管理员信息
     */
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 查询所有管理员信息（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 管理员列表
     */
    @Select("SELECT * FROM admin ORDER BY id ASC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询管理员
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 管理员列表
     */
    @SelectProvider(type = AdminSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新管理员
     * 
     * @param admin 管理员信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO admin (password) VALUES (#{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("admin") Map<String, Object> admin);

    /**
     * 更新管理员信息
     * 
     * @param id 管理员ID
     * @param admin 更新的管理员信息
     * @return int 影响行数
     */
    @UpdateProvider(type = AdminSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("admin") Map<String, Object> admin);

    /**
     * 更新管理员密码
     * 
     * @param id 管理员ID
     * @param password 新密码
     * @return int 影响行数
     */
    @Update("UPDATE admin SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 删除管理员
     * 
     * @param id 管理员ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM admin WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 统计管理员数量
     * 
     * @return long 管理员总数
     */
    @Select("SELECT COUNT(*) FROM admin")
    long count();

    /**
     * 根据条件统计管理员数量
     * 
     * @param conditions 查询条件
     * @return long 管理员数量
     */
    @SelectProvider(type = AdminSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入管理员
     * 
     * @param admins 管理员列表
     * @return int 影响行数
     */
    @InsertProvider(type = AdminSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("admins") List<Map<String, Object>> admins);

    /**
     * 验证管理员登录
     * 
     * @param id 管理员ID
     * @param password 密码
     * @return Map<String, Object> 管理员信息（如果验证成功）
     */
    @Select("SELECT * FROM admin WHERE id = #{id} AND password = #{password}")
    Map<String, Object> validateLogin(@Param("id") Long id, @Param("password") String password);

    /**
     * 管理员SQL提供者类
     * 动态生成SQL语句
     */
    class AdminSqlProvider {

        /**
         * 根据条件查询管理员的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM admin WHERE 1=1");
            
            if (conditions.containsKey("startId")) {
                sql.append(" AND id >= #{conditions.startId}");
            }
            if (conditions.containsKey("endId")) {
                sql.append(" AND id <= #{conditions.endId}");
            }
            
            // 排序
            if (conditions.containsKey("orderBy")) {
                sql.append(" ORDER BY #{conditions.orderBy}");
                if (conditions.containsKey("orderDirection")) {
                    sql.append(" #{conditions.orderDirection}");
                }
            } else {
                sql.append(" ORDER BY id ASC");
            }
            
            if (conditions.containsKey("limit")) {
                sql.append(" LIMIT #{conditions.limit}");
            }
            
            return sql.toString();
        }

        /**
         * 根据条件统计管理员数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM admin WHERE 1=1");
            
            if (conditions.containsKey("startId")) {
                sql.append(" AND id >= #{conditions.startId}");
            }
            if (conditions.containsKey("endId")) {
                sql.append(" AND id <= #{conditions.endId}");
            }
            
            return sql.toString();
        }

        /**
         * 更新管理员信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> admin = (Map<String, Object>) params.get("admin");
            
            StringBuilder sql = new StringBuilder("UPDATE admin SET ");
            boolean first = true;
            
            if (admin.containsKey("password")) {
                if (!first) sql.append(", ");
                sql.append("password = #{admin.password}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入管理员的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> admins = (List<Map<String, Object>>) params.get("admins");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO admin (password) VALUES ");
            
            for (int i = 0; i < admins.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{admins[").append(i).append("].password})")
                ;
            }
            
            return sql.toString();
        }
    }
}