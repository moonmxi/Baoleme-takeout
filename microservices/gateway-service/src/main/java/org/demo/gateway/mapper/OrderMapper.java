/**
 * 订单表数据访问接口
 * 提供订单表的基本CRUD操作，连接到网关数据库
 * 
 * 数据库：baoleme_gateway
 * 表名：order
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
 * 订单表Mapper接口
 * 提供订单数据的数据库操作方法
 */
@Mapper
@Repository
public interface OrderMapper {

    /**
     * 根据ID查询订单信息
     * 
     * @param id 订单ID
     * @return Map<String, Object> 订单信息
     */
    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询订单列表
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 订单列表
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据店铺ID查询订单列表
     * 
     * @param storeId 店铺ID
     * @return List<Map<String, Object>> 订单列表
     */
    @Select("SELECT * FROM `order` WHERE store_id = #{storeId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据骑手ID查询订单列表
     * 
     * @param riderId 骑手ID
     * @return List<Map<String, Object>> 订单列表
     */
    @Select("SELECT * FROM `order` WHERE rider_id = #{riderId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByRiderId(@Param("riderId") Long riderId);

    /**
     * 根据状态查询订单列表
     * 
     * @param status 订单状态
     * @return List<Map<String, Object>> 订单列表
     */
    @Select("SELECT * FROM `order` WHERE status = #{status} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByStatus(@Param("status") Integer status);

    /**
     * 查询可抢订单（状态为0且未分配骑手）
     * 
     * @return List<Map<String, Object>> 可抢订单列表
     */
    @Select("SELECT * FROM `order` WHERE status = 0 AND rider_id IS NULL ORDER BY created_at ASC")
    List<Map<String, Object>> selectAvailableOrders();

    /**
     * 查询所有订单信息（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 订单列表
     */
    @Select("SELECT * FROM `order` ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询订单
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 订单列表
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新订单
     * 
     * @param order 订单信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO `order` (user_id, store_id, rider_id, status, user_location, store_location, total_price, actual_price, remark, delivery_price, created_at, deadline, ended_at) " +
            "VALUES (#{userId}, #{storeId}, #{riderId}, #{status}, #{userLocation}, #{storeLocation}, #{totalPrice}, #{actualPrice}, #{remark}, #{deliveryPrice}, #{createdAt}, #{deadline}, #{endedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("order") Map<String, Object> order);

    /**
     * 更新订单信息
     * 
     * @param id 订单ID
     * @param order 更新的订单信息
     * @return int 影响行数
     */
    @UpdateProvider(type = OrderSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("order") Map<String, Object> order);

    /**
     * 更新订单状态
     * 
     * @param id 订单ID
     * @param status 状态
     * @return int 影响行数
     */
    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 分配骑手
     * 
     * @param id 订单ID
     * @param riderId 骑手ID
     * @return int 影响行数
     */
    @Update("UPDATE `order` SET rider_id = #{riderId}, status = 1 WHERE id = #{id} AND rider_id IS NULL")
    int assignRider(@Param("id") Long id, @Param("riderId") Long riderId);

    /**
     * 完成订单
     * 
     * @param id 订单ID
     * @param endedAt 完成时间
     * @return int 影响行数
     */
    @Update("UPDATE `order` SET status = 3, ended_at = #{endedAt} WHERE id = #{id}")
    int completeOrder(@Param("id") Long id, @Param("endedAt") java.time.LocalDateTime endedAt);

    /**
     * 取消订单
     * 
     * @param id 订单ID
     * @param endedAt 取消时间
     * @return int 影响行数
     */
    @Update("UPDATE `order` SET status = 4, ended_at = #{endedAt} WHERE id = #{id}")
    int cancelOrder(@Param("id") Long id, @Param("endedAt") java.time.LocalDateTime endedAt);

    /**
     * 删除订单
     * 
     * @param id 订单ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM `order` WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 统计订单数量
     * 
     * @return long 订单总数
     */
    @Select("SELECT COUNT(*) FROM `order`")
    long count();

    /**
     * 根据条件统计订单数量
     * 
     * @param conditions 查询条件
     * @return long 订单数量
     */
    @SelectProvider(type = OrderSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入订单
     * 
     * @param orders 订单列表
     * @return int 影响行数
     */
    @InsertProvider(type = OrderSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("orders") List<Map<String, Object>> orders);

    /**
     * 订单SQL提供者类
     * 动态生成SQL语句
     */
    class OrderSqlProvider {

        /**
         * 根据条件查询订单的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM `order` WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("riderId")) {
                sql.append(" AND rider_id = #{conditions.riderId}");
            }
            if (conditions.containsKey("status")) {
                sql.append(" AND status = #{conditions.status}");
            }
            if (conditions.containsKey("minPrice")) {
                sql.append(" AND total_price >= #{conditions.minPrice}");
            }
            if (conditions.containsKey("maxPrice")) {
                sql.append(" AND total_price <= #{conditions.maxPrice}");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
            }
            if (conditions.containsKey("userLocation")) {
                sql.append(" AND user_location LIKE CONCAT('%', #{conditions.userLocation}, '%')");
            }
            if (conditions.containsKey("storeLocation")) {
                sql.append(" AND store_location LIKE CONCAT('%', #{conditions.storeLocation}, '%')");
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
         * 根据条件统计订单数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `order` WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("riderId")) {
                sql.append(" AND rider_id = #{conditions.riderId}");
            }
            if (conditions.containsKey("status")) {
                sql.append(" AND status = #{conditions.status}");
            }
            if (conditions.containsKey("minPrice")) {
                sql.append(" AND total_price >= #{conditions.minPrice}");
            }
            if (conditions.containsKey("maxPrice")) {
                sql.append(" AND total_price <= #{conditions.maxPrice}");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
            }
            if (conditions.containsKey("userLocation")) {
                sql.append(" AND user_location LIKE CONCAT('%', #{conditions.userLocation}, '%')");
            }
            if (conditions.containsKey("storeLocation")) {
                sql.append(" AND store_location LIKE CONCAT('%', #{conditions.storeLocation}, '%')");
            }
            
            return sql.toString();
        }

        /**
         * 更新订单信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> order = (Map<String, Object>) params.get("order");
            
            StringBuilder sql = new StringBuilder("UPDATE `order` SET ");
            boolean first = true;
            
            if (order.containsKey("riderId")) {
                if (!first) sql.append(", ");
                sql.append("rider_id = #{order.riderId}");
                first = false;
            }
            if (order.containsKey("status")) {
                if (!first) sql.append(", ");
                sql.append("status = #{order.status}");
                first = false;
            }
            if (order.containsKey("userLocation")) {
                if (!first) sql.append(", ");
                sql.append("user_location = #{order.userLocation}");
                first = false;
            }
            if (order.containsKey("storeLocation")) {
                if (!first) sql.append(", ");
                sql.append("store_location = #{order.storeLocation}");
                first = false;
            }
            if (order.containsKey("totalPrice")) {
                if (!first) sql.append(", ");
                sql.append("total_price = #{order.totalPrice}");
                first = false;
            }
            if (order.containsKey("actualPrice")) {
                if (!first) sql.append(", ");
                sql.append("actual_price = #{order.actualPrice}");
                first = false;
            }
            if (order.containsKey("remark")) {
                if (!first) sql.append(", ");
                sql.append("remark = #{order.remark}");
                first = false;
            }
            if (order.containsKey("deliveryPrice")) {
                if (!first) sql.append(", ");
                sql.append("delivery_price = #{order.deliveryPrice}");
                first = false;
            }
            if (order.containsKey("deadline")) {
                if (!first) sql.append(", ");
                sql.append("deadline = #{order.deadline}");
                first = false;
            }
            if (order.containsKey("endedAt")) {
                if (!first) sql.append(", ");
                sql.append("ended_at = #{order.endedAt}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入订单的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> orders = (List<Map<String, Object>>) params.get("orders");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO `order` (user_id, store_id, rider_id, status, user_location, store_location, total_price, actual_price, remark, delivery_price, created_at, deadline, ended_at) VALUES ");
            
            for (int i = 0; i < orders.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{orders[").append(i).append("].userId}, ")
                   .append("#{orders[").append(i).append("].storeId}, ")
                   .append("#{orders[").append(i).append("].riderId}, ")
                   .append("#{orders[").append(i).append("].status}, ")
                   .append("#{orders[").append(i).append("].userLocation}, ")
                   .append("#{orders[").append(i).append("].storeLocation}, ")
                   .append("#{orders[").append(i).append("].totalPrice}, ")
                   .append("#{orders[").append(i).append("].actualPrice}, ")
                   .append("#{orders[").append(i).append("].remark}, ")
                   .append("#{orders[").append(i).append("].deliveryPrice}, ")
                   .append("#{orders[").append(i).append("].createdAt}, ")
                   .append("#{orders[").append(i).append("].deadline}, ")
                   .append("#{orders[").append(i).append("].endedAt})")
                ;
            }
            
            return sql.toString();
        }
    }
}