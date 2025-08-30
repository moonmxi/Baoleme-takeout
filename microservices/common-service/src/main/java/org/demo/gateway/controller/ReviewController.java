/**
 * 评论控制器
 * 提供评论相关的REST API接口
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.gateway.controller;

import jakarta.validation.Valid;
import org.demo.gateway.common.CommonResponse;
import org.demo.gateway.common.ResponseBuilder;
import org.demo.gateway.common.UserHolder;
import org.demo.gateway.dto.request.ReviewReadRequest;
import org.demo.gateway.dto.request.UserReviewRequest;
import org.demo.gateway.dto.response.ReviewPageResponse;
import org.demo.gateway.dto.response.ReviewReadResponse;
import org.demo.gateway.dto.response.UserReviewResponse;
import org.demo.gateway.client.GatewayApiClient;
import org.demo.gateway.mapper.ProductMapper;
import org.demo.gateway.mapper.UserMapper;
import org.demo.gateway.pojo.Page;
import org.demo.gateway.pojo.Product;
import org.demo.gateway.pojo.Review;
import org.demo.gateway.pojo.User;
import org.demo.gateway.service.ReviewService;
import org.demo.gateway.service.StoreService;
import org.demo.gateway.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论控制器
 * 处理用户提交评论和商家查看评论的相关请求
 */
@RestController
@RequestMapping("/store/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final StoreService storeService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final ProductMapper productMapper;
    private final GatewayApiClient gatewayApiClient;

    /**
     * 构造函数注入依赖
     * 
     * @param reviewService 评论服务
     * @param storeService 店铺服务
     * @param userMapper 用户数据访问层
     * @param userService 用户服务
     * @param productMapper 商品数据访问层
     * @param gatewayApiClient 网关API客户端
     */
    public ReviewController(ReviewService reviewService, 
                           StoreService storeService, 
                           UserMapper userMapper, 
                           UserService userService, 
                           ProductMapper productMapper,
                           GatewayApiClient gatewayApiClient) {
        this.reviewService = reviewService;
        this.storeService = storeService;
        this.userMapper = userMapper;
        this.userService = userService;
        this.productMapper = productMapper;
        this.gatewayApiClient = gatewayApiClient;
    }

    /**
     * 用户提交评论接口
     * 允许用户对店铺或商品进行评价
     * 
     * @param request 评论请求参数
     * @return 评论提交结果
     */
    @PostMapping("/submit")
    public CommonResponse submitReview(@Valid @RequestBody UserReviewRequest request) {
        try {
            Long userId = UserHolder.getId();
            UserReviewResponse response = userService.submitReview(userId, request);
            return ResponseBuilder.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.fail(e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.fail("评论提交失败：" + e.getMessage());
        }
    }

    /**
     * 商家查看店铺评论列表接口（分页）
     * 商家可以查看自己店铺的所有评论
     * 
     * @param tokenHeader 授权令牌
     * @param request 查看评论请求参数
     * @return 分页评论列表
     */
    @PostMapping("/list")
    public CommonResponse getStoreReviews(
            @RequestHeader(value = "Authorization", required = false) String tokenHeader,
            @RequestBody ReviewReadRequest request
    ) {
        System.out.println("收到请求：" + request);

        Long storeId = request.getStoreId();
        int page = request.getPage();
        int pageSize = request.getPageSize();

        // 验证店铺所有权
        if (!storeService.validateStoreOwnership(storeId, UserHolder.getId())) {
            return ResponseBuilder.fail("无权查看");
        }

        // 验证分页参数合法性
        if (page < 1 || pageSize < 1) {
            return ResponseBuilder.fail("分页参数必须大于0");
        }

        // 查询分页数据（不进行评分筛选）
        Page<Review> reviewPage = reviewService.getStoreReviewsPage(
                storeId,
                page,
                pageSize
        );

        // 转换为响应结构（空列表也视为正常结果）
        ReviewPageResponse response = convertToPageResponse(reviewPage, tokenHeader);

        System.out.println("回复：" + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 商家查看店铺评论列表接口（带筛选条件）
     * 商家可以根据评分和图片条件筛选评论
     * 
     * @param tokenHeader 授权令牌
     * @param request 筛选评论请求参数
     * @return 筛选后的分页评论列表
     */
    @PostMapping("/filter")
    public CommonResponse getFilteredReviews(
            @RequestHeader(value = "Authorization", required = false) String tokenHeader,
            @RequestBody ReviewReadRequest request
    ) {
        System.out.println("收到请求：" + request);

        Long storeId = request.getStoreId();

        // 验证店铺所有权
        if (!storeService.validateStoreOwnership(storeId, UserHolder.getId())) {
            return ResponseBuilder.fail("无权查看");
        }

        // 验证分页参数合法性
        if (request.getPage() < 1 || request.getPageSize() < 1) {
            return ResponseBuilder.fail("分页参数必须大于0");
        }

        // 根据筛选类型确定评分范围
        Integer min = null;
        Integer max = null;
        if (request.getType() != null) {
            switch (request.getType()) {
                case POSITIVE:
                    min = 4;
                    max = 5;
                    break;
                case NEGATIVE:
                    min = 1;
                    max = 2;
                    break;
                default:
                    return ResponseBuilder.fail("无效的筛选类型");
            }
        }

        // 查询分页数据
        Page<Review> reviewPage = reviewService.getFilteredReviews(
                storeId,
                min,
                max,
                request.getHasImage(),
                request.getPage(),
                request.getPageSize()
        );

        // 转换为响应结构
        ReviewPageResponse response = convertToPageResponse(reviewPage, tokenHeader);

        // 返回结果
        return ResponseBuilder.ok(response);
    }

    /**
     * 辅助方法：将Review分页结果转换为ReviewPageResponse
     * 
     * @param reviewPage 评论分页结果
     * @param tokenHeader JWT认证令牌
     * @return 评论分页响应DTO
     */
    private ReviewPageResponse convertToPageResponse(Page<Review> reviewPage, String tokenHeader) {
        ReviewPageResponse response = new ReviewPageResponse();
        response.setCurrentPage(reviewPage.getCurrPage());
        response.setPageSize(reviewPage.getPageSize());
        response.setTotalCount(reviewPage.getCount());
        response.setTotalPages(reviewPage.getPageCount());
        response.setPrePage(reviewPage.getPrePage());
        response.setNextPage(reviewPage.getNextPage());

        // 转换评论列表
        List<ReviewReadResponse> reviews = reviewPage.getList().stream().map(review -> {
            ReviewReadResponse item = new ReviewReadResponse();

            // 通过网关API获取用户信息
            try {
                Map<String, Object> userInfo = gatewayApiClient.getUserById(review.getUserId(), tokenHeader);
                if (userInfo != null && !userInfo.isEmpty()) {
                    item.setUserId(review.getUserId());
                    String username = (String) userInfo.get("username");
                    item.setUsername(username != null ? "用户" + username : "未知用户");
                    item.setUserAvatar((String) userInfo.get("avatar"));
                } else {
                    item.setUserId(review.getUserId());
                    item.setUsername("用户" + review.getUserId());
                    item.setUserAvatar(null);
                }
            } catch (Exception e) {
                System.out.println("获取用户信息失败: userId=" + review.getUserId() + ", error=" + e.getMessage());
                item.setUserId(review.getUserId());
                item.setUsername("用户" + review.getUserId());
                item.setUserAvatar(null);
            }

            // 通过网关API获取商品信息
            if (review.getProductId() != null) {
                try {
                    Map<String, Object> productInfo = gatewayApiClient.getProductById(review.getProductId(), tokenHeader);
                    if (productInfo != null && !productInfo.isEmpty()) {
                        item.setProductId(review.getProductId());
                        item.setProductName((String) productInfo.get("name"));
                    } else {
                        item.setProductId(review.getProductId());
                        item.setProductName("未知商品");
                    }
                } catch (Exception e) {
                    System.out.println("获取商品信息失败: productId=" + review.getProductId() + ", error=" + e.getMessage());
                    item.setProductId(review.getProductId());
                    item.setProductName("未知商品");
                }
            }

            // 设置评论信息
            item.setRating(review.getRating());
            item.setComment(review.getComment());
            item.setCreatedAt(review.getCreatedAt());
            item.setImage(review.getImage());
            return item;
        }).collect(Collectors.toList());

        response.setReviews(reviews);

        System.out.println("回复：" + response);
        return response;
    }
}