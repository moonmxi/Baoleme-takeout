/**
 * 收藏夹表数据访问接口
 * 提供收藏夹表的基本CRUD操作，连接到用户数据库
 * 
 * 数据库：baoleme_user
 * 表名：favorite
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
 * 收藏夹表Mapper接口
 * 提供收藏夹数据的数据库操作方法
 */
@Mapper
@Repository
public interface FavoriteMapper {

    /**
     * 根据用户ID查询收藏记录
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 收藏记录列表
     */
    @Select("SELECT * FROM favorite WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据商品ID查询收藏记录
     * 
     * @param productId 商品ID
     * @return List<Map<String, Object>> 收藏记录列表
     */
    @Select("SELECT * FROM favorite WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据店铺ID查询收藏记录
     * 
     * @param storeId 店铺ID
     * @return List<Map<String, Object>> 收藏记录列表
     */
    @Select("SELECT * FROM favorite WHERE store_id = #{storeId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 查询用户收藏的商品
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 收藏的商品列表
     */
    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND product_id IS NOT NULL ORDER BY created_at DESC")
    List<Map<String, Object>> selectUserFavoriteProducts(@Param("userId") Long userId);

    /**
     * 查询用户收藏的店铺
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 收藏的店铺列表
     */
    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND store_id IS NOT NULL ORDER BY created_at DESC")
    List<Map<String, Object>> selectUserFavoriteStores(@Param("userId") Long userId);

    /**
     * 查询所有收藏记录（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 收藏记录列表
     */
    @Select("SELECT * FROM favorite ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询收藏记录
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 收藏记录列表
     */
    @SelectProvider(type = FavoriteSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新收藏记录
     * 
     * @param favorite 收藏记录信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO favorite (user_id, product_id, store_id, created_at) " +
            "VALUES (#{userId}, #{productId}, #{storeId}, #{createdAt})")
    int insert(@Param("favorite") Map<String, Object> favorite);

    /**
     * 检查收藏记录是否存在
     * 
     * @param userId 用户ID
     * @param productId 商品ID（可为空）
     * @param storeId 店铺ID（可为空）
     * @return boolean 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM favorite WHERE user_id = #{userId} " +
            "AND (#{productId} IS NULL OR product_id = #{productId}) " +
            "AND (#{storeId} IS NULL OR store_id = #{storeId})")
    boolean exists(@Param("userId") Long userId, @Param("productId") Long productId, @Param("storeId") Long storeId);

    /**
     * 删除收藏记录
     * 
     * @param userId 用户ID
     * @param productId 商品ID（可为空）
     * @param storeId 店铺ID（可为空）
     * @return int 影响行数
     */
    @Delete("DELETE FROM favorite WHERE user_id = #{userId} " +
            "AND (#{productId} IS NULL OR product_id = #{productId}) " +
            "AND (#{storeId} IS NULL OR store_id = #{storeId})")
    int delete(@Param("userId") Long userId, @Param("productId") Long productId, @Param("storeId") Long storeId);

    /**
     * 根据用户ID删除所有收藏记录
     * 
     * @param userId 用户ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM favorite WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 统计收藏记录数量
     * 
     * @return long 收藏记录总数
     */
    @Select("SELECT COUNT(*) FROM favorite")
    long count();

    /**
     * 根据条件统计收藏记录数量
     * 
     * @param conditions 查询条件
     * @return long 收藏记录数量
     */
    @SelectProvider(type = FavoriteSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入收藏记录
     * 
     * @param favorites 收藏记录列表
     * @return int 影响行数
     */
    @InsertProvider(type = FavoriteSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("favorites") List<Map<String, Object>> favorites);

    /**
     * 收藏记录SQL提供者类
     * 动态生成SQL语句
     */
    class FavoriteSqlProvider {

        /**
         * 根据条件查询收藏记录的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM favorite WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("productId")) {
                sql.append(" AND product_id = #{conditions.productId}");
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
            if (conditions.containsKey("favoriteType")) {
                String favoriteType = (String) conditions.get("favoriteType");
                if ("product".equals(favoriteType)) {
                    sql.append(" AND product_id IS NOT NULL");
                } else if ("store".equals(favoriteType)) {
                    sql.append(" AND store_id IS NOT NULL");
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
         * 根据条件统计收藏记录数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM favorite WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("productId")) {
                sql.append(" AND product_id = #{conditions.productId}");
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
            if (conditions.containsKey("favoriteType")) {
                String favoriteType = (String) conditions.get("favoriteType");
                if ("product".equals(favoriteType)) {
                    sql.append(" AND product_id IS NOT NULL");
                } else if ("store".equals(favoriteType)) {
                    sql.append(" AND store_id IS NOT NULL");
                }
            }
            
            return sql.toString();
        }

        /**
         * 批量插入收藏记录的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> favorites = (List<Map<String, Object>>) params.get("favorites");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO favorite (user_id, product_id, store_id, created_at) VALUES ");
            
            for (int i = 0; i < favorites.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{favorites[").append(i).append("].userId}, ")
                   .append("#{favorites[").append(i).append("].productId}, ")
                   .append("#{favorites[").append(i).append("].storeId}, ")
                   .append("#{favorites[").append(i).append("].createdAt})")
                ;
            }
            
            return sql.toString();
        }
    }
}