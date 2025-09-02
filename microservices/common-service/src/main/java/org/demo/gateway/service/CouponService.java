/**
 * 优惠券服务接口
 * 定义优惠券相关的业务逻辑方法
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.service;

import org.demo.gateway.dto.response.UserCouponResponse;
import org.demo.gateway.pojo.Coupon;

import java.util.List;

/**
 * 优惠券服务接口
 * 定义优惠券管理的核心业务方法
 */
public interface CouponService {

    /**
     * 创建优惠券
     * 
     * @param coupon 优惠券信息
     * @return Coupon 创建的优惠券
     * @throws Exception 创建失败时抛出异常
     */
    Coupon createCoupon(Coupon coupon) throws Exception;

    /**
     * 获取用户优惠券列表
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return List<UserCouponResponse> 用户优惠券列表
     */
    List<UserCouponResponse> getUserCoupons(Long userId, Long storeId);

    /**
     * 用户领取优惠券
     * 
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return boolean 领取是否成功
     */
    boolean claimCoupon(Long userId, Long couponId);

    /**
     * 根据ID获取优惠券
     * 
     * @param couponId 优惠券ID
     * @return Coupon 优惠券信息
     */
    Coupon getCouponById(Long couponId);

    /**
     * 标记优惠券为已使用
     * 
     * @param couponId 优惠券ID
     * @return boolean 标记是否成功
     */
    boolean markCouponAsUsed(Long couponId);
}