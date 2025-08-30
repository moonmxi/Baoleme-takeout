/**
 * 购物车数据访问接口
 * 定义购物车相关的数据库操作方法
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.userservice.dto.response.cart.CartResponse;
import org.demo.userservice.pojo.Cart;

import java.util.List;

/**
 * 购物车数据访问接口
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 根据用户ID和商品ID查找购物车项
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 根据用户ID查找购物车所有商品
     * 
     * @param userId 用户ID
     * @return 购物车商品列表
     */
    @Select("""
    SELECT 
        c.product_id,
        p.name as productName,
        c.quantity,
        p.price,
        p.image as imageUrl,
        p.store_id as storeId,
        s.name as storeName
    FROM cart c 
    JOIN product p ON c.product_id = p.id 
    JOIN store s ON p.store_id = s.id
    WHERE c.user_id = #{userId}
    ORDER BY c.created_at DESC
    """)
    List<CartResponse> findCartsByUserId(@Param("userId") Long userId);

    /**
     * 更新购物车商品数量
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 新数量
     * @return 影响行数
     */
    @Update("UPDATE cart SET quantity = #{quantity} WHERE user_id = #{userId} AND product_id = #{productId}")
    int updateQuantity(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 删除购物车中的指定商品
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteCartItemById(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 清空用户购物车
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 获取购物车商品总数量
     * 
     * @param userId 用户ID
     * @return 商品总数量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE user_id = #{userId}")
    Integer getCartItemCount(@Param("userId") Long userId);

    /**
     * 检查购物车是否为空
     * 
     * @param userId 用户ID
     * @return 购物车商品数量
     */
    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId}")
    Integer countCartItems(@Param("userId") Long userId);
}