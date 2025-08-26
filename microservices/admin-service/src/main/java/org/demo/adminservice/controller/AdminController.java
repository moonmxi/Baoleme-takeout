/**
 * 管理员控制器
 * 提供管理员相关的REST API接口，包括登录、登出、用户管理、商家管理、骑手管理等功能
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.adminservice.controller;

import jakarta.validation.Valid;
import org.demo.adminservice.common.CommonResponse;
import org.demo.adminservice.common.JwtUtils;
import org.demo.adminservice.common.ResponseBuilder;
import org.demo.adminservice.common.UserHolder;
import org.demo.adminservice.dto.request.admin.*;
import org.demo.adminservice.dto.response.admin.*;
import org.demo.adminservice.pojo.*;
import org.demo.adminservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 管理员控制器类
 * 处理管理员相关的HTTP请求，包括登录认证、用户管理、商家管理、骑手管理等功能
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService, RedisTemplate<String, Object> redisTemplate) {
        this.adminService = adminService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 管理员登录
     * 
     * @param request 登录请求对象，包含管理员ID和密码
     * @return CommonResponse 登录结果，包含管理员信息和token
     */
    @PostMapping("/login")
    public CommonResponse login(@Valid @RequestBody AdminLoginRequest request) {
        Admin admin = adminService.login(request.getAdminId(), request.getPassword());
        if (admin == null) {
            return ResponseBuilder.fail("账号或密码错误");
        }

        // 构建 Redis 中用于标识登录状态的 key
        String redisLoginKey = "admin:login:" + admin.getId();

        // 如果已经存在登录状态，则拒绝登录
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisLoginKey))) {
            return ResponseBuilder.fail("该账户已登录，请先登出后再登录");
        }

        // 生成 token
        String token = JwtUtils.createToken(admin.getId(), "admin", null);

        // 将 token 存入 Redis（用于拦截器校验）
        String redisTokenKey = "admin:token:" + token;
        redisTemplate.opsForValue().set(redisTokenKey, admin.getId(), 1, TimeUnit.DAYS);

        // 记录登录状态（防止重复登录）
        redisTemplate.opsForValue().set(redisLoginKey, token, 1, TimeUnit.DAYS);

        // 返回响应
        AdminLoginResponse response = new AdminLoginResponse();
        response.setId(admin.getId());
        response.setToken(token);

        return ResponseBuilder.ok(response);
    }

    /**
     * 管理员登出
     * 
     * @param tokenHeader Authorization头部信息
     * @return CommonResponse 登出结果
     */
    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String redisTokenKey = "admin:token:" + token;

        // 删除 token -> id 映射
        Object userId = redisTemplate.opsForValue().get(redisTokenKey);
        if (userId != null) {
            String redisLoginKey = "admin:login:" + userId;
            redisTemplate.delete(redisLoginKey);
        }

        redisTemplate.delete(redisTokenKey);
        return ResponseBuilder.ok("登出成功");
    }

    /**
     * 管理员分页查看用户列表
     * 
     * @param request 用户查询请求对象
     * @return CommonResponse 用户列表响应
     */
    @PostMapping("/userlist")
    public CommonResponse getUserList(@Valid @RequestBody AdminUserQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPageSize();
        String keyword = request.getKeyword();
        String gender = request.getGender();
        Long startId = request.getStartId();
        Long endId = request.getEndId();
        List<User> userList = adminService.getAllUsersPaged(page, pageSize, keyword, gender, startId, endId);

        List<AdminUserQueryResponse> responses = userList.stream().map(user -> {
            AdminUserQueryResponse resp = new AdminUserQueryResponse();
            resp.setId(user.getId());
            resp.setUsername(user.getUsername());
            resp.setDescription(user.getDescription());
            resp.setPhone(user.getPhone());
            resp.setAvatar(user.getAvatar());
            resp.setCreatedAt(user.getCreatedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("users", responses));
    }



    /**
     * 管理员分页查看骑手列表
     * 
     * @param request 骑手查询请求对象
     * @return CommonResponse 骑手列表响应
     */
    @PostMapping("/riderlist")
    public CommonResponse getRiderList(@Valid @RequestBody AdminRiderQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPageSize();
        String keyword = request.getKeyword();
        Long startId = request.getStartId();
        Long endId = request.getEndId();
        Integer status = request.getStatus();
        Integer dispatchMode = request.getDispatchMode();
        Long startBalance = request.getStartBalance();
        Long endBalance = request.getEndBalance();
        List<Rider> riderList = adminService.getAllRidersPaged(page, pageSize, keyword, startId, endId, status, dispatchMode, startBalance, endBalance);

        List<AdminRiderQueryResponse> responses = riderList.stream().map(rider -> {
            AdminRiderQueryResponse resp = new AdminRiderQueryResponse();
            resp.setId(rider.getId());
            resp.setUsername(rider.getUsername());
            resp.setPhone(rider.getPhone());
            resp.setStatus(rider.getStatus());
            resp.setDispatchMode(rider.getDispatchMode());
            resp.setBalance(rider.getBalance());
            resp.setAvatar(rider.getAvatar());
            resp.setCreatedAt(rider.getCreatedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("riders", responses));
    }

    /**
     * 管理员分页查看商家列表
     * 
     * @param request 商家查询请求对象
     * @return CommonResponse 商家列表响应
     */
    @PostMapping("/merchantlist")
    public CommonResponse getMerchantList(@Valid @RequestBody AdminMerchantQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPageSize();
        String keyword = request.getKeyword();
        Long startId = request.getStartId();
        Long endId = request.getEndId();
        List<Merchant> merchantList = adminService.getAllMerchantsPaged(page, pageSize, keyword, startId, endId);

        List<AdminMerchantQueryResponse> responses = merchantList.stream().map(merchant -> {
            AdminMerchantQueryResponse resp = new AdminMerchantQueryResponse();
            resp.setId(merchant.getId());
            resp.setUsername(merchant.getUsername());
            resp.setPhone(merchant.getPhone());
            resp.setCreatedAt(merchant.getCreatedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("merchants", responses));
    }

    /**
     * 管理员分页查看店铺列表
     * 
     * @param request 店铺查询请求对象
     * @return CommonResponse 店铺列表响应
     */
    @PostMapping("/storelist")
    public CommonResponse getStoreList(@Valid @RequestBody AdminStoreQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPageSize();
        String keyword = request.getKeyword();
        Long merchantId = request.getMerchantId();
        Integer status = request.getStatus();
        List<Store> storeList = adminService.getAllStoresPaged(page, pageSize, keyword, merchantId, status);

        List<AdminStoreQueryResponse> responses = storeList.stream().map(store -> {
            AdminStoreQueryResponse resp = new AdminStoreQueryResponse();
            resp.setId(store.getId());
            resp.setMerchantId(store.getMerchantId());
            resp.setName(store.getName());
            resp.setDescription(store.getDescription());
            resp.setLocation(store.getLocation());
            resp.setType(store.getType());
            resp.setRating(store.getRating());
            resp.setStatus(store.getStatus());
            resp.setCreatedAt(store.getCreatedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("stores", responses));
    }



    /**
     * 管理员分页查看商品列表
     * 
     * @param request 商品查询请求对象
     * @return CommonResponse 商品列表响应
     */
    @PostMapping("/productlist")
    public CommonResponse getProductList(@RequestBody AdminProductQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }
        return ResponseBuilder.ok(Map.of("products", adminService.getProductsByStore(request.getStoreId(), request.getPage(), request.getPageSize())));
    }

    /**
     * 管理员删除操作
     * 
     * @param request 删除请求对象
     * @return CommonResponse 删除结果
     */
    @DeleteMapping("/delete")
    public CommonResponse delete(@RequestBody AdminDeleteRequest request) {
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

        List<String> failed = new ArrayList<>();

        // 删除用户
        if (request.getUserName() != null &&
                !adminService.deleteUserByUsername(request.getUserName())) {
            failed.add("用户删除失败");
        }

        // 删除骑手
        if (request.getRiderName() != null &&
                !adminService.deleteRiderByUsername(request.getRiderName())) {
            failed.add("骑手删除失败");
        }

        // 删除商家
        if (request.getMerchantName() != null &&
                !adminService.deleteMerchantByUsername(request.getMerchantName())) {
            failed.add("商家删除失败");
        }

        // 删除商品（需要店铺名）
        if (request.getProductName() != null) {
            if (request.getStoreName() == null) {
                return ResponseBuilder.fail("删除商品必须同时提供所属店铺名");
            }
            if (!adminService.deleteProductByNameAndStore(request.getProductName(), request.getStoreName())) {
                failed.add("商品删除失败");
            }
        } else if (request.getStoreName() != null) {
            // 删除店铺（仅当未指定商品时才删除店铺）
            if (!adminService.deleteStoreByName(request.getStoreName())) {
                failed.add("店铺删除失败");
            }
        }

        if (!failed.isEmpty()) {
            return ResponseBuilder.fail("删除失败：" + String.join("；", failed));
        }

        return ResponseBuilder.ok("删除成功");
    }

    /**
     * 管理员分页查看订单
     * 
     * @param request 订单查询请求对象
     * @return CommonResponse 订单列表响应
     */
    @PostMapping("/orderlist")
    public CommonResponse getOrderList(@Valid @RequestBody AdminOrderQueryRequest request) {
        // 身份校验
        if (!"admin".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        List<Order> orders = adminService.getAllOrdersPaged(
                request.getUserId(),
                request.getStoreId(),
                request.getRiderId(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getEndedAt(),
                request.getPage(),
                request.getPageSize()
        );

        List<AdminOrderQueryResponse> responses = orders.stream().map(order -> {
            AdminOrderQueryResponse resp = new AdminOrderQueryResponse();
            resp.setOrderId(order.getId());
            resp.setUserId(order.getUserId());
            resp.setStoreId(order.getStoreId());
            resp.setRiderId(order.getRiderId());
            resp.setStatus(order.getStatus());
            resp.setTotalPrice(order.getTotalPrice());
            resp.setCreatedAt(order.getCreatedAt());
            resp.setDeadline(order.getDeadline());
            resp.setEndedAt(order.getEndedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("orders", responses));
    }

    /**
     * 管理员分页查看评论
     * 
     * @param request 评论查询请求对象
     * @return CommonResponse 评论列表响应
     */
    @PostMapping("/reviewlist")
    public CommonResponse getReviewList(@Valid @RequestBody AdminReviewQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        int page = request.getPage();
        int pageSize = request.getPageSize();

        List<Review> reviews = adminService.getReviewsByCondition(
                request.getUserId(),
                request.getStoreId(),
                request.getProductId(),
                request.getStartTime(),
                request.getEndTime(),
                page,
                pageSize,
                request.getStartRating(),
                request.getEndRating()
        );

        List<AdminReviewQueryResponse> responses = reviews.stream().map(review -> {
            AdminReviewQueryResponse resp = new AdminReviewQueryResponse();
            resp.setId(review.getId());
            resp.setUserId(review.getUserId());
            resp.setStoreId(review.getStoreId());
            resp.setProductId(review.getProductId());
            resp.setRating(review.getRating());
            resp.setComment(review.getComment());
            resp.setCreatedAt(review.getCreatedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("reviews", responses));
    }

    /**
     * 管理员搜索店铺和商品
     * 
     * @param request 搜索请求对象
     * @return CommonResponse 搜索结果响应
     */
    @PostMapping("/search")
    public CommonResponse searchStoreAndProduct(@Valid @RequestBody AdminSearchRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }

        String keyword = request.getKeyWord();
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseBuilder.fail("关键词不能为空");
        }

        List<Map<String, Object>> raw = adminService.searchStoreAndProductByKeyword(keyword.trim());

        List<AdminSearchResponse> responses = raw.stream().map(item -> {
            AdminSearchResponse resp = new AdminSearchResponse();
            resp.setStoreId((Long) item.get("store_id"));
            resp.setStoreName((String) item.get("store_name"));
            resp.setProducts((Map<String, Long>) item.get("products"));
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("results", responses));
    }

    /**
     * 根据订单ID搜索订单
     * 
     * @param request 订单搜索请求对象
     * @return CommonResponse 订单搜索结果
     */
    @PostMapping("/search-order-by-id")
    public CommonResponse searchOrderById(@Valid @RequestBody AdminSearchOrderByIdRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }
        Order order = adminService.getOrderById(request.getOrderId());
        return ResponseBuilder.ok(Map.of("order", order));
    }

    /**
     * 根据评论ID搜索评论
     * 
     * @param request 评论搜索请求对象
     * @return CommonResponse 评论搜索结果
     */
    @PostMapping("/search-review-by-id")
    public CommonResponse searchReviewById(@Valid @RequestBody AdminSearchReviewByIdRequest request) {
        String role = UserHolder.getRole();
        if (!"admin".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅管理员可操作");
        }
        Review review = adminService.getReviewById(request.getReviewId());
        return ResponseBuilder.ok(Map.of("review", review));
    }
}