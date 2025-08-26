/**
 * 商家控制器
 * 处理商家相关的HTTP请求，包括注册、登录、信息管理等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.controller;

import jakarta.validation.Valid;
import org.demo.merchantservice.common.CommonResponse;
import org.demo.merchantservice.common.ResponseBuilder;
import org.demo.merchantservice.common.JwtUtils;
import org.demo.merchantservice.common.UserHolder;
import org.demo.merchantservice.dto.request.merchant.*;
import org.demo.merchantservice.dto.response.merchant.*;
import org.demo.merchantservice.pojo.Merchant;
import org.demo.merchantservice.service.MerchantService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 商家控制器类
 * 提供商家注册、登录、信息管理等功能的REST API
 */
@RestController
@RequestMapping("/merchant")
public class MerchantController {

    /**
     * 商家服务接口
     */
    private final MerchantService merchantService;
    
    /**
     * 密码加密器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Redis模板，用于缓存和会话管理
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造函数
     * 
     * @param merchantService 商家服务实例
     */
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /**
     * 商家注册接口
     * 
     * @param request 商家注册请求对象
     * @return 注册结果响应
     */
    @PostMapping("/register")
    public CommonResponse register(@RequestBody @Valid MerchantRegisterRequest request) {
        System.out.println("收到注册请求: " + request);

        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(request, merchant);

        Merchant result = merchantService.createMerchant(merchant);

        if (result == null) {
            return ResponseBuilder.fail("注册失败：用户名或手机号重复");
        }

        MerchantRegisterResponse response = new MerchantRegisterResponse();
        response.setUserId(result.getId());
        response.setUsername(result.getUsername());
        response.setPhone(result.getPhone());

        System.out.println("注册成功，响应: " + ResponseBuilder.ok(response));
        return ResponseBuilder.ok(response);
    }

    /**
     * 商家登录接口
     * 
     * @param request 商家登录请求对象
     * @return 登录结果响应，包含JWT token
     */
    @PostMapping("/login")
    public CommonResponse login(@RequestBody @Valid MerchantLoginRequest request) {
        System.out.println("收到登录请求: " + request);

        Merchant result = merchantService.getMerchantByPhone(request.getPhone());

        if (result == null) {
            return ResponseBuilder.fail("手机号不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), result.getPassword())) {
            return ResponseBuilder.fail("密码错误");
        }

        String loginKey = "merchant:login:" + result.getId();
        if (redisTemplate.hasKey(loginKey)) {
            return ResponseBuilder.fail("该商家已登录，请先登出");
        }

        String token = JwtUtils.createToken(result.getId(), "merchant", result.getUsername());
        redisTemplate.opsForValue().set("merchant:token:" + token, result.getId(), 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(loginKey, token, 1, TimeUnit.DAYS);

        MerchantLoginResponse response = new MerchantLoginResponse();
        response.setToken(token);
        response.setUserId(result.getId());

        System.out.println("登录成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 获取商家信息接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @return 商家信息响应
     */
    @GetMapping("/info")
    public CommonResponse getInfo(@RequestHeader("Authorization") String tokenHeader) {
        System.out.println("收到获取信息请求");

        Long id = UserHolder.getId();
        Merchant merchant = merchantService.getMerchantById(id);

        if (merchant == null) {
            return ResponseBuilder.fail("当前身份无效或用户不存在");
        }

        MerchantInfoResponse response = new MerchantInfoResponse();
        BeanUtils.copyProperties(merchant, response);
        response.setUserId(merchant.getId());

        System.out.println("获取信息成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 更新商家信息接口
     * 
     * @param request 商家更新请求对象
     * @param tokenHeader 请求头中的Authorization token
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public CommonResponse update(@RequestBody MerchantUpdateRequest request,
                                 @RequestHeader("Authorization") String tokenHeader) {
        System.out.println("收到更新请求: " + request);

        Merchant newMerchant = new Merchant();
        Long id = UserHolder.getId();
        newMerchant.setId(id);

        String oldMerchantName = merchantService.getMerchantById(id).getUsername();
        BeanUtils.copyProperties(request, newMerchant);

        Merchant afterUpdate = merchantService.updateInfo(newMerchant);

        if (afterUpdate == null) {
            return ResponseBuilder.fail("更新失败，请检查字段");
        }

        MerchantUpdateResponse response = new MerchantUpdateResponse();
        response.setUsername(request.getUsername());
        response.setUserId(id);
        response.setPhone(afterUpdate.getPhone());
        response.setAvatar(afterUpdate.getAvatar());

        boolean usernameChanged = request.getUsername() != null
                && !oldMerchantName.equals(afterUpdate.getUsername());
        if (!usernameChanged) {
            return ResponseBuilder.ok(response);
        }

        String oldToken = tokenHeader.replace("Bearer ", "");
        redisTemplate.delete("merchant:token:" + oldToken);
        redisTemplate.delete("merchant:login:" + id);

        String newToken = JwtUtils.createToken(id, "merchant", request.getUsername());
        redisTemplate.opsForValue().set("merchant:token:" + newToken, id, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("merchant:login:" + id, newToken, 1, TimeUnit.DAYS);

        response.setNewToken(newToken);

        System.out.println("更新成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 商家登出接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @return 登出结果响应
     */
    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        System.out.println("收到登出请求，Token: " + tokenHeader);
        removeToken(tokenHeader);
        return ResponseBuilder.ok();
    }

    /**
     * 删除商家账户接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @return 删除结果响应
     */
    @DeleteMapping("/delete")
    public CommonResponse delete(@RequestHeader("Authorization") String tokenHeader) {
        System.out.println("收到删除请求");

        Long id = UserHolder.getId();
        boolean ok = merchantService.deleteMerchant(id);
        removeToken(tokenHeader);

        return ok ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }

    /**
     * 移除Token的私有方法
     * 
     * @param tokenHeader 请求头中的Authorization token
     */
    private void removeToken(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String tokenKey = "merchant:token:" + token;

        Object merchantId = redisTemplate.opsForValue().get(tokenKey);
        if (merchantId != null) {
            String loginKey = "merchant:login:" + merchantId;
            redisTemplate.delete(loginKey);
        }

        redisTemplate.delete(tokenKey);
    }
}