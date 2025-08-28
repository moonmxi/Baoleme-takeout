/**
 * 商品表数据访问接口
 * 提供商品表的基本CRUD操作，连接到商家数据库
 * 
 * 数据库：baoleme_merchant
 * 表名：product
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
 * 商品表Mapper接口
 * 提供商品数据的数据库操作方法
 */
@Mapper
@Repository
public interface ProductMapper {

    /**
     * 根据ID查询商品信息
     * 
     * @param id 商品ID
     * @return Map<String, Object> 商品信息
     */
    @Select("SELECT * FROM product WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据店铺ID查询商品列表
     * 
     * @param storeId 店铺ID
     * @return List<Map<String, Object>> 商品列表
     */
    @Select("SELECT * FROM product WHERE store_id = #{storeId} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据商品名称查询商品信息
     * 
     * @param name 商品名称
     * @return List<Map<String, Object>> 商品列表
     */
    @Select("SELECT * FROM product WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Map<String, Object>> selectByName(@Param("name") String name);

    /**
     * 根据分类查询商品
     * 
     * @param category 商品分类
     * @return List<Map<String, Object>> 商品列表
     */
    @Select("SELECT * FROM product WHERE category = #{category} ORDER BY created_at DESC")
    List<Map<String, Object>> selectByCategory(@Param("category") String category);

    /**
     * 查询所有商品信息（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 商品列表
     */
    @Select("SELECT * FROM product ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询商品
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 商品列表
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新商品
     * 
     * @param product 商品信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO product (store_id, name, description, price, category, stock, rating, status, created_at, image) " +
            "VALUES (#{storeId}, #{name}, #{description}, #{price}, #{category}, #{stock}, #{rating}, #{status}, #{createdAt}, #{image})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("product") Map<String, Object> product);

    /**
     * 更新商品信息
     * 
     * @param id 商品ID
     * @param product 更新的商品信息
     * @return int 影响行数
     */
    @UpdateProvider(type = ProductSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("product") Map<String, Object> product);

    /**
     * 更新商品状态
     * 
     * @param id 商品ID
     * @param status 状态
     * @return int 影响行数
     */
    @Update("UPDATE product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新商品库存
     * 
     * @param id 商品ID
     * @param stock 库存数量
     * @return int 影响行数
     */
    @Update("UPDATE product SET stock = #{stock} WHERE id = #{id}")
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);

    /**
     * 减少商品库存
     * 
     * @param id 商品ID
     * @param quantity 减少数量
     * @return int 影响行数
     */
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 增加商品库存
     * 
     * @param id 商品ID
     * @param quantity 增加数量
     * @return int 影响行数
     */
    @Update("UPDATE product SET stock = stock + #{quantity} WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 更新商品评分
     * 
     * @param id 商品ID
     * @param rating 评分
     * @return int 影响行数
     */
    @Update("UPDATE product SET rating = #{rating} WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("rating") Double rating);

    /**
     * 删除商品
     * 
     * @param id 商品ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 统计商品数量
     * 
     * @return long 商品总数
     */
    @Select("SELECT COUNT(*) FROM product")
    long count();

    /**
     * 根据店铺ID统计商品数量
     * 
     * @param storeId 店铺ID
     * @return long 商品数量
     */
    @Select("SELECT COUNT(*) FROM product WHERE store_id = #{storeId}")
    long countByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据条件统计商品数量
     * 
     * @param conditions 查询条件
     * @return long 商品数量
     */
    @SelectProvider(type = ProductSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入商品
     * 
     * @param products 商品列表
     * @return int 影响行数
     */
    @InsertProvider(type = ProductSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("products") List<Map<String, Object>> products);

    /**
     * 批量更新商品库存
     * 
     * @param products 商品列表（包含id和stock字段）
     * @return int 影响行数
     */
    @UpdateProvider(type = ProductSqlProvider.class, method = "batchUpdateStock")
    int batchUpdateStock(@Param("products") List<Map<String, Object>> products);

    /**
     * 商品SQL提供者类
     * 动态生成SQL语句
     */
    class ProductSqlProvider {

        /**
         * 根据条件查询商品的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
            
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("name")) {
                sql.append(" AND name LIKE CONCAT('%', #{conditions.name}, '%')");
            }
            if (conditions.containsKey("category")) {
                sql.append(" AND category = #{conditions.category}");
            }
            if (conditions.containsKey("status")) {
                sql.append(" AND status = #{conditions.status}");
            }
            if (conditions.containsKey("minPrice")) {
                sql.append(" AND price >= #{conditions.minPrice}");
            }
            if (conditions.containsKey("maxPrice")) {
                sql.append(" AND price <= #{conditions.maxPrice}");
            }
            if (conditions.containsKey("minRating")) {
                sql.append(" AND rating >= #{conditions.minRating}");
            }
            if (conditions.containsKey("inStock")) {
                if ((Boolean) conditions.get("inStock")) {
                    sql.append(" AND stock > 0");
                } else {
                    sql.append(" AND stock = 0");
                }
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
         * 根据条件统计商品数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM product WHERE 1=1");
            
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("name")) {
                sql.append(" AND name LIKE CONCAT('%', #{conditions.name}, '%')");
            }
            if (conditions.containsKey("category")) {
                sql.append(" AND category = #{conditions.category}");
            }
            if (conditions.containsKey("status")) {
                sql.append(" AND status = #{conditions.status}");
            }
            if (conditions.containsKey("minPrice")) {
                sql.append(" AND price >= #{conditions.minPrice}");
            }
            if (conditions.containsKey("maxPrice")) {
                sql.append(" AND price <= #{conditions.maxPrice}");
            }
            if (conditions.containsKey("minRating")) {
                sql.append(" AND rating >= #{conditions.minRating}");
            }
            if (conditions.containsKey("inStock")) {
                if ((Boolean) conditions.get("inStock")) {
                    sql.append(" AND stock > 0");
                } else {
                    sql.append(" AND stock = 0");
                }
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
         * 更新商品信息的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> product = (Map<String, Object>) params.get("product");
            
            StringBuilder sql = new StringBuilder("UPDATE product SET ");
            boolean first = true;
            
            if (product.containsKey("name")) {
                if (!first) sql.append(", ");
                sql.append("name = #{product.name}");
                first = false;
            }
            if (product.containsKey("description")) {
                if (!first) sql.append(", ");
                sql.append("description = #{product.description}");
                first = false;
            }
            if (product.containsKey("price")) {
                if (!first) sql.append(", ");
                sql.append("price = #{product.price}");
                first = false;
            }
            if (product.containsKey("category")) {
                if (!first) sql.append(", ");
                sql.append("category = #{product.category}");
                first = false;
            }
            if (product.containsKey("stock")) {
                if (!first) sql.append(", ");
                sql.append("stock = #{product.stock}");
                first = false;
            }
            if (product.containsKey("rating")) {
                if (!first) sql.append(", ");
                sql.append("rating = #{product.rating}");
                first = false;
            }
            if (product.containsKey("status")) {
                if (!first) sql.append(", ");
                sql.append("status = #{product.status}");
                first = false;
            }
            if (product.containsKey("image")) {
                if (!first) sql.append(", ");
                sql.append("image = #{product.image}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入商品的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> products = (List<Map<String, Object>>) params.get("products");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO product (store_id, name, description, price, category, stock, rating, status, created_at, image) VALUES ");
            
            for (int i = 0; i < products.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{products[").append(i).append("].storeId}, ")
                   .append("#{products[").append(i).append("].name}, ")
                   .append("#{products[").append(i).append("].description}, ")
                   .append("#{products[").append(i).append("].price}, ")
                   .append("#{products[").append(i).append("].category}, ")
                   .append("#{products[").append(i).append("].stock}, ")
                   .append("#{products[").append(i).append("].rating}, ")
                   .append("#{products[").append(i).append("].status}, ")
                   .append("#{products[").append(i).append("].createdAt}, ")
                   .append("#{products[").append(i).append("].image})")
                ;
            }
            
            return sql.toString();
        }

        /**
         * 批量更新商品库存的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchUpdateStock(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> products = (List<Map<String, Object>>) params.get("products");
            
            StringBuilder sql = new StringBuilder();
            
            for (int i = 0; i < products.size(); i++) {
                if (i > 0) sql.append("; ");
                sql.append("UPDATE product SET stock = #{products[").append(i).append("].stock} ")
                   .append("WHERE id = #{products[").append(i).append("].id}");
            }
            
            return sql.toString();
        }
    }
}