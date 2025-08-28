/**
 * 订单明细表数据访问接口
 * 提供订单明细表的基本CRUD操作，连接到网关数据库
 * 
 * 数据库：baoleme_gateway
 * 表名：order_item
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
 * 订单明细表Mapper接口
 * 提供订单明细数据的数据库操作方法
 */
@Mapper
@Repository
public interface OrderItemMapper {

    /**
     * 根据订单ID查询订单明细列表
     * 
     * @param orderId 订单ID
     * @return List<Map<String, Object>> 订单明细列表
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId} ORDER BY product_id")
    List<Map<String, Object>> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据商品ID查询订单明细列表
     * 
     * @param productId 商品ID
     * @return List<Map<String, Object>> 订单明细列表
     */
    @Select("SELECT * FROM order_item WHERE product_id = #{productId} ORDER BY order_id DESC")
    List<Map<String, Object>> selectByProductId(@Param("productId") Long productId);

    /**
     * 查询特定订单的特定商品明细
     * 
     * @param orderId 订单ID
     * @param productId 商品ID
     * @return Map<String, Object> 订单明细
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId} AND product_id = #{productId}")
    Map<String, Object> selectByOrderAndProduct(@Param("orderId") Long orderId, @Param("productId") Long productId);

    /**
     * 查询所有订单明细（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 订单明细列表
     */
    @Select("SELECT * FROM order_item ORDER BY order_id DESC, product_id LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询订单明细
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 订单明细列表
     */
    @SelectProvider(type = OrderItemSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新订单明细
     * 
     * @param orderItem 订单明细信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO order_item (order_id, product_id, quantity) " +
            "VALUES (#{orderId}, #{productId}, #{quantity})")
    int insert(@Param("orderItem") Map<String, Object> orderItem);

    /**
     * 插入或更新订单明细
     * 如果记录已存在则更新数量，否则插入新记录
     * 
     * @param orderItem 订单明细信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO order_item (order_id, product_id, quantity) " +
            "VALUES (#{orderId}, #{productId}, #{quantity}) " +
            "ON DUPLICATE KEY UPDATE quantity = quantity + #{quantity}")
    int insertOrUpdate(@Param("orderItem") Map<String, Object> orderItem);

    /**
     * 更新订单明细数量
     * 
     * @param orderId 订单ID
     * @param productId 商品ID
     * @param quantity 新数量
     * @return int 影响行数
     */
    @Update("UPDATE order_item SET quantity = #{quantity} WHERE order_id = #{orderId} AND product_id = #{productId}")
    int updateQuantity(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 增加订单明细数量
     * 
     * @param orderId 订单ID
     * @param productId 商品ID
     * @param quantity 增加数量
     * @return int 影响行数
     */
    @Update("UPDATE order_item SET quantity = quantity + #{quantity} WHERE order_id = #{orderId} AND product_id = #{productId}")
    int increaseQuantity(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 减少订单明细数量
     * 
     * @param orderId 订单ID
     * @param productId 商品ID
     * @param quantity 减少数量
     * @return int 影响行数
     */
    @Update("UPDATE order_item SET quantity = GREATEST(quantity - #{quantity}, 0) WHERE order_id = #{orderId} AND product_id = #{productId}")
    int decreaseQuantity(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 删除订单明细
     * 
     * @param orderId 订单ID
     * @param productId 商品ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM order_item WHERE order_id = #{orderId} AND product_id = #{productId}")
    int delete(@Param("orderId") Long orderId, @Param("productId") Long productId);

    /**
     * 根据订单ID删除所有订单明细
     * 
     * @param orderId 订单ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据商品ID删除所有订单明细
     * 
     * @param productId 商品ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM order_item WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 删除数量为0的订单明细
     * 
     * @return int 影响行数
     */
    @Delete("DELETE FROM order_item WHERE quantity <= 0")
    int deleteZeroQuantityItems();

    /**
     * 检查订单明细是否存在
     * 
     * @param orderId 订单ID
     * @param productId 商品ID
     * @return boolean 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM order_item WHERE order_id = #{orderId} AND product_id = #{productId}")
    boolean exists(@Param("orderId") Long orderId, @Param("productId") Long productId);

    /**
     * 统计订单明细数量
     * 
     * @return long 订单明细总数
     */
    @Select("SELECT COUNT(*) FROM order_item")
    long count();

    /**
     * 统计订单中的商品种类数量
     * 
     * @param orderId 订单ID
     * @return long 商品种类数量
     */
    @Select("SELECT COUNT(*) FROM order_item WHERE order_id = #{orderId}")
    long countByOrderId(@Param("orderId") Long orderId);

    /**
     * 统计订单中的商品总数量
     * 
     * @param orderId 订单ID
     * @return long 商品总数量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM order_item WHERE order_id = #{orderId}")
    long sumQuantityByOrderId(@Param("orderId") Long orderId);

    /**
     * 统计商品的总销量
     * 
     * @param productId 商品ID
     * @return long 总销量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM order_item WHERE product_id = #{productId}")
    long sumQuantityByProductId(@Param("productId") Long productId);

    /**
     * 根据条件统计订单明细数量
     * 
     * @param conditions 查询条件
     * @return long 订单明细数量
     */
    @SelectProvider(type = OrderItemSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入订单明细
     * 
     * @param orderItems 订单明细列表
     * @return int 影响行数
     */
    @InsertProvider(type = OrderItemSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("orderItems") List<Map<String, Object>> orderItems);

    /**
     * 查询热销商品统计
     * 
     * @param limit 限制数量
     * @return List<Map<String, Object>> 热销商品列表
     */
    @Select("SELECT product_id, SUM(quantity) as total_quantity, COUNT(DISTINCT order_id) as order_count " +
            "FROM order_item GROUP BY product_id ORDER BY total_quantity DESC LIMIT #{limit}")
    List<Map<String, Object>> getHotProducts(@Param("limit") int limit);

    /**
     * 查询商品销售统计（按时间范围）
     * 
     * @param productId 商品ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return Map<String, Object> 销售统计
     */
    @Select("SELECT product_id, SUM(quantity) as total_quantity, COUNT(DISTINCT order_id) as order_count, " +
            "AVG(quantity) as avg_quantity FROM order_item oi " +
            "JOIN `order` o ON oi.order_id = o.id " +
            "WHERE oi.product_id = #{productId} " +
            "AND o.created_at >= #{startTime} AND o.created_at <= #{endTime} " +
            "GROUP BY product_id")
    Map<String, Object> getProductSalesStats(@Param("productId") Long productId, 
                                            @Param("startTime") java.time.LocalDateTime startTime, 
                                            @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 订单明细SQL提供者类
     * 动态生成SQL语句
     */
    class OrderItemSqlProvider {

        /**
         * 根据条件查询订单明细的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM order_item WHERE 1=1");
            
            if (conditions.containsKey("orderId")) {
                sql.append(" AND order_id = #{conditions.orderId}");
            }
            if (conditions.containsKey("productId")) {
                sql.append(" AND product_id = #{conditions.productId}");
            }
            if (conditions.containsKey("minQuantity")) {
                sql.append(" AND quantity >= #{conditions.minQuantity}");
            }
            if (conditions.containsKey("maxQuantity")) {
                sql.append(" AND quantity <= #{conditions.maxQuantity}");
            }
            if (conditions.containsKey("orderIds")) {
                sql.append(" AND order_id IN (#{conditions.orderIds})");
            }
            if (conditions.containsKey("productIds")) {
                sql.append(" AND product_id IN (#{conditions.productIds})");
            }
            
            // 排序
            if (conditions.containsKey("orderBy")) {
                sql.append(" ORDER BY #{conditions.orderBy}");
                if (conditions.containsKey("orderDirection")) {
                    sql.append(" #{conditions.orderDirection}");
                }
            } else {
                sql.append(" ORDER BY order_id DESC, product_id");
            }
            
            if (conditions.containsKey("limit")) {
                sql.append(" LIMIT #{conditions.limit}");
            }
            
            return sql.toString();
        }

        /**
         * 根据条件统计订单明细数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM order_item WHERE 1=1");
            
            if (conditions.containsKey("orderId")) {
                sql.append(" AND order_id = #{conditions.orderId}");
            }
            if (conditions.containsKey("productId")) {
                sql.append(" AND product_id = #{conditions.productId}");
            }
            if (conditions.containsKey("minQuantity")) {
                sql.append(" AND quantity >= #{conditions.minQuantity}");
            }
            if (conditions.containsKey("maxQuantity")) {
                sql.append(" AND quantity <= #{conditions.maxQuantity}");
            }
            if (conditions.containsKey("orderIds")) {
                sql.append(" AND order_id IN (#{conditions.orderIds})");
            }
            if (conditions.containsKey("productIds")) {
                sql.append(" AND product_id IN (#{conditions.productIds})");
            }
            
            return sql.toString();
        }

        /**
         * 批量插入订单明细的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> orderItems = (List<Map<String, Object>>) params.get("orderItems");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO order_item (order_id, product_id, quantity) VALUES ");
            
            for (int i = 0; i < orderItems.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{orderItems[").append(i).append("].orderId}, ")
                   .append("#{orderItems[").append(i).append("].productId}, ")
                   .append("#{orderItems[").append(i).append("].quantity})")
                ;
            }
            
            sql.append(" ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)");
            
            return sql.toString();
        }
    }
}