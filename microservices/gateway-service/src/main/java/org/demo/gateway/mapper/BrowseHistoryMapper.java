/**
 * 浏览历史表数据访问接口
 * 提供浏览历史表的基本CRUD操作，连接到用户数据库
 * 
 * 数据库：baoleme_user
 * 表名：browse_history
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
 * 浏览历史表Mapper接口
 * 提供浏览历史数据的数据库操作方法
 */
@Mapper
@Repository
public interface BrowseHistoryMapper {

    /**
     * 根据用户ID查询浏览历史
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 浏览历史列表
     */
    @Select("SELECT * FROM browse_history WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据店铺ID查询浏览历史
     * 
     * @param storeId 店铺ID
     * @return List<Map<String, Object>> 浏览历史列表
     */
    @Select("SELECT * FROM browse_history WHERE store_id = #{storeId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 查询用户最近浏览的店铺
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return List<Map<String, Object>> 最近浏览的店铺列表
     */
    @Select("SELECT * FROM browse_history WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<Map<String, Object>> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 查询所有浏览历史（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 浏览历史列表
     */
    @Select("SELECT * FROM browse_history ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询浏览历史
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 浏览历史列表
     */
    @SelectProvider(type = BrowseHistorySqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入或更新浏览历史记录
     * 如果记录已存在则更新时间，否则插入新记录
     * 
     * @param browseHistory 浏览历史信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO browse_history (user_id, store_id, created_at) " +
            "VALUES (#{userId}, #{storeId}, #{createdAt}) " +
            "ON DUPLICATE KEY UPDATE created_at = #{createdAt}")
    int insertOrUpdate(@Param("browseHistory") Map<String, Object> browseHistory);

    /**
     * 插入新浏览历史记录
     * 
     * @param browseHistory 浏览历史信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO browse_history (user_id, store_id, created_at) " +
            "VALUES (#{userId}, #{storeId}, #{createdAt})")
    int insert(@Param("browseHistory") Map<String, Object> browseHistory);

    /**
     * 检查浏览历史记录是否存在
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return boolean 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM browse_history WHERE user_id = #{userId} AND store_id = #{storeId}")
    boolean exists(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 更新浏览时间
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @param createdAt 浏览时间
     * @return int 影响行数
     */
    @Update("UPDATE browse_history SET created_at = #{createdAt} WHERE user_id = #{userId} AND store_id = #{storeId}")
    int updateBrowseTime(@Param("userId") Long userId, @Param("storeId") Long storeId, @Param("createdAt") java.time.LocalDateTime createdAt);

    /**
     * 删除浏览历史记录
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM browse_history WHERE user_id = #{userId} AND store_id = #{storeId}")
    int delete(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 根据用户ID删除所有浏览历史
     * 
     * @param userId 用户ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM browse_history WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 清理过期的浏览历史记录
     * 
     * @param days 保留天数
     * @return int 影响行数
     */
    @Delete("DELETE FROM browse_history WHERE created_at < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int cleanupOldRecords(@Param("days") int days);

    /**
     * 统计浏览历史记录数量
     * 
     * @return long 浏览历史记录总数
     */
    @Select("SELECT COUNT(*) FROM browse_history")
    long count();

    /**
     * 根据条件统计浏览历史记录数量
     * 
     * @param conditions 查询条件
     * @return long 浏览历史记录数量
     */
    @SelectProvider(type = BrowseHistorySqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入浏览历史记录
     * 
     * @param browseHistories 浏览历史记录列表
     * @return int 影响行数
     */
    @InsertProvider(type = BrowseHistorySqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("browseHistories") List<Map<String, Object>> browseHistories);

    /**
     * 浏览历史SQL提供者类
     * 动态生成SQL语句
     */
    class BrowseHistorySqlProvider {

        /**
         * 根据条件查询浏览历史的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM browse_history WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
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
         * 根据条件统计浏览历史数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM browse_history WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
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
            
            return sql.toString();
        }

        /**
         * 批量插入浏览历史的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> browseHistories = (List<Map<String, Object>>) params.get("browseHistories");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO browse_history (user_id, store_id, created_at) VALUES ");
            
            for (int i = 0; i < browseHistories.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{browseHistories[").append(i).append("].userId}, ")
                   .append("#{browseHistories[").append(i).append("].storeId}, ")
                   .append("#{browseHistories[").append(i).append("].createdAt})")
                ;
            }
            
            sql.append(" ON DUPLICATE KEY UPDATE created_at = VALUES(created_at)");
            
            return sql.toString();
        }
    }
}