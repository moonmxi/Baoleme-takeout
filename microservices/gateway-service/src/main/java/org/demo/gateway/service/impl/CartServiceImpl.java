/**
 * 购物车服务实现类
 * 实现购物车相关的业务逻辑，包括添加、查看、更新、删除购物车商品等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.service.impl;

import org.demo.gateway.dto.request.cart.*;
import org.demo.gateway.dto.response.cart.*;
import org.demo.gateway.mapper.CartMapper;
import org.demo.gateway.pojo.Cart;
import org.demo.gateway.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车服务实现类
 * 提供购物车相关的业务逻辑处理
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    /**
     * 购物车数据访问接口
     */
    @Autowired
    private CartMapper cartMapper;

    /**
     * 添加商品到购物车
     * 如果商品已存在，则增加数量；如果不存在，则新增记录
     * 
     * @param userId 用户ID
     * @param request 添加到购物车请求对象
     * @return boolean 添加是否成功
     * @throws RuntimeException 当商品不存在或库存不足时抛出异常
     */
    @Override
    public boolean addToCart(Long userId, AddToCartRequest request) {
        try {
            // 检查购物车中是否已存在该商品
            Cart existingCart = cartMapper.findByUserIdAndProductId(userId, request.getProductId());
            
            if (existingCart != null) {
                // 商品已存在，更新数量
                existingCart.setQuantity(existingCart.getQuantity() + request.getQuantity());
                existingCart.setUpdatedAt(LocalDateTime.now());
                return cartMapper.updateById(existingCart) > 0;
            } else {
                // 商品不存在，新增记录
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                newCart.setProductId(request.getProductId());
                newCart.setQuantity(request.getQuantity());
                newCart.setCreatedAt(LocalDateTime.now());
                newCart.setUpdatedAt(LocalDateTime.now());
                return cartMapper.insert(newCart) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("添加到购物车失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查看购物车内容
     * 
     * @param userId 用户ID
     * @return CartViewResponse 购物车内容响应对象
     */
    @Override
    public CartViewResponse viewCart(Long userId) {
        try {
            List<CartResponse> items = cartMapper.getCartDetailsByUserId(userId);
            
            CartViewResponse response = new CartViewResponse();
            response.setItems(items);
            
            // 计算总价和总数量
            BigDecimal totalPrice = BigDecimal.ZERO;
            Integer totalQuantity = 0;
            
            for (CartResponse item : items) {
                if (item.getPrice() != null && item.getQuantity() != null) {
                    totalPrice = totalPrice.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                    totalQuantity += item.getQuantity();
                }
            }
            
            response.setTotalPrice(totalPrice);
            response.setTotalQuantity(totalQuantity);
            response.setIsEmpty(items.isEmpty());
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("查看购物车失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新购物车商品数量
     * 
     * @param userId 用户ID
     * @param request 更新购物车请求对象
     * @return boolean 更新是否成功
     * @throws RuntimeException 当商品不存在或数量无效时抛出异常
     */
    @Override
    public boolean updateCart(Long userId, UpdateCartRequest request) {
        try {
            Cart existingCart = cartMapper.findByUserIdAndProductId(userId, request.getProductId());
            
            if (existingCart == null) {
                throw new RuntimeException("购物车中不存在该商品");
            }
            
            if (request.getQuantity() <= 0) {
                // 数量为0或负数，删除该商品
                return cartMapper.deleteByUserIdAndProductId(userId, request.getProductId()) > 0;
            } else {
                // 更新数量
                existingCart.setQuantity(request.getQuantity());
                existingCart.setUpdatedAt(LocalDateTime.now());
                return cartMapper.updateById(existingCart) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("更新购物车失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除购物车中的指定商品
     * 
     * @param userId 用户ID
     * @param request 删除购物车商品请求对象
     * @return boolean 删除是否成功
     * @throws RuntimeException 当商品不存在时抛出异常
     */
    @Override
    public boolean deleteCartItem(Long userId, DeleteCartRequest request) {
        try {
            int deletedRows = cartMapper.deleteByUserIdAndProductId(userId, request.getProductId());
            return deletedRows > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除购物车商品失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清空购物车
     * 
     * @param userId 用户ID
     * @return boolean 清空是否成功
     */
    @Override
    public boolean removeCart(Long userId) {
        try {
            int deletedRows = cartMapper.deleteByUserId(userId);
            return deletedRows >= 0; // 即使购物车为空，也算成功
        } catch (Exception e) {
            throw new RuntimeException("清空购物车失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取购物车商品数量
     * 
     * @param userId 用户ID
     * @return Integer 购物车中商品的总数量
     */
    @Override
    public Integer getCartItemCount(Long userId) {
        try {
            return cartMapper.getTotalQuantityByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("获取购物车商品数量失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查购物车是否为空
     * 
     * @param userId 用户ID
     * @return boolean 购物车是否为空
     */
    @Override
    public boolean isCartEmpty(Long userId) {
        try {
            Integer count = cartMapper.getCartItemCountByUserId(userId);
            return count == null || count == 0;
        } catch (Exception e) {
            throw new RuntimeException("检查购物车状态失败: " + e.getMessage(), e);
        }
    }
}