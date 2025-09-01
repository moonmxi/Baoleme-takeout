/**
 * 用户控制器
 * 处理用户相关的HTTP请求，包括注册、登录、个人信息管理等功能
 * 重构后跨数据库操作直接调用GatewayApiClient
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.userservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.demo.userservice.client.GatewayApiClient;
import org.demo.userservice.common.CommonResponse;
import org.demo.userservice.common.ResponseBuilder;
import org.demo.userservice.common.JwtUtils;
import org.demo.userservice.common.UserHolder;

import org.demo.userservice.dto.request.user.*;
import org.demo.userservice.dto.response.user.*;
import org.demo.userservice.pojo.Store;
import org.demo.userservice.pojo.User;
import org.demo.userservice.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户控制器类
 * 提供用户注册、登录、信息管理、收藏、优惠券等功能的REST API
 */
@Slf4j
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
     * 网关API客户端，用于跨数据库操作
     */
    @Autowired
    private GatewayApiClient gatewayApiClient;

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
     * 通过网关API调用获取店铺详细信息
     * 
     * @param request 获取收藏店铺请求对象
     * @return 收藏店铺列表响应
     */
    @PostMapping("/favorite/watch")
    public CommonResponse getFavoriteStores(@Valid@RequestBody UserGetFavoriteStoresRequest request, @RequestHeader("Authorization") String tokenHeader) {
        Long userId = UserHolder.getId();
        try {
            String token = tokenHeader.replace("Bearer ", "");
            BigDecimal distance = request.getDistance();
            BigDecimal wishPrice = request.getWishPrice();
            BigDecimal startRating = request.getStartRating();
            BigDecimal endRating = request.getEndRating();
            Integer page = request.getPage();
            Integer pageSize = request.getPageSize();
            log.info("用户获取收藏店铺列表: userId={}, page={}, pageSize={}", userId, page, pageSize);
            
            // 通过网关API获取用户收藏店铺列表
            List<Map<String, Object>> favoriteStores = gatewayApiClient.getUserFavoriteStores(userId, page, pageSize, token);
            
            List<UserFavoriteResponse> responses = favoriteStores.stream().map(favorite -> {
                UserFavoriteResponse resp = new UserFavoriteResponse();
                
                // 设置店铺ID
                Object storeIdObj = favorite.get("id");
                if (storeIdObj != null) {
                    resp.setStoreId(((Number) storeIdObj).longValue());
                }
                
                // 设置店铺名称 - 修正字段名
                resp.setName((String) favorite.get("name"));
                
                // 设置店铺类型 - 修正字段名
                resp.setType((String) favorite.get("type"));
                
                // 设置店铺描述 - 修正字段名
                resp.setDescription((String) favorite.get("description"));
                
                // 设置店铺位置 - 修正字段名
                resp.setLocation((String) favorite.get("location"));
                
                // 设置评分
                Object ratingObj = favorite.get("rating");
                if (ratingObj != null) {
                    if (ratingObj instanceof BigDecimal) {
                        resp.setRating((BigDecimal) ratingObj);
                    } else if (ratingObj instanceof Number) {
                        resp.setRating(BigDecimal.valueOf(((Number) ratingObj).doubleValue()));
                    }
                }
                
                // 设置店铺状态
                Object statusObj = favorite.get("status");
                if (statusObj != null) {
                    resp.setStatus(((Number) statusObj).intValue());
                }
                
                // 设置店铺图片 - 修正字段名
                resp.setImage((String) favorite.get("image"));
                
                // 设置收藏时间
                Object createdAtObj = favorite.get("created_at");
                if (createdAtObj != null) {
                    if (createdAtObj instanceof LocalDateTime) {
                        resp.setCreatedAt((LocalDateTime) createdAtObj);
                    } else {
                        resp.setCreatedAt(LocalDateTime.parse(createdAtObj.toString()));
                    }
                }
                
                return resp;
            }).collect(Collectors.toList());
            
            log.info("成功获取用户收藏店铺列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("favorites", responses));
            
        } catch (Exception e) {
            log.error("获取收藏店铺失败", e);
            return ResponseBuilder.fail("获取收藏店铺失败: " + e.getMessage());
        }
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
     * 通过网关API调用搜索服务
     * 
     * @param request 搜索请求对象
     * @return 搜索结果响应
     */
    @PostMapping("/search")
    public CommonResponse searchStoreAndProduct(@Valid @RequestBody UserSearchRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String keyword = request.getKeyword();
        BigDecimal distance = request.getDistance();
        BigDecimal wishPrice = request.getWishPrice();
        BigDecimal startRating = request.getStartRating();
        BigDecimal endRating = request.getEndRating();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        
        log.info("用户搜索店铺和商品: keyword={}, distance={}, wishPrice={}, startRating={}, endRating={}, page={}, pageSize={}", 
                keyword, distance, wishPrice, startRating, endRating, page, pageSize);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseBuilder.fail("关键词不能为空");
        }
        
        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            // 调用网关API进行搜索，传递所有搜索参数
            List<Map<String, Object>> searchResults = gatewayApiClient.searchStoreWithFilters(
                    keyword.trim(),
                    distance,
                    wishPrice,
                    startRating,
                    endRating,
                    page,
                    pageSize,
                    token
            );

            // 处理搜索结果
            List<UserSearchResponse> responses = new ArrayList<>();

            if (searchResults != null && !searchResults.isEmpty()) {
                responses = searchResults.stream().map(store -> {
                     UserSearchResponse response = new UserSearchResponse();
                     response.setStoreId(((Number) store.get("id")).longValue());
                     response.setName((String) store.get("name"));
                     response.setType((String) store.get("type"));
                     response.setDescription((String) store.get("description"));
                     response.setLocation((String) store.get("location"));
                    // 修复rating字段类型转换
                    Object ratingObj = store.get("rating");
                    if (ratingObj != null) {
                        if (ratingObj instanceof BigDecimal) {
                            response.setRating((BigDecimal) ratingObj);
                        } else if (ratingObj instanceof Double) {
                            response.setRating(BigDecimal.valueOf((Double) ratingObj));
                        } else if (ratingObj instanceof Number) {
                            response.setRating(BigDecimal.valueOf(((Number) ratingObj).doubleValue()));
                        }
                    }
                     response.setStatus((Integer) store.get("status"));
                     response.setImage((String) store.get("image"));
                    // 修复created_at字段类型转换
                    Object createdAtObj = store.get("created_at");
                    if (createdAtObj != null) {
                        if (createdAtObj instanceof LocalDateTime) {
                            response.setCreatedAt((LocalDateTime) createdAtObj);
                        } else if (createdAtObj instanceof String) {
                            try {
                                response.setCreatedAt(LocalDateTime.parse((String) createdAtObj));
                            } catch (Exception e) {
                                try {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    response.setCreatedAt(LocalDateTime.parse((String) createdAtObj, formatter));
                                } catch (Exception ex) {
                                    log.warn("无法解析日期字符串: {}", createdAtObj, ex);
                                }
                            }
                        }
                    }
                     return response;
                 }).collect(Collectors.toList());
            }
            log.info("成功获取搜索店铺列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("results", responses));
        } catch (Exception e) {
            log.error("搜索失败", e);
            return ResponseBuilder.fail("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 提交评价接口
     * 通过网关API调用评价服务
     * 
     * @param request 评价请求对象
     * @return 评价结果响应
     */
    @PostMapping("/review")
    public CommonResponse submitReview(@Valid @RequestBody UserReviewRequest request, @RequestHeader("Authorization") String tokenHeader) {
        Long userId = UserHolder.getId();
        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            // 构建评价数据
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("userId", userId);
            reviewData.put("storeId", request.getStoreId());
            reviewData.put("productId", request.getProductId());
            reviewData.put("rating", request.getRating());
            reviewData.put("comment", request.getComment());
            reviewData.put("images", request.getImages());
            
            // 通过网关API提交评价
            boolean success = gatewayApiClient.submitReview(reviewData, token);
            
            if (!success) {
                return ResponseBuilder.fail("评价提交失败");
            }
            
            UserReviewResponse response = new UserReviewResponse();
            response.setComment(request.getComment());
            response.setRating(request.getRating());
            response.setImages(request.getImages() != null ? request.getImages() : List.of());
            
            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            return ResponseBuilder.fail("评价提交失败: " + e.getMessage());
        }
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


    @PostMapping("/updateViewHistory")
    public CommonResponse updateViewHistory(@Valid @RequestBody UserUpdateViewHistoryRequest request) {
        Long userId = UserHolder.getId();
        Long storeId = request.getStoreId();
        LocalDateTime viewTime = request.getViewTime();
        boolean success = userService.updateViewHistory(userId, storeId,  viewTime);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("添加失败");
    }
    @PostMapping("/viewHistory")
    public CommonResponse getViewHistory(@Valid @RequestBody UserGetViewHistoryRequest request, @RequestHeader("Authorization") String tokenHeader) {
        Long userId = UserHolder.getId();
        try {
            String token = tokenHeader.replace("Bearer ", "");

            Integer page = request.getPage();
            Integer pageSize = request.getPageSize();
            log.info("用户获取浏览店铺历史列表: userId={}, page={}, pageSize={}", userId, page, pageSize);

            // 通过网关API获取用户收藏店铺列表
            List<Map<String, Object>> favoriteStores = gatewayApiClient.getUserViewHistory(userId, page, pageSize, token);

            List<UserFavoriteResponse> responses = favoriteStores.stream().map(favorite -> {
                UserFavoriteResponse resp = new UserFavoriteResponse();

                // 设置店铺ID
                Object storeIdObj = favorite.get("id");
                if (storeIdObj != null) {
                    resp.setStoreId(((Number) storeIdObj).longValue());
                }

                // 设置店铺名称 - 修正字段名
                resp.setName((String) favorite.get("name"));

                // 设置店铺类型 - 修正字段名
                resp.setType((String) favorite.get("type"));

                // 设置店铺描述 - 修正字段名
                resp.setDescription((String) favorite.get("description"));

                // 设置店铺位置 - 修正字段名
                resp.setLocation((String) favorite.get("location"));

                // 设置评分
                Object ratingObj = favorite.get("rating");
                if (ratingObj != null) {
                    if (ratingObj instanceof BigDecimal) {
                        resp.setRating((BigDecimal) ratingObj);
                    } else if (ratingObj instanceof Number) {
                        resp.setRating(BigDecimal.valueOf(((Number) ratingObj).doubleValue()));
                    }
                }

                // 设置店铺状态
                Object statusObj = favorite.get("status");
                if (statusObj != null) {
                    resp.setStatus(((Number) statusObj).intValue());
                }

                // 设置店铺图片 - 修正字段名
                resp.setImage((String) favorite.get("image"));

                // 设置收藏时间
                Object createdAtObj = favorite.get("created_at");
                if (createdAtObj != null) {
                    if (createdAtObj instanceof LocalDateTime) {
                        resp.setCreatedAt((LocalDateTime) createdAtObj);
                    } else {
                        resp.setCreatedAt(LocalDateTime.parse(createdAtObj.toString()));
                    }
                }

                return resp;
            }).collect(Collectors.toList());

            log.info("成功获取用户收藏店铺列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("favorites", responses));

        } catch (Exception e) {
            log.error("获取收藏店铺失败", e);
            return ResponseBuilder.fail("获取收藏店铺失败: " + e.getMessage());
        }
    }

    @PostMapping("/user-view-stores")
    public CommonResponse viewStores(@Valid @RequestBody UserGetFavoriteStoresRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String type = request.getType();
        BigDecimal distance = request.getDistance();
        BigDecimal wishPrice = request.getWishPrice();
        BigDecimal startRating = request.getStartRating();
        BigDecimal endRating = request.getEndRating();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();

        log.info("用户浏览店铺: type={}, distance={}, wishPrice={}, startRating={}, endRating={}, page={}, pageSize={}",
                type, distance, wishPrice, startRating, endRating, page, pageSize);

        if (type == null || type.trim().isEmpty()) {
            return ResponseBuilder.fail("关键词不能为空");
        }

        try {
            String token = tokenHeader.replace("Bearer ", "");

            // 调用网关API进行搜索，传递所有搜索参数
            List<Map<String, Object>> searchResults = gatewayApiClient.viewStores(
                    type.trim(),
                    distance,
                    wishPrice,
                    startRating,
                    endRating,
                    page,
                    pageSize,
                    token
            );

            // 处理搜索结果
            List<UserSearchResponse> responses = new ArrayList<>();

            if (searchResults != null && !searchResults.isEmpty()) {
                responses = searchResults.stream().map(store -> {
                    UserSearchResponse response = new UserSearchResponse();
                    response.setStoreId(((Number) store.get("id")).longValue());
                    response.setName((String) store.get("name"));
                    response.setType((String) store.get("type"));
                    response.setDescription((String) store.get("description"));
                    response.setLocation((String) store.get("location"));
                    // 修复rating字段类型转换
                    Object ratingObj = store.get("rating");
                    if (ratingObj != null) {
                        if (ratingObj instanceof BigDecimal) {
                            response.setRating((BigDecimal) ratingObj);
                        } else if (ratingObj instanceof Double) {
                            response.setRating(BigDecimal.valueOf((Double) ratingObj));
                        } else if (ratingObj instanceof Number) {
                            response.setRating(BigDecimal.valueOf(((Number) ratingObj).doubleValue()));
                        }
                    }
                    response.setStatus((Integer) store.get("status"));
                    response.setImage((String) store.get("image"));
                    // 修复created_at字段类型转换
                    Object createdAtObj = store.get("created_at");
                    if (createdAtObj != null) {
                        if (createdAtObj instanceof LocalDateTime) {
                            response.setCreatedAt((LocalDateTime) createdAtObj);
                        } else if (createdAtObj instanceof String) {
                            try {
                                response.setCreatedAt(LocalDateTime.parse((String) createdAtObj));
                            } catch (Exception e) {
                                try {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    response.setCreatedAt(LocalDateTime.parse((String) createdAtObj, formatter));
                                } catch (Exception ex) {
                                    log.warn("无法解析日期字符串: {}", createdAtObj, ex);
                                }
                            }
                        }
                    }
                    return response;
                }).collect(Collectors.toList());
            }
            log.info("成功获取浏览店铺列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("results", responses));
        } catch (Exception e) {
            log.error("搜索失败", e);
            return ResponseBuilder.fail("搜索失败: " + e.getMessage());
        }
    }

    @PostMapping("/user-view-products")
    public CommonResponse viewProducts(@Valid @RequestBody UserGetProductByConditionRequest request, @RequestHeader("Authorization") String tokenHeader){
        Long storeId = request.getStoreId();
        String category = request.getCategory();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        log.info("用户浏览商品: storeId={}, category={}, page={}, pageSize={}", storeId, category, page, pageSize);
        try {
            List<Map<String, Object>> searchResults = gatewayApiClient.getProductList(storeId, page, pageSize, category, tokenHeader);

            // 添加空值检查
            if (searchResults == null) {
                searchResults = new ArrayList<>();
            }

            List<UserGetProductResponse> responses = searchResults.stream().map(product -> {
                UserGetProductResponse response = new UserGetProductResponse();
                response.setId(((Number) product.get("id")).longValue());
                response.setStoreId(((Number) product.get("store_id")).longValue());
                response.setName((String) product.get("name"));
                response.setCategory((String) product.get("category"));
                response.setPrice((BigDecimal) product.get("price"));
                response.setDescription((String) product.get("description"));
                response.setImage((String) product.get("image"));
                response.setStock(((Number) product.get("stock")).intValue());
                response.setRating((BigDecimal) product.get("rating"));
                response.setStatus((Integer) product.get("status"));

                // 添加空值检查
                Object createdAtObj = product.get("created_at");
                if (createdAtObj instanceof LocalDateTime) {
                    response.setCreatedAt((LocalDateTime) createdAtObj);
                }

                return response;
            }).collect(Collectors.toList()); // 修复：添加缺失的collect调用

            return ResponseBuilder.ok(Map.of("products", responses));

        } catch (Exception e) {
            log.error("获取用户浏览店铺下的商品列表失败", e);
            return ResponseBuilder.fail("获取用户浏览店铺下的商品列表失败");
        }
    }

}

