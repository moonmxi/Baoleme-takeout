/**
 * 店铺表数据访问接口
 * 提供店铺表的基本CRUD操作，连接到商家数据库
 * 
 * 数据库：baoleme_merchant
 * 表名：store
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
 * 店铺表Mapper接口
 * 提供店铺数据的数据库操作方法
 */
@Mapper
@Repository
public interface StoreMapper {

    /**
     * 根据ID查询店铺信息
     * 
     * @param id 店铺ID
     * @return Map<String, Object> 店铺信息
     */
    @Select("SELECT * FROM store WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据商家ID查询店铺列表
     * 
     * @param merchantId 商家ID
     * @return List<Map<String, Object>> 店铺列表
     */
    @Select("SELECT * FROM store WHERE merchant_id = #{merchantId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 根据店铺名称查询店铺信息
     * 
     * @param name 店铺名称
     * @return Map<String, Object> 店铺信息
     */
    @Select("SELECT * FROM store WHERE name = #{name}")
    Map<String, Object> selectByName(@Param("name") String name);

    /**
     * 查询所有店铺信息（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 店铺列表
     */
    @Select("SELECT * FROM store ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询店铺
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 店铺列表
     */
    @SelectProvider(type = StoreSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新店铺
     * 
     * @param store 店铺信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO store (merchant_id, name, type, description, location, distance, rating, status, avg_price, created_at, image) " +
            "VALUES (#{merchantId}, #{name}, #{type}, #{description}, #{location}, #{distance}, #{rating}, #{status}, #{avgPrice}, #{createdAt}, #{image})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("store") Map<String, Object> store);

    /**
     * 更新店铺信息
     * 
     * @param id 店铺ID
     * @param store 更新的店铺信息
     * @return int 影响行数
     */
    @UpdateProvider(type = StoreSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("store") Map<String, Object> store);

    /**
     * 更新店铺状态
     * 
     * @param id 店铺ID
     * @param status 状态
     * @return int 影响行数
     */
    @Update("UPDATE store SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新店铺评分
     * 
     * @param id 店铺ID
     * @param rating 评分
     * @return int 影响行数
     */
    @Update("UPDATE store SET rating = #{rating} WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("rating") Double rating);

    /**
     * 删除店铺
     * 
     * @param id 店铺ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM store WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 统计店铺数量
     * 
     * @return long 店铺总数
     */
    @Select("SELECT COUNT(*) FROM store")
    long count();

    /**
     * 根据商家ID统计店铺数量
     * 
     * @param merchantId 商家ID
     * @return long 店铺数量
     */
    @Select("SELECT COUNT(*) FROM store WHERE merchant_id = #{merchantId}")
    long countByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 根据条件统计店铺数量
     * 
     * @param conditions 查询条件
     * @return long 店铺数量
     */
    @SelectProvider(type = StoreSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入店铺
     * 
     * @param stores 店铺列表
     * @return int 影响行数
     */
    @InsertProvider(type = StoreSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("stores") List<Map<String, Object>> stores);

    /**
     * 店铺SQL提供者类
     * 动态生成SQL语句
     */
    class StoreSqlProvider {

        /**
         * 根据条件查询店铺的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM store WHERE 1=1");
            
            if (conditions.containsKey("merchantId")) {
                sql.append(" AND merchant_id = #{conditions.merchantId}");
            }
            if (conditions.containsKey("name")) {
                sql.append(" AND name LIKE CONCAT('%', #{conditions.name}, '%')");
            }
            if (conditions.containsKey("type")) {
                sql.append(" AND type = #{conditions.type}");
            }
            if (conditions.containsKey("status")) {
                sql.append(" AND status = #{conditions.status}");
            }
            if (conditions.containsKey("minRating")) {
                sql.append(" AND rating >= #{conditions.minRating}");
            }
            if (conditions.containsKey("maxDistance")) {
                sql.append(" AND distance <= #{conditions.maxDistance}");
            }
            if (conditions.containsKey("location")) {
                sql.append(" AND location LIKE CONCAT('%', #{conditions.location}, '%')");
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
         * 根据条件统计店铺数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM store WHERE 1=1");
            
            if (conditions.containsKey("merchantId")) {
                sql.append(" AND merchant_id = #{conditions.merchantId}");
            }
            if (conditions.containsKey("name")) {
                sql.append(" AND name LIKE CONCAT('%', #{conditions.name}, '%')");
            }
            if (conditions.containsKey("type")) {
                sql.append(" AND type = #{conditions.type}");
            }
            if (conditions.containsKey("status")) {
                sql.append(" AND status = #{conditions.status}");
            }
            if (conditions.containsKey("minRating")) {
                sql.append(" AND rating >= #{conditions.minRating}");
            }
            if (conditions.containsKey("maxDistance")) {
                sql.append(" AND distance <= #{conditions.maxDistance}");
            }
            if (conditions.containsKey("location")) {
                sql.append(" AND location LIKE CONCAT('%', #{conditions.location}, '%')");
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
         * 更新店铺信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> store = (Map<String, Object>) params.get("store");
            
            StringBuilder sql = new StringBuilder("UPDATE store SET ");
            boolean first = true;
            
            if (store.containsKey("name")) {
                if (!first) sql.append(", ");
                sql.append("name = #{store.name}");
                first = false;
            }
            if (store.containsKey("type")) {
                if (!first) sql.append(", ");
                sql.append("type = #{store.type}");
                first = false;
            }
            if (store.containsKey("description")) {
                if (!first) sql.append(", ");
                sql.append("description = #{store.description}");
                first = false;
            }
            if (store.containsKey("location")) {
                if (!first) sql.append(", ");
                sql.append("location = #{store.location}");
                first = false;
            }
            if (store.containsKey("distance")) {
                if (!first) sql.append(", ");
                sql.append("distance = #{store.distance}");
                first = false;
            }
            if (store.containsKey("rating")) {
                if (!first) sql.append(", ");
                sql.append("rating = #{store.rating}");
                first = false;
            }
            if (store.containsKey("status")) {
                if (!first) sql.append(", ");
                sql.append("status = #{store.status}");
                first = false;
            }
            if (store.containsKey("avgPrice")) {
                if (!first) sql.append(", ");
                sql.append("avg_price = #{store.avgPrice}");
                first = false;
            }
            if (store.containsKey("image")) {
                if (!first) sql.append(", ");
                sql.append("image = #{store.image}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入店铺的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> stores = (List<Map<String, Object>>) params.get("stores");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO store (merchant_id, name, type, description, location, distance, rating, status, avg_price, created_at, image) VALUES ");
            
            for (int i = 0; i < stores.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{stores[").append(i).append("].merchantId}, ")
                   .append("#{stores[").append(i).append("].name}, ")
                   .append("#{stores[").append(i).append("].type}, ")
                   .append("#{stores[").append(i).append("].description}, ")
                   .append("#{stores[").append(i).append("].location}, ")
                   .append("#{stores[").append(i).append("].distance}, ")
                   .append("#{stores[").append(i).append("].rating}, ")
                   .append("#{stores[").append(i).append("].status}, ")
                   .append("#{stores[").append(i).append("].avgPrice}, ")
                   .append("#{stores[").append(i).append("].createdAt}, ")
                   .append("#{stores[").append(i).append("].image})")
                ;
            }
            
            return sql.toString();
        }
    }
}