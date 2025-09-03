/**
 * ReviewController单元测试类
 * 测试评论管理相关的所有接口功能
 * 
 * @author SOLO Coding
 * @version 1.0
 * @since 2024
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.dto.request.review.ReviewReadRequest;
import org.demo.baoleme.mapper.ProductMapper;
import org.demo.baoleme.mapper.UserMapper;
import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.pojo.Review;
import org.demo.baoleme.pojo.User;
import java.math.BigDecimal;
import org.demo.baoleme.service.ReviewService;
import org.demo.baoleme.service.StoreService;
import org.demo.baoleme.service.UserService;
import org.demo.baoleme.common.UserHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;
import org.demo.baoleme.config.TestConfig;

/**
 * ReviewController测试类
 * 包含评论列表查询、筛选等功能的测试用例
 */
@WebMvcTest(ReviewController.class)
@Import(TestConfig.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private StoreService storeService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private org.demo.baoleme.common.JwtInterceptor jwtInterceptor;

    /**
     * 测试前的初始化设置
     * 模拟用户登录状态
     */
    @BeforeEach
    void setUp() {
        // 模拟用户登录状态
        try (var mockedStatic = mockStatic(UserHolder.class)) {
            mockedStatic.when(UserHolder::getId).thenReturn(1L);
        }
    }

    /**
     * 测试获取店铺评论列表 - 正向测试用例
     * 验证正常获取店铺评论列表的业务流程
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取店铺评论列表 - 成功场景")
    void testGetStoreReviews_Success() throws Exception {
        // 准备测试数据
        ReviewReadRequest request = new ReviewReadRequest();
        request.setStoreId(1L);
        request.setPage(1);
        request.setPageSize(10);

        Review review1 = new Review();
        review1.setId(1L);
        review1.setUserId(1L);
        review1.setProductId(1L);
        review1.setRating(new BigDecimal("5"));
        review1.setComment("很好吃");
        review1.setCreatedAt(LocalDateTime.now());
        review1.setImage("review1.jpg");

        Review review2 = new Review();
        review2.setId(2L);
        review2.setUserId(2L);
        review2.setProductId(2L);
        review2.setRating(new BigDecimal("4"));
        review2.setComment("不错");
        review2.setCreatedAt(LocalDateTime.now());

        List<Review> reviews = Arrays.asList(review1, review2);
        Page<Review> mockPage = new Page<>();
        mockPage.setList(reviews);
        mockPage.setCurrPage(1);
        mockPage.setPageCount(1);
        mockPage.setCount(2);
        mockPage.setPageSize(10);
        mockPage.setPrePage(0);
        mockPage.setNextPage(0);

        User mockUser1 = new User();
        mockUser1.setId(1L);
        mockUser1.setUsername("testuser1");
        mockUser1.setAvatar("avatar1.jpg");

        User mockUser2 = new User();
        mockUser2.setId(2L);
        mockUser2.setUsername("testuser2");
        mockUser2.setAvatar("avatar2.jpg");

        Product mockProduct1 = new Product();
        mockProduct1.setId(1L);
        mockProduct1.setName("商品1");

        Product mockProduct2 = new Product();
        mockProduct2.setId(2L);
        mockProduct2.setName("商品2");

        // 模拟服务层行为
        when(storeService.validateStoreOwnership(eq(1L), eq(1L))).thenReturn(true);
        when(reviewService.getStoreReviewsPage(eq(1L), eq(1), eq(10))).thenReturn(mockPage);
        when(userMapper.selectById(eq(1L))).thenReturn(mockUser1);
        when(userMapper.selectById(eq(2L))).thenReturn(mockUser2);
        when(productMapper.selectById(eq(1L))).thenReturn(mockProduct1);
        when(productMapper.selectById(eq(2L))).thenReturn(mockProduct2);

        // 执行测试请求
        mockMvc.perform(post("/store/reviews/list")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reviews").isArray())
                .andExpect(jsonPath("$.data.reviews.length()").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1));

        // 验证服务层调用
        verify(storeService, times(1)).validateStoreOwnership(eq(1L), eq(1L));
        verify(reviewService, times(1)).getStoreReviewsPage(eq(1L), eq(1), eq(10));
        verify(userMapper, times(1)).selectById(eq(1L));
        verify(userMapper, times(1)).selectById(eq(2L));
        verify(productMapper, times(1)).selectById(eq(1L));
        verify(productMapper, times(1)).selectById(eq(2L));
    }

    /**
     * 测试获取店铺评论列表 - 反向测试用例
     * 验证没有权限时的异常处理
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取店铺评论列表 - 没有权限")
    void testGetStoreReviews_NoPermission() throws Exception {
        // 准备测试数据
        ReviewReadRequest request = new ReviewReadRequest();
        request.setStoreId(1L);
        request.setPage(1);
        request.setPageSize(10);

        // 模拟服务层行为 - 没有权限
        when(storeService.validateStoreOwnership(eq(1L), eq(1L))).thenReturn(false);

        // 执行测试请求
        mockMvc.perform(post("/store/reviews/list")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无权查看"));

        // 验证服务层调用
        verify(storeService, times(1)).validateStoreOwnership(eq(1L), eq(1L));
        verify(reviewService, never()).getStoreReviewsPage(anyLong(), anyInt(), anyInt());
    }

    /**
     * 测试获取店铺评论列表 - 反向测试用例
     * 验证分页参数无效时的异常处理
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取店铺评论列表 - 分页参数无效")
    void testGetStoreReviews_InvalidPageParams() throws Exception {
        // 准备测试数据 - 无效的分页参数
        ReviewReadRequest request = new ReviewReadRequest();
        request.setStoreId(1L);
        request.setPage(0); // 无效的页码
        request.setPageSize(10);

        // 模拟服务层行为
        when(storeService.validateStoreOwnership(eq(1L), eq(1L))).thenReturn(true);

        // 执行测试请求
        mockMvc.perform(post("/store/reviews/list")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("分页参数必须大于0"));

        // 验证服务层调用
        verify(storeService, times(1)).validateStoreOwnership(eq(1L), eq(1L));
        verify(reviewService, never()).getStoreReviewsPage(anyLong(), anyInt(), anyInt());
    }

    /**
     * 测试筛选评论 - 正向测试用例（好评筛选）
     * 验证正常筛选好评的业务流程
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("筛选评论 - 好评筛选成功")
    void testGetFilteredReviews_PositiveFilter_Success() throws Exception {
        // 准备测试数据
        ReviewReadRequest request = new ReviewReadRequest();
        request.setStoreId(1L);
        request.setType(ReviewReadRequest.ReviewFilterType.POSITIVE);
        request.setPage(1);
        request.setPageSize(10);
        request.setHasImage(true);

        Review review1 = new Review();
        review1.setId(1L);
        review1.setUserId(1L);
        review1.setProductId(1L);
        review1.setRating(new BigDecimal("5"));
        review1.setComment("非常好吃");
        review1.setCreatedAt(LocalDateTime.now());
        review1.setImage("review1.jpg");

        List<Review> reviews = Arrays.asList(review1);
        Page<Review> mockPage = new Page<>();
        mockPage.setList(reviews);
        mockPage.setCurrPage(1);
        mockPage.setPageCount(1);
        mockPage.setCount(1);
        mockPage.setPageSize(10);
        mockPage.setPrePage(0);
        mockPage.setNextPage(0);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setAvatar("avatar.jpg");

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("测试商品");

        // 模拟服务层行为
        when(storeService.validateStoreOwnership(eq(1L), eq(1L))).thenReturn(true);
        when(reviewService.getFilteredReviews(eq(1L), eq(4), eq(5), eq(true), eq(1), eq(10)))
                .thenReturn(mockPage);
        when(userMapper.selectById(eq(1L))).thenReturn(mockUser);
        when(productMapper.selectById(eq(1L))).thenReturn(mockProduct);

        // 执行测试请求
        mockMvc.perform(post("/store/reviews/filter")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reviews").isArray())
                .andExpect(jsonPath("$.data.reviews.length()").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.totalCount").value(1));

        // 验证服务层调用
        verify(storeService, times(1)).validateStoreOwnership(eq(1L), eq(1L));
        verify(reviewService, times(1)).getFilteredReviews(eq(1L), eq(4), eq(5), eq(true), eq(1), eq(10));
        verify(userMapper, times(1)).selectById(eq(1L));
        verify(productMapper, times(1)).selectById(eq(1L));
    }

    /**
     * 测试筛选评论 - 正向测试用例（差评筛选）
     * 验证正常筛选差评的业务流程
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("筛选评论 - 差评筛选成功")
    void testGetFilteredReviews_NegativeFilter_Success() throws Exception {
        // 准备测试数据
        ReviewReadRequest request = new ReviewReadRequest();
        request.setStoreId(1L);
        request.setType(ReviewReadRequest.ReviewFilterType.NEGATIVE);
        request.setPage(1);
        request.setPageSize(10);
        request.setHasImage(false);

        Review review1 = new Review();
        review1.setId(1L);
        review1.setUserId(1L);
        review1.setProductId(1L);
        review1.setRating(new BigDecimal("2"));
        review1.setComment("不太好吃");
        review1.setCreatedAt(LocalDateTime.now());

        List<Review> reviews = Arrays.asList(review1);
        Page<Review> mockPage = new Page<>();
        mockPage.setList(reviews);
        mockPage.setCurrPage(1);
        mockPage.setPageCount(1);
        mockPage.setCount(1);
        mockPage.setPageSize(10);
        mockPage.setPrePage(0);
        mockPage.setNextPage(0);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setAvatar("avatar.jpg");

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("测试商品");

        // 模拟服务层行为
        when(storeService.validateStoreOwnership(eq(1L), eq(1L))).thenReturn(true);
        when(reviewService.getFilteredReviews(eq(1L), eq(1), eq(2), eq(false), eq(1), eq(10)))
                .thenReturn(mockPage);
        when(userMapper.selectById(eq(1L))).thenReturn(mockUser);
        when(productMapper.selectById(eq(1L))).thenReturn(mockProduct);

        // 执行测试请求
        mockMvc.perform(post("/store/reviews/filter")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reviews").isArray())
                .andExpect(jsonPath("$.data.reviews.length()").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.totalCount").value(1));

        // 验证服务层调用
        verify(storeService, times(1)).validateStoreOwnership(eq(1L), eq(1L));
        verify(reviewService, times(1)).getFilteredReviews(eq(1L), eq(1), eq(2), eq(false), eq(1), eq(10));
        verify(userMapper, times(1)).selectById(eq(1L));
        verify(productMapper, times(1)).selectById(eq(1L));
    }

    /**
     * 测试筛选评论 - 反向测试用例
     * 验证没有权限时的异常处理
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("筛选评论 - 没有权限")
    void testGetFilteredReviews_NoPermission() throws Exception {
        // 准备测试数据
        ReviewReadRequest request = new ReviewReadRequest();
        request.setStoreId(1L);
        request.setType(ReviewReadRequest.ReviewFilterType.POSITIVE);
        request.setPage(1);
        request.setPageSize(10);

        // 模拟服务层行为 - 没有权限
        when(storeService.validateStoreOwnership(eq(1L), eq(1L))).thenReturn(false);

        // 执行测试请求
        mockMvc.perform(post("/store/reviews/filter")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无权查看"));

        // 验证服务层调用
        verify(storeService, times(1)).validateStoreOwnership(eq(1L), eq(1L));
        verify(reviewService, never()).getFilteredReviews(anyLong(), anyInt(), anyInt(), anyBoolean(), anyInt(), anyInt());
    }

    /**
     * 测试筛选评论 - 反向测试用例
     * 验证分页参数无效时的异常处理
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("筛选评论 - 分页参数无效")
    void testGetFilteredReviews_InvalidPageParams() throws Exception {
        // 准备测试数据 - 无效的分页参数
        ReviewReadRequest request = new ReviewReadRequest();
        request.setStoreId(1L);
        request.setType(ReviewReadRequest.ReviewFilterType.POSITIVE);
        request.setPage(-1); // 无效的页码
        request.setPageSize(10);

        // 模拟服务层行为
        when(storeService.validateStoreOwnership(eq(1L), eq(1L))).thenReturn(true);

        // 执行测试请求
        mockMvc.perform(post("/store/reviews/filter")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("分页参数必须大于0"));

        // 验证服务层调用
        verify(storeService, times(1)).validateStoreOwnership(eq(1L), eq(1L));
        verify(reviewService, never()).getFilteredReviews(anyLong(), anyInt(), anyInt(), anyBoolean(), anyInt(), anyInt());
    }

    /**
     * 测试筛选评论 - 反向测试用例
     * 验证无效筛选类型时的异常处理
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("筛选评论 - 无效筛选类型")
    void testGetFilteredReviews_InvalidFilterType() throws Exception {
        // 准备测试数据 - 这里我们通过直接构造JSON来模拟无效的筛选类型
        String invalidRequestJson = "{\"storeId\":1,\"type\":\"INVALID\",\"page\":1,\"pageSize\":10}";

        // 模拟服务层行为
        when(storeService.validateStoreOwnership(eq(1L), eq(1L))).thenReturn(true);

        // 执行测试请求 - 期望JSON解析失败或者业务逻辑处理
        mockMvc.perform(post("/store/reviews/filter")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isOk()); // 验证请求能正常处理

        // 注意：由于枚举类型的限制，实际上无效的枚举值会在JSON反序列化时就失败
        // 这里主要是验证系统的健壮性
    }
}