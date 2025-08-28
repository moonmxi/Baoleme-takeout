/**
 * 购物车表数据访问接口
 * 提供购物车表的基本CRUD操作，连接到网关数据库
 * 
 * 数据库：baoleme_gateway
 * 表名：cart
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
 * 购物车表Mapper接口
 * 提供购物车数据的数据库操作方法
 */
@Mapper
@Repository
public interface CartMapper {

    /**
     * 根据用户ID查询购物车
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 购物车商品列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据商品ID查询购物车记录
     * 
     * @param productId 商品ID
     * @return List<Map<String, Object>> 购物车记录列表
     */
    @Select("SELECT * FROM cart WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByProductId(@Param("productId") Long productId);

    /**
     * 查询特定用户的特定商品购物车记录
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return Map<String, Object> 购物车记录
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Map<String, Object> selectByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 查询所有购物车记录（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 购物车记录列表
     */
    @Select("SELECT * FROM cart ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询购物车记录
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 购物车记录列表
     */
    @SelectProvider(type = CartSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新购物车记录
     * 
     * @param cart 购物车记录信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO cart (user_id, product_id, quantity, created_at) " +
            "VALUES (#{userId}, #{productId}, #{quantity}, #{createdAt})")
    int insert(@Param("cart") Map<String, Object> cart);

    /**
     * 插入或更新购物车记录
     * 如果记录已存在则更新数量，否则插入新记录
     * 
     * @param cart 购物车记录信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO cart (user_id, product_id, quantity, created_at) " +
            "VALUES (#{userId}, #{productId}, #{quantity}, #{createdAt}) " +
            "ON DUPLICATE KEY UPDATE quantity = quantity + #{quantity}, created_at = #{createdAt}")
    int insertOrUpdate(@Param("cart") Map<String, Object> cart);

    /**
     * 更新购物车商品数量
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 新数量
     * @return int 影响行数
     */
    @Update("UPDATE cart SET quantity = #{quantity}, created_at = NOW() WHERE user_id = #{userId} AND product_id = #{productId}")
    int updateQuantity(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 增加购物车商品数量
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 增加数量
     * @return int 影响行数
     */
    @Update("UPDATE cart SET quantity = quantity + #{quantity}, created_at = NOW() WHERE user_id = #{userId} AND product_id = #{productId}")
    int increaseQuantity(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 减少购物车商品数量
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 减少数量
     * @return int 影响行数
     */
    @Update("UPDATE cart SET quantity = GREATEST(quantity - #{quantity}, 0), created_at = NOW() WHERE user_id = #{userId} AND product_id = #{productId}")
    int decreaseQuantity(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 删除购物车记录
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    int delete(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 清空用户购物车
     * 
     * @param userId 用户ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int clearByUserId(@Param("userId") Long userId);

    /**
     * 删除数量为0的购物车记录
     * 
     * @return int 影响行数
     */
    @Delete("DELETE FROM cart WHERE quantity <= 0")
    int deleteZeroQuantityItems();

    /**
     * 检查购物车记录是否存在
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return boolean 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    boolean exists(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 统计购物车记录数量
     * 
     * @return long 购物车记录总数
     */
    @Select("SELECT COUNT(*) FROM cart")
    long count();

    /**
     * 统计用户购物车商品种类数量
     * 
     * @param userId 用户ID
     * @return long 商品种类数量
     */
    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户购物车商品总数量
     * 
     * @param userId 用户ID
     * @return long 商品总数量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE user_id = #{userId}")
    long sumQuantityByUserId(@Param("userId") Long userId);

    /**
     * 根据条件统计购物车记录数量
     * 
     * @param conditions 查询条件
     * @return long 购物车记录数量
     */
    @SelectProvider(type = CartSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入购物车记录
     * 
     * @param carts 购物车记录列表
     * @return int 影响行数
     */
    @InsertProvider(type = CartSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("carts") List<Map<String, Object>> carts);

    /**
     * 购物车SQL提供者类
     * 动态生成SQL语句
     */
    class CartSqlProvider {

        /**
         * 根据条件查询购物车记录的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM cart WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
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
         * 根据条件统计购物车记录数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM cart WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
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
         * 批量插入购物车记录的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> carts = (List<Map<String, Object>>) params.get("carts");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO cart (user_id, product_id, quantity, created_at) VALUES ");
            
            for (int i = 0; i < carts.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{carts[").append(i).append("].userId}, ")
                   .append("#{carts[").append(i).append("].productId}, ")
                   .append("#{carts[").append(i).append("].quantity}, ")
                   .append("#{carts[").append(i).append("].createdAt})")
                ;
            }
            
            sql.append(" ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity), created_at = VALUES(created_at)");
            
            return sql.toString();
        }
    }
}