/**
 * 优惠券控制器
 * 处理优惠券相关的HTTP请求，包括创建优惠券、查看优惠券、领取优惠券等功能
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.common.CommonResponse;
import org.demo.gateway.common.ResponseBuilder;
import org.demo.gateway.common.UserHolder;
import org.demo.gateway.dto.request.AvailableCouponRequest;
import org.demo.gateway.dto.request.CouponCreateRequest;
import org.demo.gateway.dto.request.UserClaimCouponRequest;
import org.demo.gateway.dto.request.UserViewCouponRequest;
import org.demo.gateway.dto.response.CouponCreateResponse;
import org.demo.gateway.dto.response.UserCouponResponse;
import org.demo.gateway.pojo.Coupon;
import org.demo.gateway.service.CouponService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券控制器类
 * 提供优惠券管理的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 创建优惠券
     * 
     * @param request 优惠券创建请求
     * @return CommonResponse<CouponCreateResponse> 创建结果
     */
    @PostMapping("/create")
    public CommonResponse createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        try {
            log.info("收到创建优惠券请求: {}", request);

            // 从上下文获取商户ID（如果需要权限验证）
            Long merchantId = UserHolder.getId();
            String role = UserHolder.getRole();
            
            // 检查权限（只有商户可以创建优惠券）
            if (!"merchant".equals(role)) {
                return ResponseBuilder.fail("无权限创建优惠券，仅商户可操作");
            }

            // 初始化实体对象
            Coupon coupon = new Coupon();

            // 复制请求参数到领域模型
            BeanUtils.copyProperties(request, coupon);
            coupon.setUserId(null); // 创建时不分配给特定用户

            // 调用服务层创建优惠券
            Coupon createdCoupon = couponService.createCoupon(coupon);

            // 构建响应数据
            CouponCreateResponse response = new CouponCreateResponse();
            response.setCouponId(createdCoupon.getId());

            log.info("优惠券创建成功，响应: {}", response);
            return ResponseBuilder.ok("优惠券创建成功", response);
            
        } catch (Exception e) {
            log.error("优惠券创建失败", e);
            return ResponseBuilder.fail("优惠券创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户优惠券列表
     * 
     * @param request 用户查看优惠券请求
     * @return CommonResponse<List<UserCouponResponse>> 用户优惠券列表
     */
    @PostMapping("/user")
    public CommonResponse getUserCoupons(@Valid @RequestBody UserViewCouponRequest request) {
        try {
            String role = UserHolder.getRole();
            if (!"user".equals(role)) {
                return ResponseBuilder.fail("无权限访问，仅用户可操作");
            }
            
            Long userId = UserHolder.getId();
            Long storeId = request.getStoreId();
            
            List<UserCouponResponse> coupons = couponService.getUserCoupons(userId, storeId);
            return ResponseBuilder.ok(coupons);
            
        } catch (Exception e) {
            log.error("获取用户优惠券失败", e);
            return ResponseBuilder.fail("获取用户优惠券失败: " + e.getMessage());
        }
    }

    /**
     * 查看可用优惠券列表
     * 
     * @param request 可用优惠券查询请求
     * @return CommonResponse<List<UserCouponResponse>> 可用优惠券列表
     */
    @PostMapping("/available")
    public CommonResponse getAvailableCoupons(@Valid @RequestBody AvailableCouponRequest request) {
        try {
            Long storeId = request.getStoreId();
            List<UserCouponResponse> coupons = couponService.getUserCoupons(0L, storeId);
            return ResponseBuilder.ok(coupons);
            
        } catch (Exception e) {
            log.error("获取可用优惠券失败", e);
            return ResponseBuilder.fail("获取可用优惠券失败: " + e.getMessage());
        }
    }

    /**
     * 用户领取优惠券
     * 
     * @param request 用户领取优惠券请求
     * @return CommonResponse 领取结果
     */
    @PostMapping("/claim")
    public CommonResponse claimCoupon(@Valid @RequestBody UserClaimCouponRequest request) {
        try {
            String role = UserHolder.getRole();
            if (!"user".equals(role)) {
                return ResponseBuilder.fail("无权限访问，仅用户可操作");
            }
            
            Long userId = UserHolder.getId();
            boolean success = couponService.claimCoupon(userId, request.getId());
            
            return success ? ResponseBuilder.ok("优惠券领取成功") : ResponseBuilder.fail("优惠券领取失败");
            
        } catch (Exception e) {
            log.error("领取优惠券失败", e);
            return ResponseBuilder.fail("领取优惠券失败: " + e.getMessage());
        }
    }
}