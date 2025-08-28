/**
 * 销量表数据访问接口
 * 提供销量表的基本CRUD操作，连接到商家数据库
 * 
 * 数据库：baoleme_merchant
 * 表名：sales
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 销量表Mapper接口
 * 提供销量数据的数据库操作方法
 */
@Mapper
@Repository
public interface SalesMapper {

    /**
     * 根据ID查询销量记录
     * 
     * @param id 销量记录ID
     * @return Map<String, Object> 销量记录信息
     */
    @Select("SELECT * FROM sales WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);

    /**
     * 根据商品ID查询销量记录
     * 
     * @param productId 商品ID
     * @return List<Map<String, Object>> 销量记录列表
     */
    @Select("SELECT * FROM sales WHERE product_id = #{productId} ORDER BY sale_date DESC")
    List<Map<String, Object>> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据店铺ID查询销量记录
     * 
     * @param storeId 店铺ID
     * @return List<Map<String, Object>> 销量记录列表
     */
    @Select("SELECT * FROM sales WHERE store_id = #{storeId} ORDER BY sale_date DESC")
    List<Map<String, Object>> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据销售日期查询销量记录
     * 
     * @param saleDate 销售日期
     * @return List<Map<String, Object>> 销量记录列表
     */
    @Select("SELECT * FROM sales WHERE sale_date = #{saleDate} ORDER BY id DESC")
    List<Map<String, Object>> selectBySaleDate(@Param("saleDate") LocalDate saleDate);

    /**
     * 查询所有销量记录（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Map<String, Object>> 销量记录列表
     */
    @Select("SELECT * FROM sales ORDER BY sale_date DESC, id DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据条件查询销量记录
     * 
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 销量记录列表
     */
    @SelectProvider(type = SalesSqlProvider.class, method = "selectByConditions")
    List<Map<String, Object>> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 插入新销量记录
     * 
     * @param sales 销量记录信息
     * @return int 影响行数
     */
    @Insert("INSERT INTO sales (product_id, store_id, sale_date, quantity, unit_price, payment_method, customer_id) " +
            "VALUES (#{productId}, #{storeId}, #{saleDate}, #{quantity}, #{unitPrice}, #{paymentMethod}, #{customerId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("sales") Map<String, Object> sales);

    /**
     * 更新销量记录
     * 
     * @param id 销量记录ID
     * @param sales 更新的销量记录信息
     * @return int 影响行数
     */
    @UpdateProvider(type = SalesSqlProvider.class, method = "updateById")
    int updateById(@Param("id") Long id, @Param("sales") Map<String, Object> sales);

    /**
     * 删除销量记录
     * 
     * @param id 销量记录ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM sales WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 统计销量记录数量
     * 
     * @return long 销量记录总数
     */
    @Select("SELECT COUNT(*) FROM sales")
    long count();

    /**
     * 根据条件统计销量记录数量
     * 
     * @param conditions 查询条件
     * @return long 销量记录数量
     */
    @SelectProvider(type = SalesSqlProvider.class, method = "countByConditions")
    long countByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 批量插入销量记录
     * 
     * @param salesList 销量记录列表
     * @return int 影响行数
     */
    @InsertProvider(type = SalesSqlProvider.class, method = "batchInsert")
    int batchInsert(@Param("salesList") List<Map<String, Object>> salesList);

    /**
     * 销量记录SQL提供者类
     * 动态生成SQL语句
     */
    class SalesSqlProvider {

        /**
         * 根据条件查询销量记录的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String selectByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT * FROM sales WHERE 1=1");
            
            if (conditions.containsKey("productId")) {
                sql.append(" AND product_id = #{conditions.productId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("saleDate")) {
                sql.append(" AND sale_date = #{conditions.saleDate}");
            }
            if (conditions.containsKey("startDate")) {
                sql.append(" AND sale_date >= #{conditions.startDate}");
            }
            if (conditions.containsKey("endDate")) {
                sql.append(" AND sale_date <= #{conditions.endDate}");
            }
            if (conditions.containsKey("minQuantity")) {
                sql.append(" AND quantity >= #{conditions.minQuantity}");
            }
            if (conditions.containsKey("maxQuantity")) {
                sql.append(" AND quantity <= #{conditions.maxQuantity}");
            }
            if (conditions.containsKey("minUnitPrice")) {
                sql.append(" AND unit_price >= #{conditions.minUnitPrice}");
            }
            if (conditions.containsKey("maxUnitPrice")) {
                sql.append(" AND unit_price <= #{conditions.maxUnitPrice}");
            }
            if (conditions.containsKey("minTotalAmount")) {
                sql.append(" AND total_amount >= #{conditions.minTotalAmount}");
            }
            if (conditions.containsKey("maxTotalAmount")) {
                sql.append(" AND total_amount <= #{conditions.maxTotalAmount}");
            }
            if (conditions.containsKey("paymentMethod")) {
                sql.append(" AND payment_method LIKE CONCAT('%', #{conditions.paymentMethod}, '%')");
            }
            if (conditions.containsKey("customerId")) {
                sql.append(" AND customer_id = #{conditions.customerId}");
            }
            
            // 排序
            if (conditions.containsKey("orderBy")) {
                sql.append(" ORDER BY #{conditions.orderBy}");
                if (conditions.containsKey("orderDirection")) {
                    sql.append(" #{conditions.orderDirection}");
                }
            } else {
                sql.append(" ORDER BY sale_date DESC, id DESC");
            }
            
            if (conditions.containsKey("limit")) {
                sql.append(" LIMIT #{conditions.limit}");
            }
            
            return sql.toString();
        }

        /**
         * 根据条件统计销量记录数量的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String countByConditions(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> conditions = (Map<String, Object>) params.get("conditions");
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM sales WHERE 1=1");
            
            if (conditions.containsKey("productId")) {
                sql.append(" AND product_id = #{conditions.productId}");
            }
            if (conditions.containsKey("storeId")) {
                sql.append(" AND store_id = #{conditions.storeId}");
            }
            if (conditions.containsKey("saleDate")) {
                sql.append(" AND sale_date = #{conditions.saleDate}");
            }
            if (conditions.containsKey("startDate")) {
                sql.append(" AND sale_date >= #{conditions.startDate}");
            }
            if (conditions.containsKey("endDate")) {
                sql.append(" AND sale_date <= #{conditions.endDate}");
            }
            if (conditions.containsKey("minQuantity")) {
                sql.append(" AND quantity >= #{conditions.minQuantity}");
            }
            if (conditions.containsKey("maxQuantity")) {
                sql.append(" AND quantity <= #{conditions.maxQuantity}");
            }
            if (conditions.containsKey("minUnitPrice")) {
                sql.append(" AND unit_price >= #{conditions.minUnitPrice}");
            }
            if (conditions.containsKey("maxUnitPrice")) {
                sql.append(" AND unit_price <= #{conditions.maxUnitPrice}");
            }
            if (conditions.containsKey("minTotalAmount")) {
                sql.append(" AND total_amount >= #{conditions.minTotalAmount}");
            }
            if (conditions.containsKey("maxTotalAmount")) {
                sql.append(" AND total_amount <= #{conditions.maxTotalAmount}");
            }
            if (conditions.containsKey("paymentMethod")) {
                sql.append(" AND payment_method LIKE CONCAT('%', #{conditions.paymentMethod}, '%')");
            }
            if (conditions.containsKey("customerId")) {
                sql.append(" AND customer_id = #{conditions.customerId}");
            }
            
            return sql.toString();
        }

        /**
         * 更新销量记录的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String updateById(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            Map<String, Object> sales = (Map<String, Object>) params.get("sales");
            
            StringBuilder sql = new StringBuilder("UPDATE sales SET ");
            boolean first = true;
            
            if (sales.containsKey("productId")) {
                if (!first) sql.append(", ");
                sql.append("product_id = #{sales.productId}");
                first = false;
            }
            if (sales.containsKey("storeId")) {
                if (!first) sql.append(", ");
                sql.append("store_id = #{sales.storeId}");
                first = false;
            }
            if (sales.containsKey("saleDate")) {
                if (!first) sql.append(", ");
                sql.append("sale_date = #{sales.saleDate}");
                first = false;
            }
            if (sales.containsKey("quantity")) {
                if (!first) sql.append(", ");
                sql.append("quantity = #{sales.quantity}");
                first = false;
            }
            if (sales.containsKey("unitPrice")) {
                if (!first) sql.append(", ");
                sql.append("unit_price = #{sales.unitPrice}");
                first = false;
            }
            if (sales.containsKey("paymentMethod")) {
                if (!first) sql.append(", ");
                sql.append("payment_method = #{sales.paymentMethod}");
                first = false;
            }
            if (sales.containsKey("customerId")) {
                if (!first) sql.append(", ");
                sql.append("customer_id = #{sales.customerId}");
                first = false;
            }
            
            sql.append(" WHERE id = #{id}");
            return sql.toString();
        }

        /**
         * 批量插入销量记录的SQL
         * 
         * @param params 参数
         * @return String SQL语句
         */
        public String batchInsert(Map<String, Object> params) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> salesList = (List<Map<String, Object>>) params.get("salesList");
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO sales (product_id, store_id, sale_date, quantity, unit_price, payment_method, customer_id) VALUES ");
            
            for (int i = 0; i < salesList.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("(#{salesList[").append(i).append("].productId}, ")
                   .append("#{salesList[").append(i).append("].storeId}, ")
                   .append("#{salesList[").append(i).append("].saleDate}, ")
                   .append("#{salesList[").append(i).append("].quantity}, ")
                   .append("#{salesList[").append(i).append("].unitPrice}, ")
                   .append("#{salesList[").append(i).append("].paymentMethod}, ")
                   .append("#{salesList[").append(i).append("].customerId})")
                ;
            }
            
            return sql.toString();
        }
    }
}