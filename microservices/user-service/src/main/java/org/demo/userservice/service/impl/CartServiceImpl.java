/**
 * 购物车业务服务实现类
 * 实现购物车相关的业务逻辑
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.service.impl;

import org.demo.userservice.dto.request.cart.*;
import org.demo.userservice.dto.response.cart.*;
import org.demo.userservice.mapper.CartMapper;
import org.demo.userservice.pojo.Cart;
import org.demo.userservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车业务服务实现类
 */
@Service
public class CartServiceImpl implements CartService {

    /**
     * 购物车数据访问接口
     */
    @Autowired
    private CartMapper cartMapper;

    /**
     * 添加商品到购物车
     * 
     * @param userId 用户ID
     * @param request 添加到购物车请求对象
     * @return boolean 添加是否成功
     */
    @Override
    @Transactional
    public boolean addToCart(Long userId, AddToCartRequest request) {
        try {
            if (request.getQuantity() <= 0) {
                throw new IllegalArgumentException("商品数量必须大于0");
            }

            Cart existingItem = cartMapper.findByUserIdAndProductId(userId, request.getProductId());

            if (existingItem != null) {
                // 如果商品已存在，更新数量
                int newQuantity = existingItem.getQuantity() + request.getQuantity();
                existingItem.setQuantity(newQuantity);
                return cartMapper.updateById(existingItem) > 0;
            } else {
                // 如果商品不存在，新增购物车项
                Cart newItem = new Cart();
                newItem.setUserId(userId);
                newItem.setProductId(request.getProductId());
                newItem.setQuantity(request.getQuantity());
                newItem.setCreatedAt(LocalDateTime.now());
                return cartMapper.insert(newItem) > 0;
            }
        } catch (Exception e) {
            return false;
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
            List<CartResponse> items = cartMapper.findCartsByUserId(userId);
            BigDecimal totalPrice = calculateTotalPrice(items);
            Integer totalQuantity = calculateTotalQuantity(items);
            Boolean isEmpty = items.isEmpty();

            CartViewResponse response = new CartViewResponse();
            response.setItems(items);
            response.setTotalPrice(totalPrice);
            response.setTotalQuantity(totalQuantity);
            response.setIsEmpty(isEmpty);
            return response;
        } catch (Exception e) {
            // 返回空购物车
            CartViewResponse response = new CartViewResponse();
            response.setItems(List.of());
            response.setTotalPrice(BigDecimal.ZERO);
            response.setTotalQuantity(0);
            response.setIsEmpty(true);
            return response;
        }
    }

    /**
     * 更新购物车商品数量
     * 
     * @param userId 用户ID
     * @param request 更新购物车请求对象
     * @return boolean 更新是否成功
     */
    @Override
    @Transactional
    public boolean updateCart(Long userId, UpdateCartRequest request) {
        try {
            if (request.getQuantity() <= 0) {
                throw new IllegalArgumentException("商品数量必须大于0");
            }

            Cart item = cartMapper.findByUserIdAndProductId(userId, request.getProductId());
            if (item == null) {
                return false;
            }
            
            return cartMapper.updateQuantity(userId, request.getProductId(), request.getQuantity()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除购物车中的指定商品
     * 
     * @param userId 用户ID
     * @param request 删除购物车商品请求对象
     * @return boolean 删除是否成功
     */
    @Override
    @Transactional
    public boolean deleteCartItem(Long userId, DeleteCartRequest request) {
        try {
            return cartMapper.deleteCartItemById(userId, request.getProductId()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 清空购物车
     * 
     * @param userId 用户ID
     * @return boolean 清空是否成功
     */
    @Override
    @Transactional
    public boolean removeCart(Long userId) {
        try {
            return cartMapper.deleteByUserId(userId) > 0;
        } catch (Exception e) {
            return false;
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
            Integer count = cartMapper.getCartItemCount(userId);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
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
            Integer count = cartMapper.countCartItems(userId);
            return count == null || count == 0;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 计算购物车总价
     * 
     * @param items 购物车商品列表
     * @return 总价
     */
    private BigDecimal calculateTotalPrice(List<CartResponse> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算购物车商品总数量
     * 
     * @param items 购物车商品列表
     * @return 总数量
     */
    private Integer calculateTotalQuantity(List<CartResponse> items) {
        return items.stream()
                .mapToInt(CartResponse::getQuantity)
                .sum();
    }
}