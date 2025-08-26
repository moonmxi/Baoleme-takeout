/**
 * 购物车服务接口
 * 定义购物车相关的业务操作方法，包括添加、查看、更新、删除购物车商品等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.service;

import org.demo.gateway.dto.request.cart.*;
import org.demo.gateway.dto.response.cart.*;

/**
 * 购物车服务接口
 * 提供购物车相关的业务逻辑处理方法
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * 
     * @param userId 用户ID
     * @param request 添加到购物车请求对象
     * @return boolean 添加是否成功
     * @throws RuntimeException 当商品不存在或库存不足时抛出异常
     */
    boolean addToCart(Long userId, AddToCartRequest request);

    /**
     * 查看购物车内容
     * 
     * @param userId 用户ID
     * @return CartViewResponse 购物车内容响应对象
     */
    CartViewResponse viewCart(Long userId);

    /**
     * 更新购物车商品数量
     * 
     * @param userId 用户ID
     * @param request 更新购物车请求对象
     * @return boolean 更新是否成功
     * @throws RuntimeException 当商品不存在或数量无效时抛出异常
     */
    boolean updateCart(Long userId, UpdateCartRequest request);

    /**
     * 删除购物车中的指定商品
     * 
     * @param userId 用户ID
     * @param request 删除购物车商品请求对象
     * @return boolean 删除是否成功
     * @throws RuntimeException 当商品不存在时抛出异常
     */
    boolean deleteCartItem(Long userId, DeleteCartRequest request);

    /**
     * 清空购物车
     * 
     * @param userId 用户ID
     * @return boolean 清空是否成功
     */
    boolean removeCart(Long userId);

    /**
     * 获取购物车商品数量
     * 
     * @param userId 用户ID
     * @return Integer 购物车中商品的总数量
     */
    Integer getCartItemCount(Long userId);

    /**
     * 检查购物车是否为空
     * 
     * @param userId 用户ID
     * @return boolean 购物车是否为空
     */
    boolean isCartEmpty(Long userId);
}