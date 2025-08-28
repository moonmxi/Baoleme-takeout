/**
 * 商品详情数据访问接口
 * 提供商品详情查看相关的数据库操作方法
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.merchantservice.dto.response.product.ProductInfoResponse;
import org.demo.merchantservice.dto.response.product.ProductReviewResponse;
import org.demo.merchantservice.pojo.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品详情数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 * 同时定义商品详情查看特有的数据访问方法
 */
@Mapper
public interface ProductDetailMapper extends BaseMapper<Product> {

    /**
     * 获取商品详细信息（包含店铺信息）
     * 
     * @param productId 商品ID
     * @return ProductInfoResponse 商品详细信息
     */
    @Select("SELECT p.id, p.store_id, p.name, p.category, p.price, p.description, " +
            "       p.image, p.stock, p.rating, p.status, p.created_at, " +
            "       s.name as store_name, s.location as store_location " +
            "FROM product p " +
            "LEFT JOIN store s ON p.store_id = s.id " +
            "WHERE p.id = #{productId} AND p.status = 1 AND s.status = 1")
    ProductInfoResponse getProductDetailInfo(@Param("productId") Long productId);

    /**
     * 获取商品评价列表
     * 
     * @param productId 商品ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<ProductReviewResponse> 商品评价列表
     */
    @Select("SELECT r.id, r.user_id, r.product_id, r.store_id, r.rating, r.comment, " +
            "       r.image as images, r.created_at, " +
            "       u.username, u.avatar as user_avatar " +
            "FROM review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "WHERE r.product_id = #{productId} " +
            "ORDER BY r.created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    @Results({
            @Result(property = "images", column = "images", typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    })
    List<ProductReviewResponse> getProductReviews(@Param("productId") Long productId, 
                                                  @Param("offset") int offset, 
                                                  @Param("limit") int limit);

    /**
     * 获取店铺商品列表
     * 
     * @param storeId 店铺ID
     * @param category 商品分类（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Product> 商品列表
     */
    @Select("SELECT * FROM product " +
            "WHERE store_id = #{storeId} AND status = 1 " +
            "AND (#{category} IS NULL OR #{category} = '' OR category = #{category}) " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Product> getStoreProducts(@Param("storeId") Long storeId,
                                  @Param("category") String category,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    /**
     * 获取店铺商品总数
     * 
     * @param storeId 店铺ID
     * @param category 商品分类（可选）
     * @return Integer 商品总数
     */
    @Select("SELECT COUNT(*) FROM product " +
            "WHERE store_id = #{storeId} AND status = 1 " +
            "AND (#{category} IS NULL OR #{category} = '' OR category = #{category})")
    Integer getStoreProductCount(@Param("storeId") Long storeId,
                                @Param("category") String category);

    /**
     * 获取商品评价统计信息
     * 
     * @param productId 商品ID
     * @return Map<String, Object> 评价统计信息
     */
    @Select("SELECT COUNT(*) as review_count, COALESCE(AVG(rating), 0) as average_rating " +
            "FROM review WHERE product_id = #{productId}")
    Map<String, Object> getProductReviewStats(@Param("productId") Long productId);

    /**
     * 根据关键词搜索商品
     * 
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Product> 商品列表
     */
    @Select("SELECT p.* FROM product p " +
            "LEFT JOIN store s ON p.store_id = s.id " +
            "WHERE p.status = 1 AND s.status = 1 " +
            "AND (#{keyword} IS NULL OR #{keyword} = '' OR " +
            "     p.name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "     p.description LIKE CONCAT('%', #{keyword}, '%') OR " +
            "     p.category LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY p.rating DESC, p.created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Product> searchProducts(@Param("keyword") String keyword,
                                @Param("offset") int offset,
                                @Param("limit") int limit);
}