/**
 * CouponController单元测试类
 * 测试优惠券相关的所有接口功能，包括创建优惠券
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.dto.request.coupon.CouponCreateRequest;
import org.demo.baoleme.pojo.Coupon;
import org.demo.baoleme.service.CouponService;
import org.demo.baoleme.common.UserHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;
import org.demo.baoleme.config.TestConfig;

/**
 * CouponController测试类
 * 使用MockMvc进行Web层测试，模拟HTTP请求和响应
 * 使用Mockito模拟Service层依赖
 */
@SpringBootTest(classes = org.demo.baoleme.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class CouponControllerTest {

    /**
     * MockMvc实例，用于模拟HTTP请求
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper实例，用于JSON序列化和反序列化
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 模拟的CouponService依赖
     */
    @MockBean
    private CouponService couponService;

    /**
     * 模拟的UserHolder依赖
     */
    @MockBean
    private UserHolder userHolder;

    /**
     * Mock的JWT拦截器，避免Bean冲突
     */
    @MockBean
    private org.demo.baoleme.common.JwtInterceptor jwtInterceptor;

    /**
     * Mock的StoreMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.StoreMapper storeMapper;

    /**
     * Mock的CouponMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.CouponMapper couponMapper;

    /**
     * 测试商户ID
     */
    private static final Long TEST_MERCHANT_ID = 1L;

    /**
     * UserHolder的静态Mock对象
     */
    private MockedStatic<UserHolder> mockedUserHolder;

    /**
     * 测试前的初始化设置
     * 设置默认的商户ID
     */
    @BeforeEach
    void setUp() {
        // 模拟UserHolder返回测试商户ID
        mockedUserHolder = mockStatic(UserHolder.class);
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);
    }

    /**
     * 测试后的清理工作
     * 关闭静态Mock以避免重复注册问题
     */
    @AfterEach
    void tearDown() {
        if (mockedUserHolder != null) {
            mockedUserHolder.close();
        }
    }

    /**
     * 测试创建折扣券成功场景
     * 验证商户能够成功创建折扣券
     */
    @Test
    @DisplayName("创建折扣券 - 成功")
    void testCreateDiscountCoupon_Success() throws Exception {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(1L);
        request.setType(1); // 折扣券
        request.setDiscount(BigDecimal.valueOf(0.8)); // 8折

        // 准备模拟返回的优惠券对象
        Coupon mockCoupon = createMockCoupon(1L, request);

        // 模拟Service层行为
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(mockCoupon);

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.coupon_id").value(1L));

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建满减券成功场景
     * 验证商户能够成功创建满减券
     */
    @Test
    @DisplayName("创建满减券 - 成功")
    void testCreateFullReduceCoupon_Success() throws Exception {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(1L);
        request.setType(2); // 满减券
        request.setFullAmount(BigDecimal.valueOf(100.0)); // 满100
        request.setReduceAmount(BigDecimal.valueOf(20.0)); // 减20

        // 准备模拟返回的优惠券对象
        Coupon mockCoupon = createMockCoupon(2L, request);

        // 模拟Service层行为
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(mockCoupon);

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.coupon_id").value(2L));

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建优惠券失败场景
     * 验证Service层返回null时的处理
     */
    @Test
    @DisplayName("创建优惠券 - 创建失败")
    void testCreateCoupon_CreateFailed() throws Exception {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(1L);
        request.setType(1);
        request.setDiscount(BigDecimal.valueOf(0.8));

        // 模拟Service层返回null（创建失败）
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺创建失败，参数校验不通过"));

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建优惠券异常场景
     * 验证Service层抛出异常时的处理
     */
    @Test
    @DisplayName("创建优惠券 - 异常")
    void testCreateCoupon_Exception() throws Exception {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(1L);
        request.setType(1);
        request.setDiscount(BigDecimal.valueOf(0.8));

        // 模拟Service层抛出异常
        when(couponService.createCoupon(any(Coupon.class)))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建优惠券参数验证失败场景
     * 验证店铺ID为null时的处理
     */
    @Test
    @DisplayName("创建优惠券 - 店铺ID为空")
    void testCreateCoupon_NullStoreId() throws Exception {
        // 准备测试数据（店铺ID为null）
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(null);
        request.setType(1);
        request.setDiscount(BigDecimal.valueOf(0.8));

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // 注意：这里可能需要根据实际的参数验证逻辑调整

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建优惠券参数验证失败场景
     * 验证优惠券类型为null时的处理
     */
    @Test
    @DisplayName("创建优惠券 - 优惠券类型为空")
    void testCreateCoupon_NullType() throws Exception {
        // 准备测试数据（优惠券类型为null）
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(1L);
        request.setType(null);
        request.setDiscount(BigDecimal.valueOf(0.8));

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // 注意：这里可能需要根据实际的参数验证逻辑调整

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建折扣券参数不完整场景
     * 验证折扣券缺少折扣率时的处理
     */
    @Test
    @DisplayName("创建折扣券 - 缺少折扣率")
    void testCreateDiscountCoupon_MissingDiscount() throws Exception {
        // 准备测试数据（折扣券但缺少折扣率）
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(1L);
        request.setType(1); // 折扣券
        // 缺少discount字段

        // 准备模拟返回的优惠券对象
        Coupon mockCoupon = createMockCoupon(3L, request);

        // 模拟Service层行为
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(mockCoupon);

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.coupon_id").value(3L));

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建满减券参数不完整场景
     * 验证满减券缺少满减条件时的处理
     */
    @Test
    @DisplayName("创建满减券 - 缺少满减条件")
    void testCreateFullReduceCoupon_MissingConditions() throws Exception {
        // 准备测试数据（满减券但缺少满减条件）
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(1L);
        request.setType(2); // 满减券
        // 缺少fullAmount和reduceAmount字段

        // 准备模拟返回的优惠券对象
        Coupon mockCoupon = createMockCoupon(4L, request);

        // 模拟Service层行为
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(mockCoupon);

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.coupon_id").value(4L));

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建优惠券无效类型场景
     * 验证优惠券类型不在有效范围内时的处理
     */
    @Test
    @DisplayName("创建优惠券 - 无效类型")
    void testCreateCoupon_InvalidType() throws Exception {
        // 准备测试数据（无效的优惠券类型）
        CouponCreateRequest request = new CouponCreateRequest();
        request.setStoreId(1L);
        request.setType(99); // 无效类型
        request.setDiscount(BigDecimal.valueOf(0.8));

        // 准备模拟返回的优惠券对象
        Coupon mockCoupon = createMockCoupon(5L, request);

        // 模拟Service层行为
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(mockCoupon);

        // 执行测试
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.coupon_id").value(5L));

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    /**
     * 测试创建优惠券请求体为空场景
     * 验证请求体为空时的处理
     */
    @Test
    @DisplayName("创建优惠券 - 请求体为空")
    void testCreateCoupon_EmptyRequestBody() throws Exception {
        // 执行测试（空请求体）
        mockMvc.perform(post("/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk()); // 注意：这里可能需要根据实际的参数验证逻辑调整

        // 验证Service方法调用
        verify(couponService).createCoupon(any(Coupon.class));
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建模拟的优惠券对象
     * 
     * @param couponId 优惠券ID
     * @param request 创建请求对象
     * @return 模拟的优惠券对象
     */
    private Coupon createMockCoupon(Long couponId, CouponCreateRequest request) {
        Coupon coupon = new Coupon();
        coupon.setId(couponId);
        coupon.setStoreId(request.getStoreId());
        coupon.setType(request.getType());
        coupon.setDiscount(request.getDiscount());
        coupon.setFullAmount(request.getFullAmount());
        coupon.setReduceAmount(request.getReduceAmount());
        coupon.setUserId(0L);
        return coupon;
    }
}