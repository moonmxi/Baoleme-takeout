/**
 * 商家表数据访问接口
 * 提供商家表的基本CRUD操作，连接到商家数据库
 * 
 * 数据库：baoleme_merchant
 * 表名：merchant
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
 * 商家表Mapper接口
 * 提供商家数据的数据库操作方法
 */
@Mapper
@Repository
public interface MerchantMapper {

    /**
     * 根据ID查询商家信息
     * 
     * @param id 商家ID
     * @return Map<String, Object> 商家信息
     */
    @Select("SELECT * FROM merchant WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据手机号查询商家信息
     * 
     * @param phone 手机号
     * @return Map<String, Object> 商家信息
     */
    @Select("SELECT * FROM merchant WHERE phone = #{phone}")
    Map<String, Object> selectByPhone(@Param("phone") String phone);

    /**
     * 根据用户名查询商家信息
     * 
     * @param username 用户名
     * @return Map<String, Object> 商家信息
     */
    @Select("SELECT * FROM merchant WHERE username = #{username}")
    Map<String, Object> selectByUsername(@Param("username") String username);

    /**
     * 查询所有商家信息（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 商家列表
     */
    @Select("SELECT * FROM merchant ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询商家
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 商家列表
     */
    @SelectProvider(type = MerchantSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新商家
     * 
     * @param merchant 商家信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO merchant (username, password, phone, avatar, created_at) " +
            "VALUES (#{username}, #{password}, #{phone}, #{avatar}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("merchant") Map<String, Object> merchant);

    /**
     * 更新商家信息
     * 
     * @param id 商家ID
     * @param merchant 更新的商家信息
     * @return int 影响行数
     */
    @UpdateProvider(type = MerchantSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("merchant") Map<String, Object> merchant);

    /**
     * 删除商家
     * 
     * @param id 商家ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM merchant WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 统计商家数量
     * 
     * @return long 商家总数
     */
    @Select("SELECT COUNT(*) FROM merchant")
    long count();

    /**
     * 根据条件统计商家数量
     * 
     * @param conditions 查询条件
     * @return long 商家数量
     */
    @SelectProvider(type = MerchantSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入商家
     * 
     * @param merchants 商家列表
     * @return int 影响行数
     */
    @InsertProvider(type = MerchantSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("merchants") List<Map<String, Object>> merchants);

    /**
     * 商家SQL提供者类
     * 动态生成SQL语句
     */
    class MerchantSqlProvider {

        /**
         * 根据条件查询商家的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM merchant WHERE 1=1");
            
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
         * 根据条件统计商家数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM merchant WHERE 1=1");
            
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
         * 更新商家信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> merchant = (Map<String, Object>) params.get("merchant");
            
            StringBuilder sql = new StringBuilder("UPDATE merchant SET ");
            boolean first = true;
            
            if (merchant.containsKey("username")) {
                if (!first) sql.append(", ");
                sql.append("username = #{merchant.username}");
                first = false;
            }
            if (merchant.containsKey("password")) {
                if (!first) sql.append(", ");
                sql.append("password = #{merchant.password}");
                first = false;
            }
            if (merchant.containsKey("phone")) {
                if (!first) sql.append(", ");
                sql.append("phone = #{merchant.phone}");
                first = false;
            }
            if (merchant.containsKey("avatar")) {
                if (!first) sql.append(", ");
                sql.append("avatar = #{merchant.avatar}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入商家的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> merchants = (List<Map<String, Object>>) params.get("merchants");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO merchant (username, password, phone, avatar, created_at) VALUES ");
            
            for (int i = 0; i < merchants.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{merchants[").append(i).append("].username}, ")
                   .append("#{merchants[").append(i).append("].password}, ")
                   .append("#{merchants[").append(i).append("].phone}, ")
                   .append("#{merchants[").append(i).append("].avatar}, ")
                   .append("#{merchants[").append(i).append("].createdAt})")
                ;
            }
            
            return sql.toString();
        }
    }
}