/**
 * AdminController单元测试类
 * 测试管理员相关的所有接口功能，包括登录、登出、用户管理、骑手管理、商家管理、店铺管理、商品管理、订单管理、评论管理、删除操作和搜索功能
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.dto.request.admin.*;
import org.demo.baoleme.pojo.*;
import org.demo.baoleme.service.AdminService;
import org.demo.baoleme.service.ProductService;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.UserHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;
import org.demo.baoleme.config.TestConfig;
import org.demo.baoleme.config.TestWebConfig;
import org.demo.baoleme.common.JwtInterceptor;
import org.demo.baoleme.common.JwtUtils;
import org.mockito.MockedStatic;
import org.junit.jupiter.api.AfterEach;

/**
 * AdminController测试类
 * 使用MockMvc进行Web层测试，模拟HTTP请求和响应
 * 使用Mockito模拟Service层依赖
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class AdminControllerTest {

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
     * 模拟的AdminService依赖
     */
    @MockBean
    private AdminService adminService;

    /**
     * 模拟的ProductService依赖
     */
    @MockBean
    private ProductService productService;

    @MockBean
    private JwtInterceptor jwtInterceptor;

    /**
     * Mock的AdminMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.AdminMapper adminMapper;

    /**
     * Mock的UserMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.UserMapper userMapper;

    /**
     * Mock的RiderMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.RiderMapper riderMapper;

    /**
     * Mock的MerchantMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.MerchantMapper merchantMapper;

    /**
     * Mock的StoreMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.StoreMapper storeMapper;

    /**
     * Mock的ProductMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.ProductMapper productMapper;

    /**
     * Mock的OrderMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.OrderMapper orderMapper;

    /**
     * Mock的ReviewMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.ReviewMapper reviewMapper;

    /**
     * 模拟的JwtUtils静态方法
     */
    private MockedStatic<JwtUtils> mockedJwtUtils;

    /**
     * 测试前的初始化设置
     * 设置默认的用户角色为管理员
     */
    private MockedStatic<UserHolder> mockedUserHolder;

    @BeforeEach
    void setUp() throws Exception {
        // 默认设置为管理员角色
        mockedUserHolder = mockStatic(UserHolder.class);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("admin");
        
        // Mock JwtUtils静态方法
        mockedJwtUtils = mockStatic(JwtUtils.class);
        
        // Mock JwtInterceptor让所有请求通过
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        if (mockedUserHolder != null) {
            mockedUserHolder.close();
        }
        if (mockedJwtUtils != null) {
            mockedJwtUtils.close();
        }
    }

    /**
     * 测试管理员登录成功场景
     * 验证正确的管理员ID和密码能够成功登录
     */
    @Test
    @DisplayName("管理员登录 - 成功")
    void testAdminLogin_Success() throws Exception {
        // 准备测试数据
        AdminLoginRequest request = new AdminLoginRequest();
        request.setAdminId(1L);
        request.setPassword("admin123");

        Admin mockAdmin = new Admin();
        mockAdmin.setId(1L);
        mockAdmin.setPassword("admin123");

        String mockToken = "mock-jwt-token";

        // 模拟Service层行为
        when(adminService.login(1L, "admin123")).thenReturn(mockAdmin);
        mockedJwtUtils.when(() -> JwtUtils.createToken(1L, "admin", "admin")).thenReturn(mockToken);

        // 执行测试
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value(mockToken));

        // 验证Service方法调用
        verify(adminService).login(1L, "admin123");
        mockedJwtUtils.verify(() -> JwtUtils.createToken(1L, "admin", "admin"));
    }

    /**
     * 测试管理员登录失败场景
     * 验证错误的管理员ID或密码会导致登录失败
     */
    @Test
    @DisplayName("管理员登录 - 失败")
    void testAdminLogin_Failure() throws Exception {
        // 准备测试数据
        AdminLoginRequest request = new AdminLoginRequest();
        request.setAdminId(1L);
        request.setPassword("wrongpassword");

        // 模拟Service层返回null（管理员不存在或密码错误）
        when(adminService.login(1L, "wrongpassword")).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));

        // 验证Service方法调用
        verify(adminService).login(1L, "wrongpassword");
        mockedJwtUtils.verifyNoInteractions();
    }

    /**
     * 测试管理员登出成功场景
     * 验证管理员能够成功登出
     */
    @Test
    @DisplayName("管理员登出 - 成功")
    void testAdminLogout_Success() throws Exception {
        // 执行测试
        mockMvc.perform(post("/admin/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * 测试管理员登出权限验证失败场景
     * 验证非管理员角色无法执行登出操作
     */
    @Test
    @DisplayName("管理员登出 - 权限验证失败")
    void testAdminLogout_PermissionDenied() throws Exception {
        // 模拟非管理员角色
        when(UserHolder.getRole()).thenReturn("user");

        // 执行测试
        mockMvc.perform(post("/admin/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无权限访问，仅管理员可操作"));
    }

    /**
     * 测试获取用户列表成功场景
     * 验证管理员能够成功获取用户列表
     */
    @Test
    @DisplayName("获取用户列表 - 成功")
    void testGetUserList_Success() throws Exception {
        // 准备测试数据
        AdminUserQueryRequest request = new AdminUserQueryRequest();
        request.setKeyword("test");
        request.setPage(1);
        request.setPageSize(10);

        List<User> mockUsers = Arrays.asList(
                createMockUser(1L, "user1", "male"),
                createMockUser(2L, "user2", "female")
        );

        // 模拟Service层行为
        when(adminService.getAllUsersPaged(eq(1), eq(10), eq("test"), isNull(), isNull(), isNull()))
                .thenReturn(mockUsers);

        // 执行测试
        MvcResult result = mockMvc.perform(post("/admin/userlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
        
        // 打印实际响应内容用于调试
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Actual response: " + responseContent);
        
        // 继续验证
        mockMvc.perform(post("/admin/userlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.users").isArray())
                .andExpect(jsonPath("$.data.users.length()").value(2));

        // 验证Service方法调用
        verify(adminService, times(2)).getAllUsersPaged(eq(1), eq(10), eq("test"), isNull(), isNull(), isNull());
    }

    /**
     * 测试获取用户列表权限验证失败场景
     * 验证非管理员角色无法获取用户列表
     */
    @Test
    @DisplayName("获取用户列表 - 权限验证失败")
    void testGetUserList_PermissionDenied() throws Exception {
        // 模拟非管理员角色
        when(UserHolder.getRole()).thenReturn("user");

        AdminUserQueryRequest request = new AdminUserQueryRequest();
        request.setPage(1);
        request.setPageSize(10);

        // 执行测试
        mockMvc.perform(post("/admin/userlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无权限访问，仅管理员可操作"));

        // 验证Service方法未被调用
        verify(adminService, never()).getAllUsersPaged(anyInt(), anyInt(), anyString(), anyString(), any(), any());
    }

    /**
     * 测试获取骑手列表成功场景
     * 验证管理员能够成功获取骑手列表
     */
    @Test
    @DisplayName("获取骑手列表 - 成功")
    void testGetRiderList_Success() throws Exception {
        // 准备测试数据
        AdminRiderQueryRequest request = new AdminRiderQueryRequest();
        request.setKeyword("rider");
        request.setPage(1);
        request.setPageSize(10);

        List<Rider> mockRiders = Arrays.asList(
                createMockRider(1L, "rider1", 1),
                createMockRider(2L, "rider2", 0)
        );

        // 模拟Service层行为
        when(adminService.getAllRidersPaged(anyInt(), anyInt(), anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(mockRiders);

        // 执行测试
        mockMvc.perform(post("/admin/riderlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.riders").isArray())
                .andExpect(jsonPath("$.data.riders.length()").value(2));

        // 验证Service方法调用
        verify(adminService).getAllRidersPaged(anyInt(), anyInt(), anyString(), any(), any(), any(), any(), any(), any());
    }

    /**
     * 测试获取商家列表成功场景
     * 验证管理员能够成功获取商家列表
     */
    @Test
    @DisplayName("获取商家列表 - 成功")
    void testGetMerchantList_Success() throws Exception {
        // 准备测试数据
        AdminMerchantQueryRequest request = new AdminMerchantQueryRequest();
        request.setKeyword("merchant");
        request.setPage(1);
        request.setPageSize(10);

        List<Merchant> mockMerchants = Arrays.asList(
                createMockMerchant(1L, "merchant1"),
                createMockMerchant(2L, "merchant2")
        );

        // 模拟Service层行为
        when(adminService.getAllMerchantsPaged(anyInt(), anyInt(), anyString(), any(), any()))
                .thenReturn(mockMerchants);

        // 执行测试
        mockMvc.perform(post("/admin/merchantlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.merchants").isArray())
                .andExpect(jsonPath("$.data.merchants.length()").value(2));

        // 验证Service方法调用
        verify(adminService).getAllMerchantsPaged(anyInt(), anyInt(), anyString(), any(), any());
    }

    /**
     * 测试获取店铺列表成功场景
     * 验证管理员能够成功获取店铺列表
     */
    @Test
    @DisplayName("获取店铺列表 - 成功")
    void testGetStoreList_Success() throws Exception {
        // 准备测试数据
        AdminStoreQueryRequest request = new AdminStoreQueryRequest();
        request.setKeyword("store");
        request.setType("restaurant");
        request.setPage(1);
        request.setPageSize(10);

        List<Store> mockStores = Arrays.asList(
                createMockStore(1L, "store1", "restaurant"),
                createMockStore(2L, "store2", "cafe")
        );

        // 模拟Service层行为
        when(adminService.getAllStoresPaged(anyInt(), anyInt(), anyString(), anyString(), anyInt(), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockStores);

        // 执行测试
        mockMvc.perform(post("/admin/storelist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stores").isArray())
                .andExpect(jsonPath("$.data.stores.length()").value(2));

        // 验证Service方法调用
        verify(adminService).getAllStoresPaged(anyInt(), anyInt(), anyString(), anyString(), anyInt(), any(BigDecimal.class), any(BigDecimal.class));
    }

    /**
     * 测试获取商品列表成功场景
     * 验证管理员能够成功获取商品列表
     */
    @Test
    @DisplayName("获取商品列表 - 成功")
    void testGetProductList_Success() throws Exception {
        // 准备测试数据
        AdminProductQueryRequest request = new AdminProductQueryRequest();
        request.setStoreId(1L);
        request.setPage(1);
        request.setPageSize(10);

        List<Product> mockProducts = Arrays.asList(
                createMockProduct(1L, "product1", 1L),
                createMockProduct(2L, "product2", 1L)
        );

        Page<Product> mockPage = new Page<>();
        mockPage.setList(mockProducts);
        mockPage.setCurrPage(1);
        mockPage.setPageSize(10);
        mockPage.setCount(2);
        mockPage.setPageCount(1);

        // 模拟Service层行为
        when(productService.getProductsByStore(1L, 1, 10)).thenReturn(mockPage);

        // 执行测试
        mockMvc.perform(post("/admin/productlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products.length()").value(2));

        // 验证Service方法调用
        verify(productService).getProductsByStore(1L, 1, 10);
    }

    /**
     * 测试删除操作成功场景
     * 验证管理员能够成功删除用户
     */
    @Test
    @DisplayName("删除操作 - 删除用户成功")
    void testDelete_DeleteUser_Success() throws Exception {
        // 准备测试数据
        AdminDeleteRequest request = new AdminDeleteRequest();
        request.setUserName("testuser");

        // 模拟Service层行为
        when(adminService.deleteUserByUsername("testuser")).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/admin/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("删除成功"));

        // 验证Service方法调用
        verify(adminService).deleteUserByUsername("testuser");
    }

    /**
     * 测试删除操作失败场景
     * 验证删除操作失败时的错误处理
     */
    @Test
    @DisplayName("删除操作 - 删除失败")
    void testDelete_Failure() throws Exception {
        // 准备测试数据
        AdminDeleteRequest request = new AdminDeleteRequest();
        request.setUserName("nonexistentuser");

        // 模拟Service层行为
        when(adminService.deleteUserByUsername("nonexistentuser")).thenReturn(false);

        // 执行测试
        mockMvc.perform(delete("/admin/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败：用户删除失败"));

        // 验证Service方法调用
        verify(adminService).deleteUserByUsername("nonexistentuser");
    }

    /**
     * 测试删除操作参数验证失败场景
     * 验证未提供删除目标时的错误处理
     */
    @Test
    @DisplayName("删除操作 - 参数验证失败")
    void testDelete_NoTargetProvided() throws Exception {
        // 准备测试数据（所有字段都为null）
        AdminDeleteRequest request = new AdminDeleteRequest();

        // 执行测试
        mockMvc.perform(delete("/admin/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("至少提供一个删除目标"));

        // 验证Service方法未被调用
        verify(adminService, never()).deleteUserByUsername(anyString());
    }

    /**
     * 测试获取订单列表成功场景
     * 验证管理员能够成功获取订单列表
     */
    @Test
    @DisplayName("获取订单列表 - 成功")
    void testGetOrderList_Success() throws Exception {
        // 准备测试数据
        AdminOrderQueryRequest request = new AdminOrderQueryRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setUserId(1L);
        request.setStoreId(1L);

        List<Order> mockOrders = Arrays.asList(
                createMockOrder(1L, 1L, 1L),
                createMockOrder(2L, 1L, 2L)
        );

        // 模拟Service层行为
        when(adminService.getAllOrdersPaged(anyLong(), anyLong(), anyLong(), anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt()))
                .thenReturn(mockOrders);

        // 执行测试
        mockMvc.perform(post("/admin/orderlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders.length()").value(2));

        // 验证Service方法调用
        verify(adminService).getAllOrdersPaged(anyLong(), anyLong(), anyLong(), anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt());
    }

    /**
     * 测试获取评论列表成功场景
     * 验证管理员能够成功获取评论列表
     */
    @Test
    @DisplayName("获取评论列表 - 成功")
    void testGetReviewList_Success() throws Exception {
        // 准备测试数据
        AdminReviewQueryRequest request = new AdminReviewQueryRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setUserId(1L);
        request.setStoreId(1L);

        List<Review> mockReviews = Arrays.asList(
                createMockReview(1L, 1L, 1L),
                createMockReview(2L, 1L, 2L)
        );

        // 模拟Service层行为
        when(adminService.getReviewsByCondition(anyLong(), anyLong(), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt(), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockReviews);

        // 执行测试
        mockMvc.perform(post("/admin/reviewlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reviews").isArray())
                .andExpect(jsonPath("$.data.reviews.length()").value(2));

        // 验证Service方法调用
        verify(adminService).getReviewsByCondition(anyLong(), anyLong(), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt(), any(BigDecimal.class), any(BigDecimal.class));
    }

    /**
     * 测试搜索店铺和商品成功场景
     * 验证管理员能够成功搜索店铺和商品
     */
    @Test
    @DisplayName("搜索店铺和商品 - 成功")
    void testSearchStoreAndProduct_Success() throws Exception {
        // 准备测试数据
        AdminSearchRequest request = new AdminSearchRequest();
        request.setKeyWord("test");

        List<Map<String, Object>> mockResults = Arrays.asList(
                createMockSearchResult(1L, "store1", Map.of("product1", 1L)),
                createMockSearchResult(2L, "store2", Map.of("product2", 2L))
        );

        // 模拟Service层行为
        when(adminService.searchStoreAndProductByKeyword("test")).thenReturn(mockResults);

        // 执行测试
        mockMvc.perform(post("/admin/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.results").isArray())
                .andExpect(jsonPath("$.data.results.length()").value(2));

        // 验证Service方法调用
        verify(adminService).searchStoreAndProductByKeyword("test");
    }

    /**
     * 测试搜索店铺和商品关键词为空场景
     * 验证关键词为空时的错误处理
     */
    @Test
    @DisplayName("搜索店铺和商品 - 关键词为空")
    void testSearchStoreAndProduct_EmptyKeyword() throws Exception {
        // 准备测试数据
        AdminSearchRequest request = new AdminSearchRequest();
        request.setKeyWord("");

        // 执行测试
        mockMvc.perform(post("/admin/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("关键词不能为空"));

        // 验证Service方法未被调用
        verify(adminService, never()).searchStoreAndProductByKeyword(anyString());
    }

    /**
     * 测试根据ID搜索订单成功场景
     * 验证管理员能够成功根据ID搜索订单
     */
    @Test
    @DisplayName("根据ID搜索订单 - 成功")
    void testSearchOrderById_Success() throws Exception {
        // 准备测试数据
        AdminSearchOrderByIdRequest request = new AdminSearchOrderByIdRequest();
        request.setOrderId(1L);

        Order mockOrder = createMockOrder(1L, 1L, 1L);

        // 模拟Service层行为
        when(adminService.getOrderById(1L)).thenReturn(mockOrder);

        // 执行测试
        mockMvc.perform(post("/admin/search-order-by-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.order").exists());

        // 验证Service方法调用
        verify(adminService).getOrderById(1L);
    }

    /**
     * 测试根据ID搜索评论成功场景
     * 验证管理员能够成功根据ID搜索评论
     */
    @Test
    @DisplayName("根据ID搜索评论 - 成功")
    void testSearchReviewById_Success() throws Exception {
        // 准备测试数据
        AdminSearchReviewByIdRequest request = new AdminSearchReviewByIdRequest();
        request.setReviewId(1L);

        Review mockReview = createMockReview(1L, 1L, 1L);

        // 模拟Service层行为
        when(adminService.getReviewById(1L)).thenReturn(mockReview);

        // 执行测试
        mockMvc.perform(post("/admin/search-review-by-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.review").exists());

        // 验证Service方法调用
        verify(adminService).getReviewById(1L);
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建模拟用户对象
     * 
     * @param id 用户ID
     * @param username 用户名
     * @param gender 性别
     * @return 模拟的用户对象
     */
    private User createMockUser(Long id, String username, String gender) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setGender(gender);
        user.setPhone("1234567890");
        user.setDescription("Test user description");
        user.setAvatar("test-avatar.jpg");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * 创建模拟骑手对象
     * 
     * @param id 骑手ID
     * @param username 用户名
     * @param status 状态
     * @return 模拟的骑手对象
     */
    private Rider createMockRider(Long id, String username, Integer status) {
        Rider rider = new Rider();
        rider.setId(id);
        rider.setUsername(username);
        rider.setOrderStatus(status);
        rider.setPhone("1234567890");
        rider.setBalance(100L);
        rider.setDispatchMode(1);
        rider.setAvatar("rider-avatar.jpg");
        rider.setCreatedAt(LocalDateTime.now());
        return rider;
    }

    /**
     * 创建模拟商家对象
     * 
     * @param id 商家ID
     * @param username 用户名
     * @return 模拟的商家对象
     */
    private Merchant createMockMerchant(Long id, String username) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setUsername(username);
        merchant.setPhone("1234567890");
        merchant.setAvatar("merchant-avatar.jpg");
        merchant.setCreatedAt(LocalDateTime.now());
        return merchant;
    }

    /**
     * 创建模拟店铺对象
     * 
     * @param id 店铺ID
     * @param name 店铺名称
     * @param type 店铺类型
     * @return 模拟的店铺对象
     */
    private Store createMockStore(Long id, String name, String type) {
        Store store = new Store();
        store.setId(id);
        store.setName(name);
        store.setType(type);
        store.setDescription("Test store description");
        store.setLocation("Test location");
        store.setRating(BigDecimal.valueOf(4.5));
        store.setStatus(1);
        store.setCreatedAt(LocalDateTime.now());
        return store;
    }

    /**
     * 创建模拟商品对象
     * 
     * @param id 商品ID
     * @param name 商品名称
     * @param storeId 店铺ID
     * @return 模拟的商品对象
     */
    private Product createMockProduct(Long id, String name, Long storeId) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setStoreId(storeId);
        product.setDescription("Test product description");
        product.setPrice(BigDecimal.valueOf(29.99));
        product.setCategory("food");
        product.setStock(100);
        product.setStatus(1);
        return product;
    }

    /**
     * 创建模拟订单对象
     * 
     * @param id 订单ID
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 模拟的订单对象
     */
    private Order createMockOrder(Long id, Long userId, Long storeId) {
        Order order = new Order();
        order.setId(id);
        order.setUserId(userId);
        order.setStoreId(storeId);
        order.setRiderId(1L);
        order.setStatus(1);
        order.setTotalPrice(BigDecimal.valueOf(59.99));
        order.setCreatedAt(LocalDateTime.now());
        order.setDeadline(LocalDateTime.now().plusHours(1));
        return order;
    }

    /**
     * 创建模拟评论对象
     * 
     * @param id 评论ID
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 模拟的评论对象
     */
    private Review createMockReview(Long id, Long userId, Long storeId) {
        Review review = new Review();
        review.setId(id);
        review.setUserId(userId);
        review.setStoreId(storeId);
        review.setProductId(1L);
        review.setRating(BigDecimal.valueOf(4.5));
        review.setComment("Great product!");
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    /**
     * 创建模拟搜索结果对象
     * 
     * @param storeId 店铺ID
     * @param storeName 店铺名称
     * @param products 商品映射
     * @return 模拟的搜索结果对象
     */
    private Map<String, Object> createMockSearchResult(Long storeId, String storeName, Map<String, Long> products) {
        Map<String, Object> result = new HashMap<>();
        result.put("store_id", storeId);
        result.put("store_name", storeName);
        result.put("products", products);
        return result;
    }
}