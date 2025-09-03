/**
 * StoreController单元测试类
 * 测试店铺相关的所有接口功能，包括创建、查询、更新、删除等操作
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.store.*;
import org.demo.baoleme.dto.request.user.UserGetFavoriteStoresRequest;
import org.demo.baoleme.dto.request.user.UserGetProductByConditionRequest;
import org.demo.baoleme.dto.response.user.UserFavoriteResponse;
import org.demo.baoleme.dto.response.user.UserGetProductResponse;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.StoreService;
import org.demo.baoleme.service.UserService;
import org.demo.baoleme.common.JwtInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.demo.baoleme.config.TestConfig;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * StoreController测试类
 * 使用MockMvc进行Web层测试，模拟HTTP请求和响应
 * 使用Mockito模拟Service层依赖
 */
@SpringBootTest(classes = org.demo.baoleme.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class StoreControllerTest extends BaseControllerTest {

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
     * 模拟的StoreService依赖
     */
    @MockBean
    private StoreService storeService;

    /**
     * 模拟的UserService依赖
     */
    @MockBean
    private UserService userService;

    /**
     * 模拟的StoreMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.StoreMapper storeMapper;

    /**
     * 模拟的ProductMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.ProductMapper productMapper;

    /**
     * 模拟的UserMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.UserMapper userMapper;

    /**
     * 模拟的MerchantMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.MerchantMapper merchantMapper;

    /**
     * 模拟的OrderMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.OrderMapper orderMapper;

    /**
     * 模拟的CouponMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.CouponMapper couponMapper;

    /**
     * 模拟的RiderMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.RiderMapper riderMapper;

    /**
     * 模拟的SaleMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.SaleMapper saleMapper;

    /**
     * 模拟的JwtInterceptor依赖，用于避免JWT认证问题
     */
    @MockBean
    private JwtInterceptor jwtInterceptor;

    /**
     * 测试用店铺数据
     */
    private Store testStore;
    
    /**
     * 测试用JWT令牌
     */
    private static final String TEST_TOKEN = "test-jwt-token";
    
    /**
     * 测试用商户ID
     */
    private static final Long TEST_MERCHANT_ID = 1L;
    
    /**
     * 测试用店铺ID
     */
    private static final Long TEST_STORE_ID = 1L;
    
    /**
     * 测试用店铺名称
     */
    private static final String TEST_STORE_NAME = "测试餐厅";
    
    /**
     * 测试用店铺描述
     */
    private static final String TEST_DESCRIPTION = "美味的测试餐厅";
    
    /**
     * 测试用店铺位置
     */
    private static final String TEST_LOCATION = "测试街道123号";
    
    /**
     * 测试用店铺类型
     */
    private static final String TEST_TYPE = "中餐";

    /**
     * 测试前的初始化设置
     * 准备测试数据和模拟对象行为
     */
    @BeforeEach
    void setUp() throws Exception {
        // 配置JwtInterceptor Mock行为
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        
        // 初始化测试店铺数据
        testStore = new Store();
        testStore.setId(TEST_STORE_ID);
        testStore.setMerchantId(TEST_MERCHANT_ID);
        testStore.setName(TEST_STORE_NAME);
        testStore.setDescription(TEST_DESCRIPTION);
        testStore.setLocation(TEST_LOCATION);
        testStore.setType(TEST_TYPE);
        testStore.setStatus(1); // 营业中
        testStore.setImage("http://example.com/store.jpg");
        testStore.setCreatedAt(LocalDateTime.now());
    }

    // ==================== 创建店铺测试 ====================

    /**
     * 测试创建店铺成功场景
     * 验证能够成功创建店铺
     */
    @Test
    @DisplayName("创建店铺 - 成功")
    void testCreateStore_Success() throws Exception {
        // 准备测试数据
        StoreCreateRequest request = new StoreCreateRequest();
        request.setName(TEST_STORE_NAME);
        request.setDescription(TEST_DESCRIPTION);
        request.setLocation(TEST_LOCATION);
        request.setType(TEST_TYPE);
        request.setImage("http://example.com/store.jpg");

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);
        when(storeService.createStore(any(Store.class))).thenReturn(testStore);

        // 执行测试
        mockMvc.perform(post("/store/create")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(TEST_STORE_ID));

        // 验证Service方法调用
        verify(storeService).createStore(any(Store.class));
    }

    /**
     * 测试创建店铺失败场景（参数校验不通过）
     * 验证创建失败时的处理
     */
    @Test
    @DisplayName("创建店铺 - 参数校验不通过")
    void testCreateStore_ValidationFailed() throws Exception {
        // 准备测试数据
        StoreCreateRequest request = new StoreCreateRequest();
        request.setName(TEST_STORE_NAME);
        request.setDescription(TEST_DESCRIPTION);
        request.setLocation(TEST_LOCATION);
        request.setType(TEST_TYPE);

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);
        when(storeService.createStore(any(Store.class))).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/store/create")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺创建失败，参数校验不通过"));

        // 验证Service方法调用
        verify(storeService).createStore(any(Store.class));
    }

    /**
     * 测试创建店铺参数验证失败场景（店铺名为空）
     * 验证必填参数校验
     */
    @Test
    @DisplayName("创建店铺 - 店铺名为空")
    void testCreateStore_EmptyName() throws Exception {
        // 准备测试数据（店铺名为空）
        StoreCreateRequest request = new StoreCreateRequest();
        request.setName(""); // 空店铺名
        request.setDescription(TEST_DESCRIPTION);
        request.setLocation(TEST_LOCATION);
        request.setType(TEST_TYPE);

        // 执行测试
        mockMvc.perform(post("/store/create")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 验证Service方法未被调用
        verify(storeService, never()).createStore(any(Store.class));
    }

    // ==================== 查询店铺列表测试 ====================

    /**
     * 测试查询店铺列表成功场景
     * 验证能够成功查询店铺列表
     */
    @Test
    @DisplayName("查询店铺列表 - 成功")
    void testListStore_Success() throws Exception {
        // 准备测试数据
        StoreListRequest request = new StoreListRequest();
        request.setPage(1);
        request.setPageSize(10);

        List<Store> stores = Arrays.asList(testStore);

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);
        when(storeService.getStoresByMerchant(TEST_MERCHANT_ID, 1, 10)).thenReturn(stores);

        // 执行测试
        mockMvc.perform(post("/store/list")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stores").isArray())
                .andExpect(jsonPath("$.data.stores[0].store_id").value(TEST_STORE_ID))
                .andExpect(jsonPath("$.data.stores[0].name").value(TEST_STORE_NAME))
                .andExpect(jsonPath("$.data.current_page").value(1));

        // 验证Service方法调用
        verify(storeService).getStoresByMerchant(TEST_MERCHANT_ID, 1, 10);
    }

    /**
     * 测试查询店铺列表场景（空列表）
     * 验证无店铺时的处理
     */
    @Test
    @DisplayName("查询店铺列表 - 空列表")
    void testListStore_EmptyList() throws Exception {
        // 准备测试数据
        StoreListRequest request = new StoreListRequest();
        request.setPage(1);
        request.setPageSize(10);

        List<Store> emptyStores = Arrays.asList();

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);
        when(storeService.getStoresByMerchant(TEST_MERCHANT_ID, 1, 10)).thenReturn(emptyStores);

        // 执行测试
        mockMvc.perform(post("/store/list")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stores").isArray())
                .andExpect(jsonPath("$.data.stores").isEmpty())
                .andExpect(jsonPath("$.data.current_page").value(1));

        // 验证Service方法调用
        verify(storeService).getStoresByMerchant(TEST_MERCHANT_ID, 1, 10);
    }

    // ==================== 查看店铺详情测试 ====================

    /**
     * 测试查看店铺详情成功场景
     * 验证能够成功查看店铺详情
     */
    @Test
    @DisplayName("查看店铺详情 - 成功")
    void testGetStoreById_Success() throws Exception {
        // 准备测试数据
        StoreViewInfoRequest request = new StoreViewInfoRequest();
        request.setStoreId(TEST_STORE_ID);

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(true);
        when(storeService.getStoreById(TEST_STORE_ID)).thenReturn(testStore);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(post("/store/view")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(TEST_STORE_NAME))
                .andExpect(jsonPath("$.data.description").value(TEST_DESCRIPTION))
                .andExpect(jsonPath("$.data.location").value(TEST_LOCATION));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService).getStoreById(TEST_STORE_ID);
    }

    /**
     * 测试查看店铺详情失败场景（店铺不属于当前商户）
     * 验证权限校验
     */
    @Test
    @DisplayName("查看店铺详情 - 店铺不属于当前商户")
    void testGetStoreById_NotOwner() throws Exception {
        // 准备测试数据
        StoreViewInfoRequest request = new StoreViewInfoRequest();
        request.setStoreId(TEST_STORE_ID);

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(false);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(post("/store/view")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺不属于您"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService, never()).getStoreById(anyLong());
    }

    /**
     * 测试查看店铺详情失败场景（店铺ID不存在）
     * 验证无效ID处理
     */
    @Test
    @DisplayName("查看店铺详情 - 店铺ID不存在")
    void testGetStoreById_NotExists() throws Exception {
        // 准备测试数据
        StoreViewInfoRequest request = new StoreViewInfoRequest();
        request.setStoreId(TEST_STORE_ID);

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(true);
        when(storeService.getStoreById(TEST_STORE_ID)).thenReturn(null);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(post("/store/view")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺ID不存在"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService).getStoreById(TEST_STORE_ID);
    }

    // ==================== 更新店铺测试 ====================

    /**
     * 测试更新店铺成功场景
     * 验证能够成功更新店铺信息
     */
    @Test
    @DisplayName("更新店铺 - 成功")
    void testUpdateStore_Success() throws Exception {
        // 准备测试数据
        StoreUpdateRequest request = new StoreUpdateRequest();
        request.setId(TEST_STORE_ID);
        request.setName("更新后的餐厅名");
        request.setDescription("更新后的描述");
        request.setLocation("更新后的地址");

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(true);
        when(storeService.updateStore(any(Store.class))).thenReturn(true);
        when(storeService.getStoreById(TEST_STORE_ID)).thenReturn(testStore);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(put("/store/update")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(TEST_STORE_NAME));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService).updateStore(any(Store.class));
        verify(storeService).getStoreById(TEST_STORE_ID);
    }

    /**
     * 测试更新店铺失败场景（店铺不属于当前商户）
     * 验证权限校验
     */
    @Test
    @DisplayName("更新店铺 - 店铺不属于当前商户")
    void testUpdateStore_NotOwner() throws Exception {
        // 准备测试数据
        StoreUpdateRequest request = new StoreUpdateRequest();
        request.setId(TEST_STORE_ID);
        request.setName("更新后的餐厅名");

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(false);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(put("/store/update")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺不属于您"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService, never()).updateStore(any(Store.class));
    }

    /**
     * 测试更新店铺失败场景（更新失败）
     * 验证更新失败时的处理
     */
    @Test
    @DisplayName("更新店铺 - 更新失败")
    void testUpdateStore_UpdateFailed() throws Exception {
        // 准备测试数据
        StoreUpdateRequest request = new StoreUpdateRequest();
        request.setId(TEST_STORE_ID);
        request.setName("更新后的餐厅名");

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(true);
        when(storeService.updateStore(any(Store.class))).thenReturn(false);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(put("/store/update")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺信息更新失败"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService).updateStore(any(Store.class));
    }

    // ==================== 切换店铺状态测试 ====================

    /**
     * 测试切换店铺状态成功场景
     * 验证能够成功切换店铺状态
     */
    @Test
    @DisplayName("切换店铺状态 - 成功")
    void testToggleStoreStatus_Success() throws Exception {
        // 准备测试数据
        StoreUpdateRequest request = new StoreUpdateRequest();
        request.setId(TEST_STORE_ID);
        request.setStatus(0); // 暂停营业

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(true);
        when(storeService.toggleStoreStatus(TEST_STORE_ID, 0)).thenReturn(true);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(put("/store/status")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("店铺状态更新成功"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService).toggleStoreStatus(TEST_STORE_ID, 0);
    }

    /**
     * 测试切换店铺状态失败场景（非法状态值）
     * 验证状态值校验
     */
    @Test
    @DisplayName("切换店铺状态 - 非法状态值")
    void testToggleStoreStatus_InvalidStatus() throws Exception {
        // 准备测试数据
        StoreUpdateRequest request = new StoreUpdateRequest();
        request.setId(TEST_STORE_ID);
        request.setStatus(2); // 非法状态值

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(true);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(put("/store/status")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("状态值必须是0或1"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService, never()).toggleStoreStatus(anyLong(), anyInt());
    }

    /**
     * 测试切换店铺状态失败场景（店铺不属于当前商户）
     * 验证权限校验
     */
    @Test
    @DisplayName("切换店铺状态 - 店铺不属于当前商户")
    void testToggleStoreStatus_NotOwner() throws Exception {
        // 准备测试数据
        StoreUpdateRequest request = new StoreUpdateRequest();
        request.setId(TEST_STORE_ID);
        request.setStatus(0);

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(false);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(put("/store/status")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺不属于您"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService, never()).toggleStoreStatus(anyLong(), anyInt());
    }

    // ==================== 删除店铺测试 ====================

    /**
     * 测试删除店铺成功场景
     * 验证能够成功删除店铺
     */
    @Test
    @DisplayName("删除店铺 - 成功")
    void testDeleteStore_Success() throws Exception {
        // 准备测试数据
        StoreDeleteRequest request = new StoreDeleteRequest();
        request.setStoreId(TEST_STORE_ID);

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(true);
        when(storeService.deleteStore(TEST_STORE_ID)).thenReturn(true);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(delete("/store/delete")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("店铺数据已删除"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService).deleteStore(TEST_STORE_ID);
    }

    /**
     * 测试删除店铺失败场景（店铺不属于当前商户）
     * 验证权限校验
     */
    @Test
    @DisplayName("删除店铺 - 店铺不属于当前商户")
    void testDeleteStore_NotOwner() throws Exception {
        // 准备测试数据
        StoreDeleteRequest request = new StoreDeleteRequest();
        request.setStoreId(TEST_STORE_ID);

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(false);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(delete("/store/delete")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺不属于您"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService, never()).deleteStore(anyLong());
    }

    /**
     * 测试删除店铺失败场景（删除失败）
     * 验证删除失败时的处理
     */
    @Test
    @DisplayName("删除店铺 - 删除失败")
    void testDeleteStore_DeleteFailed() throws Exception {
        // 准备测试数据
        StoreDeleteRequest request = new StoreDeleteRequest();
        request.setStoreId(TEST_STORE_ID);

        // 模拟Service层行为
        when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID)).thenReturn(true);
        when(storeService.deleteStore(TEST_STORE_ID)).thenReturn(false);

        // 模拟UserHolder行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_MERCHANT_ID);

        // 执行测试
        mockMvc.perform(delete("/store/delete")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除操作失败，店铺可能不存在"));

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_MERCHANT_ID);
        verify(storeService).deleteStore(TEST_STORE_ID);
    }

    // ==================== 用户浏览店铺测试 ====================

    /**
     * 测试用户浏览店铺成功场景
     * 验证用户能够成功浏览店铺
     */
    @Test
    @DisplayName("用户浏览店铺 - 成功")
    void testGetShops_Success() throws Exception {
        // 准备测试数据
        UserGetFavoriteStoresRequest request = new UserGetFavoriteStoresRequest();
        request.setType("中餐");
        request.setDistance(new BigDecimal("5.0"));
        request.setWishPrice(new BigDecimal("50.0"));
        request.setStartRating(new BigDecimal("4.0"));
        request.setEndRating(new BigDecimal("5.0"));
        request.setPage(1);
        request.setPageSize(10);

        UserFavoriteResponse favoriteResponse = new UserFavoriteResponse();
        favoriteResponse.setStoreId(TEST_STORE_ID);
        favoriteResponse.setName(TEST_STORE_NAME);
        List<UserFavoriteResponse> stores = Arrays.asList(favoriteResponse);

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(1L);
        when(userService.getStores(eq(1L), eq("中餐"), any(BigDecimal.class), any(BigDecimal.class), 
                any(BigDecimal.class), any(BigDecimal.class), eq(1), eq(10))).thenReturn(stores);

        // 执行测试
        mockMvc.perform(post("/store/user-view-stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].store_id").value(TEST_STORE_ID))
                .andExpect(jsonPath("$.data[0].name").value(TEST_STORE_NAME));

        // 验证Service方法调用
        verify(userService).getStores(eq(1L), eq("中餐"), any(BigDecimal.class), any(BigDecimal.class), 
                any(BigDecimal.class), any(BigDecimal.class), eq(1), eq(10));
    }

    /**
     * 测试用户浏览店铺场景（空结果）
     * 验证无匹配店铺时的处理
     */
    @Test
    @DisplayName("用户浏览店铺 - 空结果")
    void testGetShops_EmptyResult() throws Exception {
        // 准备测试数据
        UserGetFavoriteStoresRequest request = new UserGetFavoriteStoresRequest();
        request.setType("西餐");
        request.setPage(1);
        request.setPageSize(10);

        List<UserFavoriteResponse> emptyStores = Arrays.asList();

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(1L);
        when(userService.getStores(eq(1L), eq("西餐"), any(), any(), any(), any(), eq(1), eq(10)))
                .thenReturn(emptyStores);

        // 执行测试
        mockMvc.perform(post("/store/user-view-stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        // 验证Service方法调用
        verify(userService).getStores(eq(1L), eq("西餐"), any(), any(), any(), any(), eq(1), eq(10));
    }

    // ==================== 用户浏览商品测试 ====================

    /**
     * 测试用户浏览商品成功场景
     * 验证用户能够成功浏览店铺商品
     */
    @Test
    @DisplayName("用户浏览商品 - 成功")
    void testGetProductsByStore_Success() throws Exception {
        // 准备测试数据
        UserGetProductByConditionRequest request = new UserGetProductByConditionRequest();
        request.setStoreId(TEST_STORE_ID);
        request.setCategory("主食");

        UserGetProductResponse productResponse = new UserGetProductResponse();
        productResponse.setId(1L);
        productResponse.setName("测试商品");
        List<UserGetProductResponse> products = Arrays.asList(productResponse);

        // 模拟Service层行为
        when(userService.getProducts(TEST_STORE_ID, "主食")).thenReturn(products);

        // 执行测试
        mockMvc.perform(post("/store/user-view-products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("测试商品"));

        // 验证Service方法调用
        verify(userService).getProducts(TEST_STORE_ID, "主食");
    }

    /**
     * 测试用户浏览商品场景（空结果）
     * 验证无匹配商品时的处理
     */
    @Test
    @DisplayName("用户浏览商品 - 空结果")
    void testGetProductsByStore_EmptyResult() throws Exception {
        // 准备测试数据
        UserGetProductByConditionRequest request = new UserGetProductByConditionRequest();
        request.setStoreId(TEST_STORE_ID);
        request.setCategory("饮品");

        List<UserGetProductResponse> emptyProducts = Arrays.asList();

        // 模拟Service层行为
        when(userService.getProducts(TEST_STORE_ID, "饮品")).thenReturn(emptyProducts);

        // 执行测试
        mockMvc.perform(post("/store/user-view-products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        // 验证Service方法调用
        verify(userService).getProducts(TEST_STORE_ID, "饮品");
    }

    // ==================== 获取店铺信息测试 ====================

    /**
     * 测试获取店铺信息成功场景
     * 验证能够成功获取店铺信息
     */
    @Test
    @DisplayName("获取店铺信息 - 成功")
    void testGetStoreInfo_Success() throws Exception {
        // 准备测试数据
        StoreInfoRequest request = new StoreInfoRequest();
        request.setId(TEST_STORE_ID);

        // 模拟Service层行为
        when(storeService.getStoreById(TEST_STORE_ID)).thenReturn(testStore);

        // 执行测试
        mockMvc.perform(post("/store/storeInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(TEST_STORE_NAME))
                .andExpect(jsonPath("$.data.description").value(TEST_DESCRIPTION))
                .andExpect(jsonPath("$.data.location").value(TEST_LOCATION));

        // 验证Service方法调用
        verify(storeService).getStoreById(TEST_STORE_ID);
    }

    /**
     * 测试获取店铺信息失败场景（店铺不存在）
     * 验证店铺不存在时的处理
     */
    @Test
    @DisplayName("获取店铺信息 - 店铺不存在")
    void testGetStoreInfo_NotExists() throws Exception {
        // 准备测试数据
        StoreInfoRequest request = new StoreInfoRequest();
        request.setId(999L); // 不存在的店铺ID

        // 模拟Service层行为
        when(storeService.getStoreById(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/store/storeInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());

        // 验证Service方法调用
        verify(storeService).getStoreById(999L);
    }
}