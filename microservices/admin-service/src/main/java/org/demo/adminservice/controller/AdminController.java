/**
 * 管理员控制器
 * 提供管理员相关的REST API接口，包括登录、登出、用户管理、商家管理、骑手管理等功能
 * 重构后通过网关API调用其他微服务数据，仅保留管理员认证功能的直接数据库访问
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.adminservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.demo.adminservice.client.GatewayApiClient;
import org.demo.adminservice.common.CommonResponse;
import org.demo.adminservice.common.JwtUtils;
import org.demo.adminservice.common.ResponseBuilder;
import org.demo.adminservice.common.UserHolder;
import org.demo.adminservice.dto.request.admin.*;
import org.demo.adminservice.dto.response.admin.*;
import org.demo.adminservice.pojo.Admin;
import org.demo.adminservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 管理员控制器类
 * 处理管理员相关的HTTP请求，包括登录认证、用户管理、商家管理、骑手管理等功能
 * 重构后通过网关API客户端调用其他微服务数据
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    /**
     * Redis模板，用于管理登录状态
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 管理员服务，处理管理员认证逻辑
     */
    @Autowired
    private AdminService adminService;

    /**
     * 网关API客户端，用于调用其他微服务
     */
    @Autowired
    private GatewayApiClient gatewayApiClient;

    /**
     * 构造函数
     * 
     * @param adminService 管理员服务
     * @param redisTemplate Redis模板
     * @param gatewayApiClient 网关API客户端
     */
    @Autowired
    public AdminController(AdminService adminService, RedisTemplate<String, Object> redisTemplate, GatewayApiClient gatewayApiClient) {
        this.adminService = adminService;
        this.redisTemplate = redisTemplate;
        this.gatewayApiClient = gatewayApiClient;
    }

    /**
     * 管理员登录
     * 
     * @param request 登录请求对象，包含管理员ID和密码
     * @return CommonResponse 登录结果，包含管理员信息和token
     */
    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody AdminLoginRequest request) {
        try {
            log.info("管理员登录请求: adminId={}", request.getAdminId());
            
            Admin admin = adminService.login(request.getAdminId(), request.getPassword());
            if (admin == null) {
                log.warn("管理员登录失败: adminId={}", request.getAdminId());
                return ResponseBuilder.fail("账号或密码错误");
            }

            // 构建 Redis 中用于标识登录状态的 key
            String redisLoginKey = "admin:login:" + admin.getId();

            // 如果已经存在登录状态，则拒绝登录
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisLoginKey))) {
                log.warn("管理员重复登录: adminId={}", admin.getId());
                return ResponseBuilder.fail("该账户已登录，请先登出后再登录");
            }

            // 生成 token
            String token = JwtUtils.createToken(admin.getId(), "admin", String.valueOf(request.getAdminId()));

            // 存储token映射（用于JWT认证过滤器验证）
            redisTemplate.opsForValue().set("admin:token:" + token, admin.getId(), 1, TimeUnit.DAYS);
            // 记录登录状态（防止重复登录）
            redisTemplate.opsForValue().set(redisLoginKey, token, 1, TimeUnit.DAYS);

            // 返回响应
            AdminLoginResponse response = new AdminLoginResponse();
            response.setId(admin.getId());
            response.setToken(token);

            log.info("管理员登录成功: adminId={}", admin.getId());
            return ResponseBuilder.ok(response);
            
        } catch (Exception e) {
            log.error("管理员登录异常", e);
            return ResponseBuilder.fail("登录失败: " + e.getMessage());
        }
    }

    /**
     * 管理员登出
     * 
     * @param tokenHeader Authorization头部信息
     * @return CommonResponse 登出结果
     */
    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String token = tokenHeader.replace("Bearer ", "");
            String tokenKey = "admin:token:" + token;
            
            // 从Redis中获取用户ID
            Object userId = redisTemplate.opsForValue().get(tokenKey);
            if (userId != null) {
                String redisLoginKey = "admin:login:" + userId;
                redisTemplate.delete(redisLoginKey);
                log.info("管理员登出成功: adminId={}", userId);
            }
            
            // 删除token映射
            redisTemplate.delete(tokenKey);
            return ResponseBuilder.ok("登出成功");
            
        } catch (Exception e) {
            log.error("管理员登出异常", e);
            return ResponseBuilder.fail("登出失败: " + e.getMessage());
        }
    }

    /**
     * 管理员分页查看用户列表
     * 通过网关API调用用户微服务获取数据
     * 
     * @param request 用户查询请求对象
     * @return CommonResponse 用户列表响应
     */
    @PostMapping("/userlist")
    public CommonResponse getUserList(@Valid @RequestBody AdminUserQueryRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        try {
            String token = tokenHeader.replace("Bearer ", "");
            List<Map<String, Object>> userList = gatewayApiClient.getUserList(
                request.getPage(), request.getPageSize(), request.getKeyword(), 
                request.getGender(), request.getStartId(), request.getEndId(), token);

            List<AdminUserQueryResponse> responses = userList.stream().map(user -> {
                AdminUserQueryResponse resp = new AdminUserQueryResponse();
                resp.setId(((Number) user.get("id")).longValue());
                resp.setUsername((String) user.get("username"));
                resp.setDescription((String) user.get("description"));
                resp.setPhone((String) user.get("phone"));
                resp.setAvatar((String) user.get("avatar"));
                // 使用数据库字段名（下划线格式）
                Object createdAt = user.get("created_at");
                if (createdAt != null) {
                    resp.setCreatedAt(LocalDateTime.parse(createdAt.toString()));
                }
                return resp;
            }).collect(Collectors.toList());

            return ResponseBuilder.ok(Map.of("users", responses));
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ResponseBuilder.fail("获取用户列表失败");
        }
    }

    /**
     * 管理员分页查看骑手列表
     * 通过网关API调用骑手微服务获取数据
     * 
     * @param request 骑手查询请求对象
     * @return CommonResponse 骑手列表响应
     */
    @PostMapping("/riderlist")
    public CommonResponse getRiderList(@Valid @RequestBody AdminRiderQueryRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        try {
            String token = tokenHeader.replace("Bearer ", "");
            int page = request.getPage();
            int pageSize = request.getPageSize();
            String keyword = request.getKeyword();
            Long startId = request.getStartId();
            Long endId = request.getEndId();
            Integer status = request.getStatus();
            Integer dispatchMode = request.getDispatchMode();
            Long startBalance = request.getStartBalance();
            Long endBalance = request.getEndBalance();
            
            log.info("管理员查询骑手列表: page={}, pageSize={}, keyword={}", page, pageSize, keyword);
            
            List<Map<String, Object>> riderList = gatewayApiClient.getRiderList(page, pageSize, keyword, startId, endId, status, dispatchMode, startBalance, endBalance, token);

            List<AdminRiderQueryResponse> responses = riderList.stream().map(rider -> {
                AdminRiderQueryResponse resp = new AdminRiderQueryResponse();
                resp.setId(((Number) rider.get("id")).longValue());
                resp.setUsername((String) rider.get("username"));
                resp.setPhone((String) rider.get("phone"));
                // 使用数据库字段名（下划线格式）
                Object statusObj = rider.get("order_status");
                if (statusObj != null) resp.setStatus(((Number) statusObj).intValue());
                Object dispatchModeObj = rider.get("dispatch_mode");
                if (dispatchModeObj != null) resp.setDispatchMode(((Number) dispatchModeObj).intValue());
                Object balanceObj = rider.get("balance");
                if (balanceObj != null) resp.setBalance(((Number) balanceObj).longValue());
                resp.setAvatar((String) rider.get("avatar"));
                Object createdAt = rider.get("created_at");
                if (createdAt != null) {
                    resp.setCreatedAt(LocalDateTime.parse(createdAt.toString()));
                }
                return resp;
            }).collect(Collectors.toList());

            log.info("成功获取骑手列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("riders", responses));
            
        } catch (Exception e) {
            log.error("获取骑手列表失败", e);
            return ResponseBuilder.fail("获取骑手列表失败: " + e.getMessage());
        }
    }

    /**
     * 管理员分页查看商家列表
     * 通过网关API调用商家微服务获取数据
     * 
     * @param request 商家查询请求对象
     * @return CommonResponse 商家列表响应
     */
    @PostMapping("/merchantlist")
    public CommonResponse getMerchantList(@Valid @RequestBody AdminMerchantQueryRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        try {
            String token = tokenHeader.replace("Bearer ", "");
            int page = request.getPage();
            int pageSize = request.getPageSize();
            String keyword = request.getKeyword();
            Long startId = request.getStartId();
            Long endId = request.getEndId();
            
            log.info("管理员查询商家列表: page={}, pageSize={}, keyword={}", page, pageSize, keyword);
            
            List<Map<String, Object>> merchantList = gatewayApiClient.getMerchantList(page, pageSize, keyword, startId, endId, token);

            List<AdminMerchantQueryResponse> responses = merchantList.stream().map(merchant -> {
                AdminMerchantQueryResponse resp = new AdminMerchantQueryResponse();
                resp.setId(((Number) merchant.get("id")).longValue());
                resp.setUsername((String) merchant.get("username"));
                resp.setPhone((String) merchant.get("phone"));
                // 使用数据库字段名（下划线格式）
                Object createdAt = merchant.get("created_at");
                if (createdAt != null) {
                    resp.setCreatedAt(LocalDateTime.parse(createdAt.toString()));
                }
                return resp;
            }).collect(Collectors.toList());

            log.info("成功获取商家列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("merchants", responses));
            
        } catch (Exception e) {
            log.error("获取商家列表失败", e);
            return ResponseBuilder.fail("获取商家列表失败: " + e.getMessage());
        }
    }

    /**
     * 管理员分页查看店铺列表
     * 通过网关API调用商家微服务获取数据
     * 
     * @param request 店铺查询请求对象
     * @return CommonResponse 店铺列表响应
     */
    @PostMapping("/storelist")
    public CommonResponse getStoreList(@Valid @RequestBody AdminStoreQueryRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        try {
            String token = tokenHeader.replace("Bearer ", "");
            int page = request.getPage();
            int pageSize = request.getPageSize();
            String keyword = request.getKeyword();
            Long merchantId = request.getMerchantId();
            Integer status = request.getStatus();
            
            log.info("管理员查询店铺列表: page={}, pageSize={}, keyword={}", page, pageSize, keyword);
            
            List<Map<String, Object>> storeList = gatewayApiClient.getStoreList(page, pageSize, keyword, merchantId, status, token);

            List<AdminStoreQueryResponse> responses = storeList.stream().map(store -> {
                AdminStoreQueryResponse resp = new AdminStoreQueryResponse();
                resp.setId(((Number) store.get("id")).longValue());
                Object merchantIdObj = store.get("merchantId");
                if (merchantIdObj != null) resp.setMerchantId(((Number) merchantIdObj).longValue());
                resp.setName((String) store.get("name"));
                resp.setDescription((String) store.get("description"));
                resp.setLocation((String) store.get("location"));
                resp.setType((String) store.get("type"));
                Object ratingObj = store.get("rating");
                if (ratingObj != null) resp.setRating(BigDecimal.valueOf(((Number) ratingObj).doubleValue()));
                Object statusObj = store.get("status");
                if (statusObj != null) resp.setStatus(((Number) statusObj).intValue());
                Object createdAt = store.get("createdAt");
                if (createdAt != null) {
                    resp.setCreatedAt(LocalDateTime.parse(createdAt.toString()));
                }
                return resp;
            }).collect(Collectors.toList());

            log.info("成功获取店铺列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("stores", responses));
            
        } catch (Exception e) {
            log.error("获取店铺列表失败", e);
            return ResponseBuilder.fail("获取店铺列表失败: " + e.getMessage());
        }
    }

    /**
     * 管理员分页查看商品列表
     * 通过网关API调用商家微服务获取数据
     * 
     * @param request 商品查询请求对象
     * @return CommonResponse 商品列表响应
     */
    @PostMapping("/productlist")
    public CommonResponse getProductList(@RequestBody AdminProductQueryRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }
        
        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            log.info("管理员查询商品列表: storeId={}, page={}, pageSize={}", request.getStoreId(), request.getPage(), request.getPageSize());
            
            List<Map<String, Object>> productList = gatewayApiClient.getProductList(request.getStoreId(), request.getPage(), request.getPageSize(), token);
            
            log.info("成功获取商品列表，共{}条记录", productList.size());
            return ResponseBuilder.ok(Map.of("products", productList));
            
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return ResponseBuilder.fail("获取商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 管理员删除操作
     * 通过网关API调用相应微服务进行删除
     * 
     * @param request 删除请求对象
     * @return CommonResponse 删除结果
     */
    @DeleteMapping("/delete")
    public CommonResponse delete(@RequestBody AdminDeleteRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        boolean hasAny = request.getUserName() != null ||
                request.getRiderName() != null ||
                request.getMerchantName() != null ||
                request.getStoreName() != null ||
                request.getProductName() != null;

        if (!hasAny) {
            return ResponseBuilder.fail("至少提供一个删除目标");
        }

        try {
            String token = tokenHeader.replace("Bearer ", "");
            List<String> failed = new ArrayList<>();

            // 删除用户
            if (request.getUserName() != null) {
                log.info("管理员删除用户: {}", request.getUserName());
                if (!gatewayApiClient.deleteUser(request.getUserName(), token)) {
                    failed.add("用户删除失败");
                }
            }

            // 删除骑手
            if (request.getRiderName() != null) {
                log.info("管理员删除骑手: {}", request.getRiderName());
                if (!gatewayApiClient.deleteRider(request.getRiderName(), token)) {
                    failed.add("骑手删除失败");
                }
            }

            // 删除商家
            if (request.getMerchantName() != null) {
                log.info("管理员删除商家: {}", request.getMerchantName());
                if (!gatewayApiClient.deleteMerchant(request.getMerchantName(), token)) {
                    failed.add("商家删除失败");
                }
            }

            // 删除商品（需要店铺名）
            if (request.getProductName() != null) {
                if (request.getStoreName() == null) {
                    return ResponseBuilder.fail("删除商品必须同时提供所属店铺名");
                }
                log.info("管理员删除商品: {} from {}", request.getProductName(), request.getStoreName());
                if (!gatewayApiClient.deleteProduct(request.getProductName(), request.getStoreName(), token)) {
                    failed.add("商品删除失败");
                }
            } else if (request.getStoreName() != null) {
                // 删除店铺（仅当未指定商品时才删除店铺）
                log.info("管理员删除店铺: {}", request.getStoreName());
                if (!gatewayApiClient.deleteStore(request.getStoreName(), token)) {
                    failed.add("店铺删除失败");
                }
            }

            if (!failed.isEmpty()) {
                return ResponseBuilder.fail("删除失败：" + String.join("；", failed));
            }

            log.info("管理员删除操作成功");
            return ResponseBuilder.ok("删除成功");
            
        } catch (Exception e) {
            log.error("删除操作失败", e);
            return ResponseBuilder.fail("删除操作失败: " + e.getMessage());
        }
    }


    @PostMapping("/orderlist")
    public CommonResponse getOrderList(@RequestBody Map<String, Object> requestMap, @RequestHeader("Authorization") String tokenHeader) {
        // 身份校验
        if (!"admin".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            // 从Map中提取参数
            int page = requestMap.get("page") != null ? ((Number) requestMap.get("page")).intValue() : 1;
            int pageSize = requestMap.get("pageSize") != null ? ((Number) requestMap.get("pageSize")).intValue() : 10;
            
            Long userId = requestMap.get("userId") != null ? ((Number) requestMap.get("userId")).longValue() : null;
            Long storeId = requestMap.get("storeId") != null ? ((Number) requestMap.get("storeId")).longValue() : null;
            Long riderId = requestMap.get("riderId") != null ? ((Number) requestMap.get("riderId")).longValue() : null;
            Integer status = requestMap.get("status") != null ? ((Number) requestMap.get("status")).intValue() : null;
            
            // 处理日期时间字符串转换
            LocalDateTime createdAt = null;
            LocalDateTime endedAt = null;
            if (requestMap.get("createdAt") != null) {
                try {
                    String createdAtStr = (String) requestMap.get("createdAt");
                    createdAt = LocalDateTime.parse(createdAtStr.replace(" ", "T"));
                } catch (Exception e) {
                    log.warn("创建时间格式错误: {}", requestMap.get("createdAt"));
                }
            }
            if (requestMap.get("endedAt") != null) {
                try {
                    String endedAtStr = (String) requestMap.get("endedAt");
                    endedAt = LocalDateTime.parse(endedAtStr.replace(" ", "T"));
                } catch (Exception e) {
                    log.warn("结束时间格式错误: {}", requestMap.get("endedAt"));
                }
            }
            
            log.info("管理员查询订单列表: page={}, pageSize={}", page, pageSize);
            
            List<Map<String, Object>> orders = gatewayApiClient.getOrderList(
                    userId,
                    storeId,
                    riderId,
                    status,
                    createdAt,
                    endedAt,
                    page,
                    pageSize,
                    token
            );

            List<AdminOrderQueryResponse> responses = orders.stream().map(order -> {
                AdminOrderQueryResponse resp = new AdminOrderQueryResponse();
                resp.setOrderId(((Number) order.get("id")).longValue());
                // 使用数据库字段名（下划线格式）
                Object userIdObj = order.get("user_id");
                if (userIdObj != null) resp.setUserId(((Number) userIdObj).longValue());
                Object storeIdObj = order.get("store_id");
                if (storeIdObj != null) resp.setStoreId(((Number) storeIdObj).longValue());
                Object riderIdObj = order.get("rider_id");
                if (riderIdObj != null) resp.setRiderId(((Number) riderIdObj).longValue());
                Object statusObj = order.get("status");
                if (statusObj != null) resp.setStatus(((Number) statusObj).intValue());
                Object totalPriceObj = order.get("total_price");
                if (totalPriceObj != null) resp.setTotalPrice(BigDecimal.valueOf(((Number) totalPriceObj).doubleValue()));
                Object orderCreatedAt = order.get("created_at");
                if (orderCreatedAt != null) resp.setCreatedAt(LocalDateTime.parse(orderCreatedAt.toString()));
                Object deadline = order.get("deadline");
                if (deadline != null) resp.setDeadline(LocalDateTime.parse(deadline.toString()));
                Object orderEndedAt = order.get("ended_at");
                if (orderEndedAt != null) resp.setEndedAt(LocalDateTime.parse(orderEndedAt.toString()));
                return resp;
            }).collect(Collectors.toList());

            log.info("成功获取订单列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("orders", responses));
            
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return ResponseBuilder.fail("获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 管理员分页查看评论
     * 通过网关API调用网关微服务获取数据
     * 
     * @param request 评论查询请求对象
     * @return CommonResponse 评论列表响应
     */
    @PostMapping("/reviewlist")
    public CommonResponse getReviewList(@Valid @RequestBody AdminReviewQueryRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        try {
            String token = tokenHeader.replace("Bearer ", "");
            
            // 设置默认值，与其他列表查询保持一致
            int page = request.getPage() != null ? request.getPage() : 1;
            int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
            Long userId = request.getUserId();
            Long storeId = request.getStoreId();
            Long productId = request.getProductId();
            LocalDateTime startTime = request.getStartTime();
            LocalDateTime endTime = request.getEndTime();
            Integer startRating = request.getStartRating();
            Integer endRating = request.getEndRating();
            
            log.info("管理员查询评论列表: page={}, pageSize={}", page, pageSize);

            List<Map<String, Object>> reviews = gatewayApiClient.getReviewList(
                    userId,
                    storeId,
                    productId,
                    startTime,
                    endTime,
                    page,
                    pageSize,
                    startRating,
                    endRating,
                    token
            );

            List<AdminReviewQueryResponse> responses = reviews.stream().map(review -> {
                AdminReviewQueryResponse resp = new AdminReviewQueryResponse();
                resp.setId(((Number) review.get("id")).longValue());
                // 使用数据库字段名（下划线格式）
                Object userIdObj = review.get("user_id");
                if (userIdObj != null) resp.setUserId(((Number) userIdObj).longValue());
                Object storeIdObj = review.get("store_id");
                if (storeIdObj != null) resp.setStoreId(((Number) storeIdObj).longValue());
                Object productIdObj = review.get("product_id");
                if (productIdObj != null) resp.setProductId(((Number) productIdObj).longValue());
                Object ratingObj = review.get("rating");
                if (ratingObj != null) resp.setRating(BigDecimal.valueOf(((Number) ratingObj).intValue()));
                resp.setComment((String) review.get("comment"));
                Object reviewCreatedAt = review.get("created_at");
                if (reviewCreatedAt != null) resp.setCreatedAt(LocalDateTime.parse(reviewCreatedAt.toString()));
                return resp;
            }).collect(Collectors.toList());

            log.info("成功获取评论列表，共{}条记录", responses.size());
            return ResponseBuilder.ok(Map.of("reviews", responses));
            
        } catch (Exception e) {
            log.error("获取评论列表失败", e);
            return ResponseBuilder.fail("获取评论列表失败");
        }
    }

    /**
     * 管理员搜索店铺和商品
     * 通过网关API调用商家微服务获取数据，与用户搜索逻辑保持一致
     * 
     * @param request 搜索请求对象
     * @param tokenHeader Authorization头部信息
     * @return CommonResponse 搜索结果响应
     */
    @PostMapping("/search")
    public CommonResponse searchStoreAndProduct(@Valid @RequestBody AdminSearchRequest request, @RequestHeader("Authorization") String tokenHeader) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        String keyword = request.getKeyword();
        BigDecimal distance = request.getDistance();
        BigDecimal wishPrice = request.getWishPrice();
        BigDecimal startRating = request.getStartRating();
        BigDecimal endRating = request.getEndRating();
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        
        log.info("管理员搜索店铺和商品: keyword={}, distance={}, wishPrice={}, startRating={}, endRating={}, page={}, pageSize={}", 
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
            List<AdminSearchResponse> responses = new ArrayList<>();

            if (searchResults != null && !searchResults.isEmpty()) {
                responses = searchResults.stream().map(store -> {
                     AdminSearchResponse response = new AdminSearchResponse();
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
}