/**
 * 评价表数据访问接口
 * 提供评价表的基本CRUD操作，连接到网关数据库
 * 
 * 数据库：baoleme_gateway
 * 表名：review
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
 * 评价表Mapper接口
 * 提供评价数据的数据库操作方法
 */
@Mapper
@Repository
public interface ReviewMapper {

    /**
     * 根据ID查询评价信息
     * 
     * @param id 评价ID
     * @return Map<String, Object> 评价信息
     */
    @Select("SELECT * FROM review WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询评价列表
     * 
     * @param userId 用户ID
     * @return List<Map<String, Object>> 评价列表
     */
    @Select("SELECT * FROM review WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据店铺ID查询评价列表
     * 
     * @param storeId 店铺ID
     * @return List<Map<String, Object>> 评价列表
     */
    @Select("SELECT * FROM review WHERE store_id = #{storeId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据商品ID查询评价列表
     * 
     * @param productId 商品ID
     * @return List<Map<String, Object>> 评价列表
     */
    @Select("SELECT * FROM review WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据评分查询评价列表
     * 
     * @param rating 评分
     * @return List<Map<String, Object>> 评价列表
     */
    @Select("SELECT * FROM review WHERE rating = #{rating} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByRating(@Param("rating") Integer rating);

    /**
     * 查询店铺的平均评分
     * 
     * @param storeId 店铺ID
     * @return Double 平均评分
     */
    @Select("SELECT AVG(rating) FROM review WHERE store_id = #{storeId}")
    Double getAverageRatingByStore(@Param("storeId") Long storeId);

    /**
     * 查询商品的平均评分
     * 
     * @param productId 商品ID
     * @return Double 平均评分
     */
    @Select("SELECT AVG(rating) FROM review WHERE product_id = #{productId}")
    Double getAverageRatingByProduct(@Param("productId") Long productId);

    /**
     * 查询店铺各评分的数量分布
     * 
     * @param storeId 店铺ID
     * @return List<Map<String, Object>> 评分分布
     */
    @Select("SELECT rating, COUNT(*) as count FROM review WHERE store_id = #{storeId} GROUP BY rating ORDER BY rating DESC")
    List<Map<String, Object>> getRatingDistributionByStore(@Param("storeId") Long storeId);

    /**
     * 查询商品各评分的数量分布
     * 
     * @param productId 商品ID
     * @return List<Map<String, Object>> 评分分布
     */
    @Select("SELECT rating, COUNT(*) as count FROM review WHERE product_id = #{productId} GROUP BY rating ORDER BY rating DESC")
    List<Map<String, Object>> getRatingDistributionByProduct(@Param("productId") Long productId);

    /**
     * 查询有图片的评价
     * 
     * @param storeId 店铺ID（可选）
     * @param productId 商品ID（可选）
     * @return List<Map<String, Object>> 有图评价列表
     */
    @Select("SELECT * FROM review WHERE image IS NOT NULL AND image != '' " +
            "AND (#{storeId} IS NULL OR store_id = #{storeId}) " +
            "AND (#{productId} IS NULL OR product_id = #{productId}) " +
            "ORDER BY created_at DESC")
    List<Map<String, Object>> selectWithImages(@Param("storeId") Long storeId, @Param("productId") Long productId);

    /**
     * 查询所有评价（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 评价列表
     */
    @Select("SELECT * FROM review ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询评价
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 评价列表
     */
    @SelectProvider(type = ReviewSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新评价
     * 
     * @param review 评价信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO review (user_id, store_id, product_id, rating, comment, image, created_at) " +
            "VALUES (#{userId}, #{storeId}, #{productId}, #{rating}, #{comment}, #{image}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("review") Map<String, Object> review);

    /**
     * 更新评价信息
     * 
     * @param id 评价ID
     * @param review 更新的评价信息
     * @return int 影响行数
     */
    @UpdateProvider(type = ReviewSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("review") Map<String, Object> review);

    /**
     * 删除评价
     * 
     * @param id 评价ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM review WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 根据用户ID删除评价
     * 
     * @param userId 用户ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM review WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据店铺ID删除评价
     * 
     * @param storeId 店铺ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM review WHERE store_id = #{storeId}")
    int deleteByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据商品ID删除评价
     * 
     * @param productId 商品ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM review WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 统计评价数量
     * 
     * @return long 评价总数
     */
    @Select("SELECT COUNT(*) FROM review")
    long count();

    /**
     * 统计店铺评价数量
     * 
     * @param storeId 店铺ID
     * @return long 店铺评价数量
     */
    @Select("SELECT COUNT(*) FROM review WHERE store_id = #{storeId}")
    long countByStoreId(@Param("storeId") Long storeId);

    /**
     * 统计商品评价数量
     * 
     * @param productId 商品ID
     * @return long 商品评价数量
     */
    @Select("SELECT COUNT(*) FROM review WHERE product_id = #{productId}")
    long countByProductId(@Param("productId") Long productId);

    /**
     * 根据条件统计评价数量
     * 
     * @param conditions 查询条件
     * @return long 评价数量
     */
    @SelectProvider(type = ReviewSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入评价
     * 
     * @param reviews 评价列表
     * @return int 影响行数
     */
    @InsertProvider(type = ReviewSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("reviews") List<Map<String, Object>> reviews);

    /**
     * 评价SQL提供者类
     * 动态生成SQL语句
     */
    class ReviewSqlProvider {

        /**
         * 根据条件查询评价的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM review WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("productId")) {
                sql.append(" AND product_id = #{conditions.productId}");
            }
            if (conditions.containsKey("rating")) {
                sql.append(" AND rating = #{conditions.rating}");
            }
            if (conditions.containsKey("minRating")) {
                sql.append(" AND rating >= #{conditions.minRating}");
            }
            if (conditions.containsKey("maxRating")) {
                sql.append(" AND rating <= #{conditions.maxRating}");
            }
            if (conditions.containsKey("comment")) {
                sql.append(" AND comment LIKE CONCAT('%', #{conditions.comment}, '%')");
            }
            if (conditions.containsKey("hasImage")) {
                Boolean hasImage = (Boolean) conditions.get("hasImage");
                if (hasImage) {
                    sql.append(" AND image IS NOT NULL AND image != ''");
                } else {
                    sql.append(" AND (image IS NULL OR image = '')");
                }
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
         * 根据条件统计评价数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM review WHERE 1=1");
            
            if (conditions.containsKey("userId")) {
                sql.append(" AND user_id = #{conditions.userId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("productId")) {
                sql.append(" AND product_id = #{conditions.productId}");
            }
            if (conditions.containsKey("rating")) {
                sql.append(" AND rating = #{conditions.rating}");
            }
            if (conditions.containsKey("minRating")) {
                sql.append(" AND rating >= #{conditions.minRating}");
            }
            if (conditions.containsKey("maxRating")) {
                sql.append(" AND rating <= #{conditions.maxRating}");
            }
            if (conditions.containsKey("comment")) {
                sql.append(" AND comment LIKE CONCAT('%', #{conditions.comment}, '%')");
            }
            if (conditions.containsKey("hasImage")) {
                Boolean hasImage = (Boolean) conditions.get("hasImage");
                if (hasImage) {
                    sql.append(" AND image IS NOT NULL AND image != ''");
                } else {
                    sql.append(" AND (image IS NULL OR image = '')");
                }
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
         * 更新评价信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> review = (Map<String, Object>) params.get("review");
            
            StringBuilder sql = new StringBuilder("UPDATE review SET ");
            boolean first = true;
            
            if (review.containsKey("userId")) {
                if (!first) sql.append(", ");
                sql.append("user_id = #{review.userId}");
                first = false;
            }
            if (review.containsKey("storeId")) {
                if (!first) sql.append(", ");
                sql.append("store_id = #{review.storeId}");
                first = false;
            }
            if (review.containsKey("productId")) {
                if (!first) sql.append(", ");
                sql.append("product_id = #{review.productId}");
                first = false;
            }
            if (review.containsKey("rating")) {
                if (!first) sql.append(", ");
                sql.append("rating = #{review.rating}");
                first = false;
            }
            if (review.containsKey("comment")) {
                if (!first) sql.append(", ");
                sql.append("comment = #{review.comment}");
                first = false;
            }
            if (review.containsKey("image")) {
                if (!first) sql.append(", ");
                sql.append("image = #{review.image}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入评价的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> reviews = (List<Map<String, Object>>) params.get("reviews");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO review (user_id, store_id, product_id, rating, comment, image, created_at) VALUES ");
            
            for (int i = 0; i < reviews.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{reviews[").append(i).append("].userId}, ")
                   .append("#{reviews[").append(i).append("].storeId}, ")
                   .append("#{reviews[").append(i).append("].productId}, ")
                   .append("#{reviews[").append(i).append("].rating}, ")
                   .append("#{reviews[").append(i).append("].comment}, ")
                   .append("#{reviews[").append(i).append("].image}, ")
                   .append("#{reviews[").append(i).append("].createdAt})")
                ;
            }
            
            return sql.toString();
        }
    }
}