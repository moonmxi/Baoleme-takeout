/**
 * 优惠券表数据访问接口
 * 提供优惠券表的基本CRUD操作，连接到网关数据库
 * 
 * 数据库：baoleme_gateway
 * 表名：coupon
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
 * 优惠券表Mapper接口
 * 提供优惠券数据的数据库操作方法
 */
@Mapper
@Repository
public interface CouponMapper {

    /**
     * 根据ID查询优惠券信息
     * 
     * @param id 优惠券ID
     * @return Map<String, Object> 优惠券信息
     */
    @Select("SELECT * FROM coupon WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询优惠券列表
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 优惠券列表
     */
    @Select("SELECT * FROM coupon WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据店铺ID查询优惠券列表
     * 
     * @param storeId 店铺ID
     * @return List<Map<String, Object>> 优惠券列表
     */
    @Select("SELECT * FROM coupon WHERE store_id = #{storeId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 查询用户可用的优惠券
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID（可选）
     * @return List<Map<String, Object>> 可用优惠券列表
     */
    @Select("SELECT * FROM coupon WHERE user_id = #{userId} " +
            "AND is_used = FALSE " +
            "AND (expiration_date IS NULL OR expiration_date > NOW()) " +
            "AND (#{storeId} IS NULL OR store_id = #{storeId}) " +
            "ORDER BY expiration_date ASC")
    List<Map<String, Object>> selectAvailableCoupons(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 查询用户已使用的优惠券
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 已使用优惠券列表
     */
    @Select("SELECT * FROM coupon WHERE user_id = #{userId} AND is_used = TRUE ORDER BY created_at DESC")
    List<Map<String, Object>> selectUsedCoupons(@Param("userId") Long userId);

    /**
     * 查询过期的优惠券
     * 
     * @param userId 用户ID（可选）
     * @return List<Map<String, Object>> 过期优惠券列表
     */
    @Select("SELECT * FROM coupon WHERE expiration_date < NOW() " +
            "AND (#{userId} IS NULL OR user_id = #{userId}) " +
            "ORDER BY expiration_date DESC")
    List<Map<String, Object>> selectExpiredCoupons(@Param("userId") Long userId);

    /**
     * 查询所有优惠券（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 优惠券列表
     */
    @Select("SELECT * FROM coupon ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询优惠券
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 优惠券列表
     */
    @SelectProvider(type = CouponSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新优惠券
     * 
     * @param coupon 优惠券信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO coupon (user_id, store_id, type, discount, expiration_date, created_at, is_used, full_amount, reduce_amount) " +
            "VALUES (#{userId}, #{storeId}, #{type}, #{discount}, #{expirationDate}, #{createdAt}, #{isUsed}, #{fullAmount}, #{reduceAmount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("coupon") Map<String, Object> coupon);

    /**
     * 更新优惠券信息
     * 
     * @param id 优惠券ID
     * @param coupon 更新的优惠券信息
     * @return int 影响行数
     */
    @UpdateProvider(type = CouponSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("coupon") Map<String, Object> coupon);

    /**
     * 标记优惠券为已使用
     * 
     * @param id 优惠券ID
     * @return int 影响行数
     */
    @Update("UPDATE coupon SET is_used = TRUE WHERE id = #{id} AND is_used = FALSE")
    int markAsUsed(@Param("id") Long id);

    /**
     * 批量标记优惠券为已使用
     * 
     * @param ids 优惠券ID列表
     * @return int 影响行数
     */
    @Update("<script>" +
            "UPDATE coupon SET is_used = TRUE WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "AND is_used = FALSE" +
            "</script>")
    int batchMarkAsUsed(@Param("ids") List<Long> ids);

    /**
     * 删除优惠券
     * 
     * @param id 优惠券ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM coupon WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 删除过期的优惠券
     * 
     * @return int 影响行数
     */
    @Delete("DELETE FROM coupon WHERE expiration_date < NOW()")
    int deleteExpiredCoupons();

    /**
     * 统计优惠券数量
     * 
     * @return long 优惠券总数
     */
    @Select("SELECT COUNT(*) FROM coupon")
    long count();

    /**
     * 统计用户可用优惠券数量
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID（可选）
     * @return long 可用优惠券数量
     */
    @Select("SELECT COUNT(*) FROM coupon WHERE user_id = #{userId} " +
            "AND is_used = FALSE " +
            "AND (expiration_date IS NULL OR expiration_date > NOW()) " +
            "AND (#{storeId} IS NULL OR store_id = #{storeId})")
    long countAvailableCoupons(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 根据条件统计优惠券数量
     * 
     * @param conditions 查询条件
     * @return long 优惠券数量
     */
    @SelectProvider(type = CouponSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入优惠券
     * 
     * @param coupons 优惠券列表
     * @return int 影响行数
     */
    @InsertProvider(type = CouponSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("coupons") List<Map<String, Object>> coupons);

    /**
     * 优惠券SQL提供者类
     * 动态生成SQL语句
     */
    class CouponSqlProvider {

        /**
         * 根据条件查询优惠券的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM coupon WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("type")) {
                sql.append(" AND type = #{conditions.type}");
            }
            if (conditions.containsKey("isUsed")) {
                sql.append(" AND is_used = #{conditions.isUsed}");
            }
            if (conditions.containsKey("minDiscount")) {
                sql.append(" AND discount >= #{conditions.minDiscount}");
            }
            if (conditions.containsKey("maxDiscount")) {
                sql.append(" AND discount <= #{conditions.maxDiscount}");
            }
            if (conditions.containsKey("minFullAmount")) {
                sql.append(" AND full_amount >= #{conditions.minFullAmount}");
            }
            if (conditions.containsKey("maxFullAmount")) {
                sql.append(" AND full_amount <= #{conditions.maxFullAmount}");
            }
            if (conditions.containsKey("minReduceAmount")) {
                sql.append(" AND reduce_amount >= #{conditions.minReduceAmount}");
            }
            if (conditions.containsKey("maxReduceAmount")) {
                sql.append(" AND reduce_amount <= #{conditions.maxReduceAmount}");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
            }
            if (conditions.containsKey("expirationStart")) {
                sql.append(" AND expiration_date >= #{conditions.expirationStart}");
            }
            if (conditions.containsKey("expirationEnd")) {
                sql.append(" AND expiration_date <= #{conditions.expirationEnd}");
            }
            if (conditions.containsKey("isExpired")) {
                Boolean isExpired = (Boolean) conditions.get("isExpired");
                if (isExpired) {
                    sql.append(" AND expiration_date < NOW()");
                } else {
                    sql.append(" AND (expiration_date IS NULL OR expiration_date > NOW())");
                }
            }
            if (conditions.containsKey("isAvailable")) {
                Boolean isAvailable = (Boolean) conditions.get("isAvailable");
                if (isAvailable) {
                    sql.append(" AND is_used = FALSE AND (expiration_date IS NULL OR expiration_date > NOW())");
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
         * 根据条件统计优惠券数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM coupon WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("type")) {
                sql.append(" AND type = #{conditions.type}");
            }
            if (conditions.containsKey("isUsed")) {
                sql.append(" AND is_used = #{conditions.isUsed}");
            }
            if (conditions.containsKey("minDiscount")) {
                sql.append(" AND discount >= #{conditions.minDiscount}");
            }
            if (conditions.containsKey("maxDiscount")) {
                sql.append(" AND discount <= #{conditions.maxDiscount}");
            }
            if (conditions.containsKey("minFullAmount")) {
                sql.append(" AND full_amount >= #{conditions.minFullAmount}");
            }
            if (conditions.containsKey("maxFullAmount")) {
                sql.append(" AND full_amount <= #{conditions.maxFullAmount}");
            }
            if (conditions.containsKey("minReduceAmount")) {
                sql.append(" AND reduce_amount >= #{conditions.minReduceAmount}");
            }
            if (conditions.containsKey("maxReduceAmount")) {
                sql.append(" AND reduce_amount <= #{conditions.maxReduceAmount}");
            }
            if (conditions.containsKey("startTime")) {
                sql.append(" AND created_at >= #{conditions.startTime}");
            }
            if (conditions.containsKey("endTime")) {
                sql.append(" AND created_at <= #{conditions.endTime}");
            }
            if (conditions.containsKey("expirationStart")) {
                sql.append(" AND expiration_date >= #{conditions.expirationStart}");
            }
            if (conditions.containsKey("expirationEnd")) {
                sql.append(" AND expiration_date <= #{conditions.expirationEnd}");
            }
            if (conditions.containsKey("isExpired")) {
                Boolean isExpired = (Boolean) conditions.get("isExpired");
                if (isExpired) {
                    sql.append(" AND expiration_date < NOW()");
                } else {
                    sql.append(" AND (expiration_date IS NULL OR expiration_date > NOW())");
                }
            }
            if (conditions.containsKey("isAvailable")) {
                Boolean isAvailable = (Boolean) conditions.get("isAvailable");
                if (isAvailable) {
                    sql.append(" AND is_used = FALSE AND (expiration_date IS NULL OR expiration_date > NOW())");
                }
            }
            
            return sql.toString();
        }

        /**
         * 更新优惠券信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> coupon = (Map<String, Object>) params.get("coupon");
            
            StringBuilder sql = new StringBuilder("UPDATE coupon SET ");
            boolean first = true;
            
            if (coupon.containsKey("userId")) {
                if (!first) sql.append(", ");
                sql.append("user_id = #{coupon.userId}");
                first = false;
            }
            if (coupon.containsKey("storeId")) {
                if (!first) sql.append(", ");
                sql.append("store_id = #{coupon.storeId}");
                first = false;
            }
            if (coupon.containsKey("type")) {
                if (!first) sql.append(", ");
                sql.append("type = #{coupon.type}");
                first = false;
            }
            if (coupon.containsKey("discount")) {
                if (!first) sql.append(", ");
                sql.append("discount = #{coupon.discount}");
                first = false;
            }
            if (coupon.containsKey("expirationDate")) {
                if (!first) sql.append(", ");
                sql.append("expiration_date = #{coupon.expirationDate}");
                first = false;
            }
            if (coupon.containsKey("isUsed")) {
                if (!first) sql.append(", ");
                sql.append("is_used = #{coupon.isUsed}");
                first = false;
            }
            if (coupon.containsKey("fullAmount")) {
                if (!first) sql.append(", ");
                sql.append("full_amount = #{coupon.fullAmount}");
                first = false;
            }
            if (coupon.containsKey("reduceAmount")) {
                if (!first) sql.append(", ");
                sql.append("reduce_amount = #{coupon.reduceAmount}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入优惠券的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> coupons = (List<Map<String, Object>>) params.get("coupons");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO coupon (user_id, store_id, type, discount, expiration_date, created_at, is_used, full_amount, reduce_amount) VALUES ");
            
            for (int i = 0; i < coupons.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{coupons[").append(i).append("].userId}, ")
                   .append("#{coupons[").append(i).append("].storeId}, ")
                   .append("#{coupons[").append(i).append("].type}, ")
                   .append("#{coupons[").append(i).append("].discount}, ")
                   .append("#{coupons[").append(i).append("].expirationDate}, ")
                   .append("#{coupons[").append(i).append("].createdAt}, ")
                   .append("#{coupons[").append(i).append("].isUsed}, ")
                   .append("#{coupons[").append(i).append("].fullAmount}, ")
                   .append("#{coupons[").append(i).append("].reduceAmount})")
                ;
            }
            
            return sql.toString();
        }
    }
}