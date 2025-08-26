/**
 * 购物车数据访问接口
 * 提供购物车相关的数据库操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.gateway.pojo.Cart;
import org.demo.gateway.dto.response.cart.CartResponse;

import java.util.List;

/**
 * 购物车数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 * 同时定义购物车特有的数据访问方法
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 根据用户ID和商品ID查找购物车项
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return Cart 购物车项，如果不存在则返回null
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 获取用户购物车详细信息，包含商品信息
     * 
     * @param userId 用户ID
     * @return List<CartResponse> 购物车商品详细信息列表
     */
    @Select("<script>" +
            "SELECT c.product_id, p.name as product_name, c.quantity, p.price, p.image as image_url, " +
            "       p.store_id, s.name as store_name " +
            "FROM cart c " +
            "LEFT JOIN product p ON c.product_id = p.id " +
            "LEFT JOIN store s ON p.store_id = s.id " +
            "WHERE c.user_id = #{userId} AND p.status = 1 AND s.status = 1 " +
            "ORDER BY c.created_at DESC" +
            "</script>")
    List<CartResponse> getCartDetailsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID删除所有购物车项
     * 
     * @param userId 用户ID
     * @return int 删除的记录数
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和商品ID删除购物车项
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return int 删除的记录数
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 获取用户购物车商品总数量
     * 
     * @param userId 用户ID
     * @return Integer 购物车中商品的总数量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE user_id = #{userId}")
    Integer getTotalQuantityByUserId(@Param("userId") Long userId);

    /**
     * 检查用户购物车是否为空
     * 
     * @param userId 用户ID
     * @return Integer 购物车中的商品种类数量，0表示为空
     */
    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId}")
    Integer getCartItemCountByUserId(@Param("userId") Long userId);
}