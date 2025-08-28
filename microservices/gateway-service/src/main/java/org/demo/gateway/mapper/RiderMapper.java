/**
 * 骑手表数据访问接口
 * 提供骑手表的基本CRUD操作，连接到骑手数据库
 * 
 * 数据库：baoleme_rider
 * 表名：rider
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
 * 骑手表Mapper接口
 * 提供骑手数据的数据库操作方法
 */
@Mapper
@Repository
public interface RiderMapper {

    /**
     * 根据ID查询骑手信息
     * 
     * @param id 骑手ID
     * @return Map<String, Object> 骑手信息
     */
    @Select("SELECT * FROM rider WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据手机号查询骑手信息
     * 
     * @param phone 手机号
     * @return Map<String, Object> 骑手信息
     */
    @Select("SELECT * FROM rider WHERE phone = #{phone}")
    Map<String, Object> selectByPhone(@Param("phone") String phone);

    /**
     * 根据用户名查询骑手信息
     * 
     * @param username 用户名
     * @return Map<String, Object> 骑手信息
     */
    @Select("SELECT * FROM rider WHERE username = #{username}")
    Map<String, Object> selectByUsername(@Param("username") String username);

    /**
     * 查询所有骑手信息（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 骑手列表
     */
    @Select("SELECT * FROM rider ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据状态查询骑手
     * 
     * @param status 骑手状态
     * @return List<Map<String, Object>> 骑手列表
     */
    @Select("SELECT * FROM rider WHERE status = #{status} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByStatus(@Param("status") Integer status);

    /**
     * 根据接单模式查询骑手
     * 
     * @param dispatchMode 接单模式
     * @return List<Map<String, Object>> 骑手列表
     */
    @Select("SELECT * FROM rider WHERE dispatch_mode = #{dispatchMode} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByDispatchMode(@Param("dispatchMode") Integer dispatchMode);

    /**
     * 根据条件查询骑手
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 骑手列表
     */
    @SelectProvider(type = RiderSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新骑手
     * 
     * @param rider 骑手信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO rider (username, password, dispatch_mode, status, phone, balance, avatar, created_at) " +
            "VALUES (#{username}, #{password}, #{dispatchMode}, #{status}, #{phone}, #{balance}, #{avatar}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("rider") Map<String, Object> rider);

    /**
     * 更新骑手信息
     * 
     * @param id 骑手ID
     * @param rider 更新的骑手信息
     * @return int 影响行数
     */
    @UpdateProvider(type = RiderSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("rider") Map<String, Object> rider);

    /**
     * 更新骑手状态
     * 
     * @param id 骑手ID
     * @param status 状态
     * @return int 影响行数
     */
    @Update("UPDATE rider SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新骑手接单模式
     * 
     * @param id 骑手ID
     * @param dispatchMode 接单模式
     * @return int 影响行数
     */
    @Update("UPDATE rider SET dispatch_mode = #{dispatchMode} WHERE id = #{id}")
    int updateDispatchMode(@Param("id") Long id, @Param("dispatchMode") Integer dispatchMode);

    /**
     * 更新骑手余额
     * 
     * @param id 骑手ID
     * @param balance 余额
     * @return int 影响行数
     */
    @Update("UPDATE rider SET balance = #{balance} WHERE id = #{id}")
    int updateBalance(@Param("id") Long id, @Param("balance") Double balance);

    /**
     * 增加骑手余额
     * 
     * @param id 骑手ID
     * @param amount 增加金额
     * @return int 影响行数
     */
    @Update("UPDATE rider SET balance = balance + #{amount} WHERE id = #{id}")
    int increaseBalance(@Param("id") Long id, @Param("amount") Double amount);

    /**
     * 减少骑手余额
     * 
     * @param id 骑手ID
     * @param amount 减少金额
     * @return int 影响行数
     */
    @Update("UPDATE rider SET balance = balance - #{amount} WHERE id = #{id} AND balance >= #{amount}")
    int decreaseBalance(@Param("id") Long id, @Param("amount") Double amount);

    /**
     * 删除骑手
     * 
     * @param id 骑手ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM rider WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 统计骑手数量
     * 
     * @return long 骑手总数
     */
    @Select("SELECT COUNT(*) FROM rider")
    long count();

    /**
     * 根据条件统计骑手数量
     * 
     * @param conditions 查询条件
     * @return long 骑手数量
     */
    @SelectProvider(type = RiderSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入骑手
     * 
     * @param riders 骑手列表
     * @return int 影响行数
     */
    @InsertProvider(type = RiderSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("riders") List<Map<String, Object>> riders);

    /**
     * 骑手SQL提供者类
     * 动态生成SQL语句
     */
    class RiderSqlProvider {

        /**
         * 根据条件查询骑手的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM rider WHERE 1=1");
            
            if (conditions.containsKey("username")) {
                sql.append(" AND username LIKE CONCAT('%', #{conditions.username}, '%')");
            }
            if (conditions.containsKey("phone")) {
                sql.append(" AND phone = #{conditions.phone}");
            }
            if (conditions.containsKey("status")) {
                sql.append(" AND status = #{conditions.status}");
            }
            if (conditions.containsKey("dispatchMode")) {
                sql.append(" AND dispatch_mode = #{conditions.dispatchMode}");
            }
            if (conditions.containsKey("minBalance")) {
                sql.append(" AND balance >= #{conditions.minBalance}");
            }
            if (conditions.containsKey("maxBalance")) {
                sql.append(" AND balance <= #{conditions.maxBalance}");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
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
         * 根据条件统计骑手数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM rider WHERE 1=1");
            
            if (conditions.containsKey("username")) {
                sql.append(" AND username LIKE CONCAT('%', #{conditions.username}, '%')");
            }
            if (conditions.containsKey("phone")) {
                sql.append(" AND phone = #{conditions.phone}");
            }
            if (conditions.containsKey("status")) {
                sql.append(" AND status = #{conditions.status}");
            }
            if (conditions.containsKey("dispatchMode")) {
                sql.append(" AND dispatch_mode = #{conditions.dispatchMode}");
            }
            if (conditions.containsKey("minBalance")) {
                sql.append(" AND balance >= #{conditions.minBalance}");
            }
            if (conditions.containsKey("maxBalance")) {
                sql.append(" AND balance <= #{conditions.maxBalance}");
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
         * 更新骑手信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rider = (Map<String, Object>) params.get("rider");
            
            StringBuilder sql = new StringBuilder("UPDATE rider SET ");
            boolean first = true;
            
            if (rider.containsKey("username")) {
                if (!first) sql.append(", ");
                sql.append("username = #{rider.username}");
                first = false;
            }
            if (rider.containsKey("password")) {
                if (!first) sql.append(", ");
                sql.append("password = #{rider.password}");
                first = false;
            }
            if (rider.containsKey("phone")) {
                if (!first) sql.append(", ");
                sql.append("phone = #{rider.phone}");
                first = false;
            }
            if (rider.containsKey("dispatchMode")) {
                if (!first) sql.append(", ");
                sql.append("dispatch_mode = #{rider.dispatchMode}");
                first = false;
            }
            if (rider.containsKey("status")) {
                if (!first) sql.append(", ");
                sql.append("status = #{rider.status}");
                first = false;
            }
            if (rider.containsKey("balance")) {
                if (!first) sql.append(", ");
                sql.append("balance = #{rider.balance}");
                first = false;
            }
            if (rider.containsKey("avatar")) {
                if (!first) sql.append(", ");
                sql.append("avatar = #{rider.avatar}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入骑手的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> riders = (List<Map<String, Object>>) params.get("riders");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO rider (username, password, dispatch_mode, status, phone, balance, avatar, created_at) VALUES ");
            
            for (int i = 0; i < riders.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{riders[").append(i).append("].username}, ")
                   .append("#{riders[").append(i).append("].password}, ")
                   .append("#{riders[").append(i).append("].dispatchMode}, ")
                   .append("#{riders[").append(i).append("].status}, ")
                   .append("#{riders[").append(i).append("].phone}, ")
                   .append("#{riders[").append(i).append("].balance}, ")
                   .append("#{riders[").append(i).append("].avatar}, ")
                   .append("#{riders[").append(i).append("].createdAt})")
                ;
            }
            
            return sql.toString();
        }
    }
}