/**
 * 优惠券服务实现类
 * 实现优惠券相关的业务逻辑
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.dto.response.UserCouponResponse;
import org.demo.gateway.mapper.CouponMapper;
import org.demo.gateway.pojo.Coupon;
import org.demo.gateway.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券服务实现类
 * 提供优惠券管理的具体业务逻辑实现
 */
@Slf4j
@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    /**
     * 创建优惠券
     * 
     * @param coupon 优惠券信息
     * @return Coupon 创建的优惠券
     * @throws Exception 创建失败时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Coupon createCoupon(Coupon coupon) throws Exception {
        log.info("创建优惠券: storeId={}, type={}", coupon.getStoreId(), coupon.getType());
        
        // 设置创建时间
        coupon.setCreatedAt(LocalDateTime.now());
        
        // 设置默认值
        if (coupon.getIsUsed() == null) {
            coupon.setIsUsed(false);
        }
        
        // 插入优惠券数据
        int result = couponMapper.insert(coupon);
        if (result <= 0) {
            log.warn("优惠券创建失败: storeId={}, type={}", coupon.getStoreId(), coupon.getType());
            throw new Exception("优惠券创建失败");
        }
        
        log.info("优惠券创建成功: couponId={}", coupon.getId());
        return coupon;
    }

    /**
     * 获取用户优惠券列表
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return List<UserCouponResponse> 用户优惠券列表
     */
    @Override
    public List<UserCouponResponse> getUserCoupons(Long userId, Long storeId) {
        log.info("获取用户优惠券: userId={}, storeId={}", userId, storeId);
        return couponMapper.selectUserCouponsByUserId(userId, storeId);
    }

    /**
     * 用户领取优惠券
     * 
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return boolean 领取是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean claimCoupon(Long userId, Long couponId) {
        log.info("用户领取优惠券: userId={}, couponId={}", userId, couponId);
        
        // 从数据库中获取指定的优惠券
        Coupon availableCoupon = couponMapper.selectById(couponId);
        if (availableCoupon == null) {
            log.warn("领取失败：优惠券不存在, couponId={}", couponId);
            return false;
        }
        
        // 检查优惠券是否已被领取
        if (availableCoupon.getUserId() != null) {
            log.warn("领取失败：优惠券已被领取, couponId={}", couponId);
            return false;
        }
        
        // 检查优惠券是否已过期
        if (availableCoupon.isExpired()) {
            log.warn("领取失败：优惠券已过期, couponId={}", couponId);
            return false;
        }
        
        // 更新优惠券的用户ID
        int result = couponMapper.updateUserCoupon(couponId, userId);
        if (result <= 0) {
            log.warn("领取失败：更新优惠券用户ID失败, couponId={}, userId={}", couponId, userId);
            return false;
        }
        
        log.info("优惠券领取成功: userId={}, couponId={}", userId, couponId);
        return true;
    }

    /**
     * 根据ID获取优惠券
     * 
     * @param couponId 优惠券ID
     * @return Coupon 优惠券信息
     */
    @Override
    public Coupon getCouponById(Long couponId) {
        log.info("根据ID获取优惠券: couponId={}", couponId);
        return couponMapper.selectById(couponId);
    }

    /**
     * 标记优惠券为已使用
     * 
     * @param couponId 优惠券ID
     * @return boolean 标记是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markCouponAsUsed(Long couponId) {
        log.info("标记优惠券为已使用: couponId={}", couponId);
        
        int result = couponMapper.markAsUsed(couponId);
        if (result > 0) {
            log.info("优惠券标记为已使用成功: couponId={}", couponId);
            return true;
        } else {
            log.warn("优惠券标记为已使用失败: couponId={}", couponId);
            return false;
        }
    }
}