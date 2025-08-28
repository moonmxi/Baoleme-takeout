/**
 * 用户控制器
 * 处理用户相关的HTTP请求，包括注册、登录、个人信息管理等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.controller;

import jakarta.validation.Valid;
import org.demo.userservice.common.CommonResponse;
import org.demo.userservice.common.ResponseBuilder;
import org.demo.userservice.common.JwtUtils;
import org.demo.userservice.common.UserHolder;

import org.demo.userservice.dto.request.user.*;
import org.demo.userservice.dto.response.user.*;
import org.demo.userservice.pojo.User;
import org.demo.userservice.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.List;

/**
 * 用户控制器类
 * 提供用户注册、登录、信息管理、收藏、优惠券等功能的REST API
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 用户服务接口
     */
    private final UserService userService;

    /**
     * Redis模板，用于缓存和会话管理
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造函数
     * 
     * @param userService 用户服务实例
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口
     * 
     * @param request 用户注册请求对象
     * @return 注册结果响应
     */
    @PostMapping("/register")
    public CommonResponse register(@Valid @RequestBody UserRegisterRequest request) {
        System.out.println("收到注册请求: " + request);
        User user = new User();
        BeanUtils.copyProperties(request, user);
        User result = userService.register(user);

        if (result == null) {
            return ResponseBuilder.fail("注册失败：用户名或手机号已存在");
        }

        UserRegisterResponse response = new UserRegisterResponse();
        response.setUserId(result.getId());
        response.setUsername(result.getUsername());
        response.setPhone(result.getPhone());
        return ResponseBuilder.ok(response);
    }

    /**
     * 用户登录接口
     * 
     * @param request 用户登录请求对象
     * @return 登录结果响应，包含JWT token
     */
    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody UserLoginRequest request) {
        User result = userService.login(request.getPhone(), request.getPassword());
        if (result == null) {
            return ResponseBuilder.fail("手机号或密码错误");
        }
        String loginKey = "user:login:" + result.getId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(loginKey))) {
            return ResponseBuilder.fail("该用户已登录，请先登出");
        }
        String token = JwtUtils.createToken(result.getId(), "user", result.getUsername());
        redisTemplate.opsForValue().set("user:token:" + token, result.getId(), 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(loginKey, token, 1, TimeUnit.DAYS);

        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setUsername(result.getUsername());
        response.setUserId(result.getId());
        return ResponseBuilder.ok(response);
    }

    /**
     * 用户登出接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @return 登出结果响应
     */
    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String tokenKey = "user:token:" + token;

        Object userId = redisTemplate.opsForValue().get(tokenKey);
        if (userId != null) {
            String loginKey = "user:login:" + userId;
            redisTemplate.delete(loginKey);
        }

        redisTemplate.delete(tokenKey);

        Long id = UserHolder.getId();
        User user = new User();
        user.setId(id);

        boolean success = userService.updateInfo(user);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("登出失败");
    }

    /**
     * 删除用户账户接口
     * 
     * @return 删除结果响应
     */
    @DeleteMapping("/delete")
    public CommonResponse delete() {
        boolean ok = userService.delete(UserHolder.getId());
        return ok ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }

    /**
     * 获取用户信息接口
     * 
     * @return 用户信息响应
     */
    @GetMapping("/info")
    public CommonResponse getInfo() {
        Long id = UserHolder.getId();
        User user = userService.getInfo(id);
        if (user == null) {
            return ResponseBuilder.fail("用户不存在");
        }

        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        response.setUserId(user.getId());
        return ResponseBuilder.ok(response);
    }

    /**
     * 更新用户信息接口
     * 
     * @param request 用户更新请求对象
     * @param tokenHeader 请求头中的Authorization token
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public CommonResponse update(@Valid @RequestBody UserUpdateRequest request, @RequestHeader("Authorization") String tokenHeader) {
        Long id = UserHolder.getId();

        User before = userService.getInfo(id);
        if (before == null) {
            return ResponseBuilder.fail("用户不存在");
        }

        User user = new User();
        user.setId(id);
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setDescription(request.getDescription());
        user.setLocation(request.getLocation());
        user.setGender(request.getGender());

        boolean success = userService.updateInfo(user);
        if (!success) {
            return ResponseBuilder.fail("更新失败，请检查字段");
        }

        boolean usernameChanged = request.getUsername() != null && !request.getUsername().equals(before.getUsername());
        if (!usernameChanged) {
            return ResponseBuilder.ok();
        }

        String oldToken = tokenHeader.replace("Bearer ", "");
        String oldTokenKey = "user:token:" + oldToken;
        String oldLoginKey = "user:login:" + id;

        redisTemplate.delete(oldTokenKey);
        redisTemplate.delete(oldLoginKey);

        String newToken = JwtUtils.createToken(id, "user", request.getUsername());
        redisTemplate.opsForValue().set("user:token:" + newToken, id, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("user:login:" + id, newToken, 1, TimeUnit.DAYS);

        UserLoginResponse response = new UserLoginResponse();
        response.setToken(newToken);
        response.setUsername(request.getUsername());
        response.setUserId(id);
        return ResponseBuilder.ok(response);
    }

    /**
     * 收藏店铺接口
     * 
     * @param request 收藏请求对象
     * @return 收藏结果响应
     */
    @PostMapping("/favorite")
    public CommonResponse favoriteStore(@Valid @RequestBody UserFavoriteRequest request) {
        Long userId = UserHolder.getId();
        boolean success = userService.favoriteStore(userId, request.getStoreId());
        System.out.println(
                userId+"商店id是"+request.getStoreId()
        );
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("收藏失败");
    }

    /**
     * 获取收藏店铺列表接口
     * 
     * @param request 获取收藏店铺请求对象
     * @return 收藏店铺列表响应
     */
    @PostMapping("/favorite/watch")
    public CommonResponse getFavoriteStores(@Valid@RequestBody UserGetFavoriteStoresRequest request) {
        Long userId = UserHolder.getId();
        String type = request.getType();
        BigDecimal distance = request.getDistance();
        BigDecimal wishPrice = request.getWishPrice();
        BigDecimal startRating = request.getStartRating();
        BigDecimal endRating = request.getEndRating();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        List<UserFavoriteResponse> stores = userService.getFavoriteStores(userId,type, distance,wishPrice,startRating,endRating,page,pageSize);

        return ResponseBuilder.ok(stores);
    }

    /**
     * 删除收藏接口
     * 
     * @param request 删除收藏请求对象
     * @return 删除结果响应
     */
    @PostMapping("/deleteFavorite")
    public CommonResponse deleteFavorite(@Valid @RequestBody UserDeleteFavoriteRequest request) {
        Long userId = UserHolder.getId();
        boolean success = userService.deleteFavorite(userId, request.getStoreId());
        System.out.println(
                userId+"商店id是"+request.getStoreId()
        );
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("删除失败");
    }



    /**
     * 搜索店铺和商品接口
     * 
     * @param request 搜索请求对象
     * @return 搜索结果响应
     */
    @PostMapping("/search")
    public CommonResponse searchStoreAndProduct(@Valid @RequestBody UserSearchRequest request) {
        String keyword = request.getKeyword();
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseBuilder.fail("关键词不能为空");
        }
        BigDecimal  distance = request.getDistance();
        BigDecimal wishPrice = request.getWishPrice();
        BigDecimal startRating = request.getStartRating();
        BigDecimal endRating = request.getEndRating();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();

        System.out.println(
                "关键词是"+keyword+"距离是"+distance+"价格是"+wishPrice+"开始是"+startRating+"结束是"+endRating+"页数是"+page+"页大小是"+pageSize
        );

        List<UserSearchResponse> stores = userService.searchStores(keyword.trim(),distance,wishPrice,startRating,endRating,page,pageSize);
        return ResponseBuilder.ok(Map.of("results", stores));
    }

    /**
     * 提交评价接口
     * 
     * @param request 评价请求对象
     * @return 评价结果响应
     */
    @PostMapping("/review")
    public CommonResponse submitReview(@Valid @RequestBody UserReviewRequest request) {
        Long userId = UserHolder.getId();
        UserReviewResponse response = userService.submitReview(userId, request);
        return ResponseBuilder.ok(response);
    }

    /**
     * 注销账户接口
     * 
     * @return 注销结果响应
     */
    @DeleteMapping("/cancel")
    public CommonResponse cancelAccount() {
        Long userId = UserHolder.getId();
        boolean success = userService.cancelAccount(userId);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }

    /**
     * 更新浏览历史接口
     * 
     * @param request 更新浏览历史请求对象
     * @return 更新结果响应
     */
    @PostMapping("/updateViewHistory")
    public CommonResponse updateViewHistory(@Valid @RequestBody UserUpdateViewHistoryRequest request) {
        Long userId = UserHolder.getId();
        Long storeId = request.getStoreId();
        LocalDateTime viewTime = request.getViewTime();
        boolean success = userService.updateViewHistory(userId, storeId,  viewTime);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("添加失败");
    }

    /**
     * 获取浏览历史接口
     * 
     * @param request 获取浏览历史请求对象
     * @return 浏览历史响应
     */
    @PostMapping("/viewHistory")
    public CommonResponse getViewHistory(@Valid @RequestBody UserGetViewHistoryRequest request) {
        Long userId = UserHolder.getId();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        List<UserViewHistoryResponse> response = userService.getViewHistory(userId, page, pageSize);
        return ResponseBuilder.ok(Map.of("stores", response));
    }
}