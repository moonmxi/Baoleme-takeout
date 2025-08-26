/**
 * 购物车控制器
 * 处理购物车相关的HTTP请求，包括添加、查看、更新、删除购物车商品等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.controller;

import jakarta.validation.Valid;
import org.demo.gateway.common.CommonResponse;
import org.demo.gateway.common.ResponseBuilder;
import org.demo.gateway.common.UserHolder;
import org.demo.gateway.dto.request.cart.*;
import org.demo.gateway.dto.response.cart.*;
import org.demo.gateway.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车控制器类
 * 提供购物车管理相关功能的REST API
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    /**
     * 购物车服务接口
     */
    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车接口
     * 
     * @param request 添加到购物车请求对象
     * @return 添加结果响应
     */
    @PostMapping("/add")
    public CommonResponse addToCart(@Valid @RequestBody AddToCartRequest request) {
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

        Long userId = UserHolder.getId();
        boolean success = cartService.addToCart(userId, request);
        
        if (!success) {
            return ResponseBuilder.fail("添加到购物车失败，商品可能不存在或库存不足");
        }
        
        return ResponseBuilder.ok("商品已添加到购物车");
    }

    /**
     * 查看购物车接口
     * 
     * @return 购物车内容响应
     */
    @GetMapping("/view")
    public CommonResponse viewCart() {
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

        Long userId = UserHolder.getId();
        CartViewResponse response = cartService.viewCart(userId);
        
        return ResponseBuilder.ok(response);
    }

    /**
     * 更新购物车商品数量接口
     * 
     * @param request 更新购物车请求对象
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public CommonResponse updateCartItem(@Valid @RequestBody UpdateCartRequest request) {
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

        Long userId = UserHolder.getId();
        boolean success = cartService.updateCart(userId, request);
        
        if (!success) {
            return ResponseBuilder.fail("更新购物车失败，商品可能不存在或数量无效");
        }
        
        return ResponseBuilder.ok("购物车已更新");
    }

    /**
     * 删除购物车商品接口
     * 
     * @param request 删除购物车商品请求对象
     * @return 删除结果响应
     */
    @PutMapping("/delete")
    public CommonResponse deleteCartItem(@Valid @RequestBody DeleteCartRequest request) {
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

        Long userId = UserHolder.getId();
        boolean success = cartService.deleteCartItem(userId, request);
        
        if (!success) {
            return ResponseBuilder.fail("删除购物车商品失败，商品可能不存在");
        }
        
        return ResponseBuilder.ok("商品已从购物车删除");
    }

    /**
     * 清空购物车接口
     * 
     * @return 清空结果响应
     */
    @DeleteMapping("/remove")
    public CommonResponse removeCartItems() {
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

        Long userId = UserHolder.getId();
        boolean success = cartService.removeCart(userId);
        
        if (!success) {
            return ResponseBuilder.fail("清空购物车失败");
        }
        
        return ResponseBuilder.ok("购物车已清空");
    }
}