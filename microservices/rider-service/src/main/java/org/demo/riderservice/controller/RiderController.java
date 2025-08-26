/**
 * 骑手控制器
 * 处理骑手相关的HTTP请求，包括注册、登录、信息管理、接单模式切换等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.riderservice.controller;

import jakarta.validation.Valid;
import org.demo.riderservice.common.CommonResponse;
import org.demo.riderservice.common.ResponseBuilder;
import org.demo.riderservice.common.JwtUtils;
import org.demo.riderservice.common.UserHolder;
import org.demo.riderservice.dto.request.rider.*;
import org.demo.riderservice.dto.response.rider.*;
import org.demo.riderservice.pojo.Rider;
import org.demo.riderservice.service.RiderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 骑手控制器类
 * 提供骑手注册、登录、信息管理、接单模式管理等功能的REST API
 */
@RestController
@RequestMapping("/rider")
public class RiderController {

    /**
     * 骑手服务接口
     */
    @Autowired
    private RiderService riderService;

    /**
     * Redis模板，用于缓存和会话管理
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造函数
     * 
     * @param riderService 骑手服务实例
     */
    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }

    /**
     * 骑手注册接口
     * 
     * @param request 骑手注册请求对象
     * @return 注册结果响应
     */
    @PostMapping("/register")
    public CommonResponse register(@Valid @RequestBody RiderRegisterRequest request) {
        System.out.println("收到注册请求: " + request);
        Rider rider = new Rider();
        BeanUtils.copyProperties(request, rider);
        Rider result = riderService.register(rider);
        if (result == null) {
            return ResponseBuilder.fail("注册失败：用户名或手机号重复");
        }

        RiderRegisterResponse response = new RiderRegisterResponse();
        response.setUserId(result.getId());
        response.setUsername(result.getUsername());
        response.setPhone(result.getPhone());
        return ResponseBuilder.ok(response);
    }

    /**
     * 骑手登录接口
     * 
     * @param request 骑手登录请求对象
     * @return 登录结果响应，包含JWT token
     */
    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody RiderLoginRequest request) {
        Rider result = riderService.login(request.getPhone(), request.getPassword());
        if (result == null) {
            return ResponseBuilder.fail("手机号或密码错误");
        }

        String loginKey = "rider:login:" + result.getId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(loginKey))) {
            return ResponseBuilder.fail("该骑手已登录，请先登出");
        }

        String token = JwtUtils.createToken(result.getId(), "rider", result.getUsername());
        redisTemplate.opsForValue().set("rider:token:" + token, result.getId(), 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(loginKey, token, 1, TimeUnit.DAYS);

        RiderLoginResponse response = new RiderLoginResponse();
        response.setToken(token);
        response.setUsername(result.getUsername());
        response.setUserId(result.getId());
        return ResponseBuilder.ok(response);
    }

    /**
     * 获取骑手信息接口
     * 
     * @return 骑手信息响应
     */
    @GetMapping("/info")
    public CommonResponse getInfo() {
        Long id = UserHolder.getId();
        Rider rider = riderService.getInfo(id);
        if (rider == null) {
            return ResponseBuilder.fail("当前身份无效或用户不存在");
        }

        RiderInfoResponse response = new RiderInfoResponse();
        BeanUtils.copyProperties(rider, response);
        response.setUserId(rider.getId());
        return ResponseBuilder.ok(response);
    }

    /**
     * 更新骑手信息接口
     * 
     * @param request 骑手更新请求对象
     * @param tokenHeader 请求头中的Authorization token
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public CommonResponse update(@Valid @RequestBody RiderUpdateRequest request,
                                 @RequestHeader("Authorization") String tokenHeader) {
        Long id = UserHolder.getId();

        Rider before = riderService.getInfo(id);
        if (before == null) {
            return ResponseBuilder.fail("用户不存在");
        }

        Rider rider = new Rider();
        rider.setId(id);
        BeanUtils.copyProperties(request, rider);

        boolean success = riderService.updateInfo(rider);
        if (!success) {
            return ResponseBuilder.fail("更新失败，请检查字段");
        }

        boolean usernameChanged = request.getUsername() != null && !request.getUsername().equals(before.getUsername());
        if (!usernameChanged) {
            return ResponseBuilder.ok();
        }

        String oldToken = tokenHeader.replace("Bearer ", "");
        String oldTokenKey = "rider:token:" + oldToken;
        String oldLoginKey = "rider:login:" + id;

        redisTemplate.delete(oldTokenKey);
        redisTemplate.delete(oldLoginKey);

        String newToken = JwtUtils.createToken(id, "rider", request.getUsername());
        redisTemplate.opsForValue().set("rider:token:" + newToken, id, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("rider:login:" + id, newToken, 1, TimeUnit.DAYS);

        RiderLoginResponse response = new RiderLoginResponse();
        response.setToken(newToken);
        response.setUsername(request.getUsername());
        response.setUserId(id);
        return ResponseBuilder.ok(response);
    }

    /**
     * 切换接单模式接口
     * 
     * @param request 接单模式切换请求对象
     * @return 切换结果响应
     */
    @PatchMapping("/dispatch-mode")
    public CommonResponse switchDispatchMode(@Valid @RequestBody RiderDispatchModeRequest request) {
        Rider rider = new Rider();
        rider.setId(UserHolder.getId());
        rider.setDispatchMode(request.getDispatchMode());

        boolean ok = riderService.updateInfo(rider);
        if (!ok) return ResponseBuilder.fail("切换接单模式失败");

        RiderDispatchModeResponse resp = new RiderDispatchModeResponse();
        resp.setCurrentMode(request.getDispatchMode());
        resp.setModeChangedAt(new Date().toString());
        return ResponseBuilder.ok(resp);
    }

    /**
     * 骑手登出接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @return 登出结果响应
     */
    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String tokenKey = "rider:token:" + token;

        Object riderId = redisTemplate.opsForValue().get(tokenKey);
        if (riderId != null) {
            String loginKey = "rider:login:" + riderId;
            redisTemplate.delete(loginKey);
        }

        redisTemplate.delete(tokenKey);

        Long id = UserHolder.getId();
        Rider rider = new Rider();
        rider.setId(id);
        rider.setOrderStatus(-1);

        boolean success = riderService.updateInfo(rider);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("登出失败");
    }

    /**
     * 删除骑手账户接口
     * 
     * @return 删除结果响应
     */
    @DeleteMapping("/delete")
    public CommonResponse delete() {
        boolean ok = riderService.delete(UserHolder.getId());
        return ok ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }

    /**
     * 自动接单接口
     * 
     * @return 接单结果响应
     */
    @PostMapping("/auto-order-taking")
    public CommonResponse autoOrderTaking() {
        Long id = UserHolder.getId();
        if(riderService.getInfo(id).getDispatchMode() == 0)
            return ResponseBuilder.ok("当前骑手不自动接单");

        return riderService.randomSendOrder(id) ? ResponseBuilder.ok() : ResponseBuilder.fail("目前无空闲订单");
    }
}