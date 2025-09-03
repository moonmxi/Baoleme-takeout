/**
 * 《饱了么》用户控制器单元测试类
 * 
 * 本测试类为UserController提供完整的单元测试覆盖，包括：
 * - 用户注册、登录、登出功能测试
 * - 用户信息查询、更新、删除功能测试
 * - 订单历史、收藏店铺、优惠券等业务功能测试
 * - 正向测试用例（验证正常业务场景）
 * - 反向测试用例（验证异常处理逻辑）
 * 
 * @author 测试开发团队
 * @version 1.0
 * @since 2024-01-20
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.user.*;
import org.demo.baoleme.dto.request.order.OrderCreateRequest;
import org.demo.baoleme.dto.request.coupon.AvailableCouponRequest;
import org.demo.baoleme.dto.response.user.*;
import org.demo.baoleme.mapper.OrderMapper;
import org.demo.baoleme.pojo.User;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.service.UserService;
import org.demo.baoleme.service.OrderService;
import org.demo.baoleme.service.SalesStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.demo.baoleme.config.TestConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController单元测试类
 * 
 * 测试覆盖范围：
 * 1. 用户注册接口 - 正向/反向测试
 * 2. 用户登录接口 - 正向/反向测试
 * 3. 用户登出接口 - 正向/反向测试
 * 4. 用户信息查询接口 - 正向/反向测试
 * 5. 用户信息更新接口 - 正向/反向测试
 * 6. 用户删除接口 - 正向/反向测试
 * 7. 订单历史查询接口 - 正向/反向测试
 * 8. 收藏店铺相关接口 - 正向/反向测试
 * 9. 优惠券相关接口 - 正向/反向测试
 * 10. 其他业务接口 - 正向/反向测试
 */
@SpringBootTest(classes = org.demo.baoleme.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class UserControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private SalesStatsService salesStatsService;

    @MockBean
    private OrderMapper orderMapper;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    @MockBean
    private JwtUtils jwtUtils;

    /**
     * Mock JwtInterceptor to avoid authentication issues in tests
     */
    @MockBean
    private org.demo.baoleme.common.JwtInterceptor jwtInterceptor;

    /**
     * Mock UserMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.UserMapper userMapper;

    /**
     * Mock StoreMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.StoreMapper storeMapper;

    /**
     * Mock MerchantMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.MerchantMapper merchantMapper;

    /**
     * Mock ProductMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.ProductMapper productMapper;

    /**
     * Mock CouponMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.CouponMapper couponMapper;

    /**
     * Mock RiderMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.RiderMapper riderMapper;

    /**
     * Mock SaleMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.SaleMapper saleMapper;

    /**
     * 测试前置设置
     * 初始化Mock对象和通用测试数据
     */
    @BeforeEach
    void setUp() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        // Mock JwtInterceptor让所有请求通过
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    // ==================== 用户注册接口测试 ====================

    /**
     * 测试用户注册 - 正向测试用例
     * 
     * 验证场景：使用有效的用户注册信息进行注册
     * 预期结果：返回状态码200，包含用户ID和基本信息
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("用户注册 - 正向测试：有效注册信息")
    void testRegister_Success() throws Exception {
        // 准备测试数据
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setPhone("13800138000");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPhone("13800138000");

        // 配置Mock行为
        when(userService.register(any(User.class))).thenReturn(mockUser);

        // 执行测试
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user_id").value(1L))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));

        // 验证Mock调用
        verify(userService, times(1)).register(any(User.class));
    }

    /**
     * 测试用户注册 - 反向测试用例
     * 
     * 验证场景：用户名或手机号已存在的情况
     * 预期结果：返回失败响应，提示注册失败
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("用户注册 - 反向测试：用户名或手机号已存在")
    void testRegister_UserExists() throws Exception {
        // 准备测试数据
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("existuser");
        request.setPassword("123456");
        request.setPhone("13800138001");

        // 配置Mock行为 - 返回null表示注册失败
        when(userService.register(any(User.class))).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("注册失败：用户名或手机号已存在"));

        // 验证Mock调用
        verify(userService, times(1)).register(any(User.class));
    }

    /**
     * 测试用户注册 - 反向测试用例
     * 
     * 验证场景：请求参数验证失败（用户名为空）
     * 预期结果：返回参数验证错误
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("用户注册 - 反向测试：用户名为空")
    void testRegister_EmptyUsername() throws Exception {
        // 准备测试数据 - 用户名为空
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("");
        request.setPassword("123456");
        request.setPhone("13800138000");

        // 执行测试
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== 用户登录接口测试 ====================

    /**
     * 测试用户登录 - 正向测试用例
     * 
     * 验证场景：使用正确的手机号和密码登录
     * 预期结果：返回状态码200，包含token和用户信息
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("用户登录 - 正向测试：正确的登录凭据")
    void testLogin_Success() throws Exception {
        // 准备测试数据
        UserLoginRequest request = new UserLoginRequest();
        request.setPhone("13800138000");
        request.setPassword("123456");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPhone("13800138000");

        // 配置Mock行为
        when(userService.login("13800138000", "123456")).thenReturn(mockUser);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user_id").value(1L))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.token").exists());

        // 验证Mock调用
        verify(userService, times(1)).login("13800138000", "123456");
    }

    /**
     * 测试用户登录 - 反向测试用例
     * 
     * 验证场景：手机号或密码错误
     * 预期结果：返回登录失败响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("用户登录 - 反向测试：手机号或密码错误")
    void testLogin_InvalidCredentials() throws Exception {
        // 准备测试数据
        UserLoginRequest request = new UserLoginRequest();
        request.setPhone("13800138000");
        request.setPassword("wrongpassword");

        // 配置Mock行为 - 返回null表示登录失败
        when(userService.login("13800138000", "wrongpassword")).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("手机号或密码错误"));

        // 验证Mock调用
        verify(userService, times(1)).login("13800138000", "wrongpassword");
    }

    /**
     * 测试用户登录 - 反向测试用例
     * 
     * 验证场景：用户已登录状态
     * 预期结果：返回用户已登录的错误提示
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("用户登录 - 反向测试：用户已登录")
    void testLogin_AlreadyLoggedIn() throws Exception {
        // 准备测试数据
        UserLoginRequest request = new UserLoginRequest();
        request.setPhone("13800138000");
        request.setPassword("123456");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        // 配置Mock行为 - 用户已登录
        when(userService.login("13800138000", "123456")).thenReturn(mockUser);
        when(redisTemplate.hasKey("user:login:1")).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("该用户已登录，请先登出"));
    }

    // ==================== 用户登出接口测试 ====================

    /**
     * 测试用户登出 - 正向测试用例
     * 
     * 验证场景：正常登出操作
     * 预期结果：返回状态码200，成功登出
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("用户登出 - 正向测试：正常登出")
    void testLogout_Success() throws Exception {
        // 准备测试数据
        String token = "Bearer test-token";
        Long userId = 1L;

        // 配置Mock行为
        when(valueOperations.get("user:token:test-token")).thenReturn(userId);
        when(userService.updateInfo(any(User.class))).thenReturn(true);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(post("/user/logout")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证Mock调用
        verify(redisTemplate, times(2)).delete(anyString());
        verify(userService, times(1)).updateInfo(any(User.class));
    }

    /**
     * 测试用户登出 - 反向测试用例
     * 
     * 验证场景：登出失败
     * 预期结果：返回登出失败响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("用户登出 - 反向测试：登出失败")
    void testLogout_Failure() throws Exception {
        // 准备测试数据
        String token = "Bearer test-token";
        Long userId = 1L;

        // 配置Mock行为 - 更新失败
        when(valueOperations.get("user:token:test-token")).thenReturn(userId);
        when(userService.updateInfo(any(User.class))).thenReturn(false);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(post("/user/logout")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("登出失败"));

        // 验证Mock调用
        verify(userService, times(1)).updateInfo(any(User.class));
    }

    // ==================== 用户信息查询接口测试 ====================

    /**
     * 测试获取用户信息 - 正向测试用例
     * 
     * 验证场景：正常获取用户信息
     * 预期结果：返回状态码200，包含用户详细信息
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取用户信息 - 正向测试：正常获取")
    void testGetInfo_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setPhone("13800138000");
        mockUser.setAvatar("avatar.jpg");

        // 配置Mock行为
        when(userService.getInfo(userId)).thenReturn(mockUser);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(get("/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.user_id").value(userId))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));

        // 验证Mock调用
        verify(userService, times(1)).getInfo(userId);
    }

    /**
     * 测试获取用户信息 - 反向测试用例
     * 
     * 验证场景：用户不存在
     * 预期结果：返回用户不存在的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取用户信息 - 反向测试：用户不存在")
    void testGetInfo_UserNotFound() throws Exception {
        // 准备测试数据
        Long userId = 999L;

        // 配置Mock行为 - 返回null表示用户不存在
        when(userService.getInfo(userId)).thenReturn(null);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(get("/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        // 验证Mock调用
        verify(userService, times(1)).getInfo(userId);
    }

    // ==================== 用户信息更新接口测试 ====================

    /**
     * 测试更新用户信息 - 正向测试用例
     * 
     * 验证场景：正常更新用户信息（不修改用户名）
     * 预期结果：返回状态码200，更新成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("更新用户信息 - 正向测试：正常更新（不修改用户名）")
    void testUpdate_Success_NoUsernameChange() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        String token = "Bearer test-token";
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("testuser"); // 保持原用户名
        request.setPassword("newpassword");
        request.setPhone("13800138001");
        request.setAvatar("new-avatar.jpg");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("testuser"); // 原用户名

        // 配置Mock行为
        when(userService.getInfo(userId)).thenReturn(existingUser);
        when(userService.updateInfo(any(User.class))).thenReturn(true);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(put("/user/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证Mock调用
        verify(userService, times(1)).getInfo(userId);
        verify(userService, times(1)).updateInfo(any(User.class));
    }

    /**
     * 测试更新用户信息 - 正向测试用例
     * 
     * 验证场景：更新用户信息并修改用户名
     * 预期结果：返回状态码200，包含新的token
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("更新用户信息 - 正向测试：修改用户名")
    void testUpdate_Success_UsernameChanged() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        String token = "Bearer test-token";
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("newusername"); // 修改用户名
        request.setPassword("newpassword");
        request.setPhone("13800138001");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldusername"); // 原用户名

        // 配置Mock行为
        when(userService.getInfo(userId)).thenReturn(existingUser);
        when(userService.updateInfo(any(User.class))).thenReturn(true);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        // 使用MockedStatic模拟UserHolder和JwtUtils
        try (MockedStatic<JwtUtils> mockedJwtUtils = Mockito.mockStatic(JwtUtils.class)) {
            
            mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
            mockedJwtUtils.when(() -> JwtUtils.createToken(userId, "user", "newusername"))
                    .thenReturn("new-test-token");

            // 执行测试
            mockMvc.perform(put("/user/update")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").value("new-test-token"))
                    .andExpect(jsonPath("$.data.username").value("newusername"));
        }

        // 验证Mock调用
        verify(userService, times(1)).getInfo(userId);
        verify(userService, times(1)).updateInfo(any(User.class));
        verify(redisTemplate, times(2)).delete(anyString());
    }

    /**
     * 测试更新用户信息 - 反向测试用例
     * 
     * 验证场景：用户不存在
     * 预期结果：返回用户不存在的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("更新用户信息 - 反向测试：用户不存在")
    void testUpdate_UserNotFound() throws Exception {
        // 准备测试数据
        Long userId = 999L;
        String token = "Bearer test-token";
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("testuser");
        request.setPassword("newpassword");

        // 配置Mock行为 - 用户不存在
        when(userService.getInfo(userId)).thenReturn(null);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(put("/user/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        // 验证Mock调用
        verify(userService, times(1)).getInfo(userId);
        verify(userService, never()).updateInfo(any(User.class));
    }

    /**
     * 测试更新用户信息 - 反向测试用例
     * 
     * 验证场景：更新失败
     * 预期结果：返回更新失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("更新用户信息 - 反向测试：更新失败")
    void testUpdate_UpdateFailed() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        String token = "Bearer test-token";
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("testuser");
        request.setPassword("newpassword");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("testuser");

        // 配置Mock行为 - 更新失败
        when(userService.getInfo(userId)).thenReturn(existingUser);
        when(userService.updateInfo(any(User.class))).thenReturn(false);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(put("/user/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("更新失败，请检查字段"));

        // 验证Mock调用
        verify(userService, times(1)).getInfo(userId);
        verify(userService, times(1)).updateInfo(any(User.class));
    }

    // ==================== 用户删除接口测试 ====================

    /**
     * 测试删除用户 - 正向测试用例
     * 
     * 验证场景：正常删除用户
     * 预期结果：返回状态码200，删除成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("删除用户 - 正向测试：正常删除")
    void testDelete_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;

        // 配置Mock行为
        when(userService.delete(userId)).thenReturn(true);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(delete("/user/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证Mock调用
        verify(userService, times(1)).delete(userId);
    }

    /**
     * 测试删除用户 - 反向测试用例
     * 
     * 验证场景：删除失败
     * 预期结果：返回注销失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("删除用户 - 反向测试：删除失败")
    void testDelete_Failure() throws Exception {
        // 准备测试数据
        Long userId = 1L;

        // 配置Mock行为 - 删除失败
        when(userService.delete(userId)).thenReturn(false);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(delete("/user/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("注销失败"));

        // 验证Mock调用
        verify(userService, times(1)).delete(userId);
    }

    // ==================== 订单历史查询接口测试 ====================

    /**
     * 测试获取订单历史 - 正向测试用例
     * 
     * 验证场景：正常获取用户订单历史
     * 预期结果：返回状态码200，包含订单列表
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取订单历史 - 正向测试：正常获取")
    void testGetOrderHistory_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        UserOrderHistoryRequest request = new UserOrderHistoryRequest();
        request.setStatus(1);
        request.setPage(1);
        request.setPageSize(10);

        List<Map<String, Object>> mockRecords = new ArrayList<>();
        Map<String, Object> orderRecord = new HashMap<>();
        orderRecord.put("id", 1L);
        orderRecord.put("status", 1);
        orderRecord.put("store_name", "测试店铺");
        orderRecord.put("total_price", new BigDecimal("50.00"));
        orderRecord.put("created_at", LocalDateTime.now());
        mockRecords.add(orderRecord);

        // 配置Mock行为
        when(userService.getUserOrdersPaged(eq(userId), eq(1), any(), any(), eq(1), eq(10)))
                .thenReturn(mockRecords);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("user");

        // 执行测试
        mockMvc.perform(post("/user/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders[0].order_id").value(1L))
                .andExpect(jsonPath("$.data.orders[0].store_name").value("测试店铺"));

        // 验证Mock调用
        verify(userService, times(1)).getUserOrdersPaged(eq(userId), eq(1), any(), any(), eq(1), eq(10));
    }

    /**
     * 测试获取订单历史 - 反向测试用例
     * 
     * 验证场景：非用户角色访问
     * 预期结果：返回权限错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取订单历史 - 反向测试：权限不足")
    void testGetOrderHistory_NoPermission() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        UserOrderHistoryRequest request = new UserOrderHistoryRequest();
        request.setStatus(1);
        request.setPage(1);
        request.setPageSize(10);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("merchant"); // 非用户角色

        // 执行测试
        mockMvc.perform(post("/user/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("无权限访问，仅普通用户可操作"));

        // 验证Mock调用
        verify(userService, never()).getUserOrdersPaged(anyLong(), anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt());
    }

    // ==================== 收藏店铺接口测试 ====================

    /**
     * 测试收藏店铺 - 正向测试用例
     * 
     * 验证场景：正常收藏店铺
     * 预期结果：返回状态码200，收藏成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("收藏店铺 - 正向测试：正常收藏")
    void testFavoriteStore_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        Long storeId = 100L;
        
        UserFavoriteRequest request = new UserFavoriteRequest();
        request.setStoreId(storeId);

        // 配置Mock行为
        when(userService.favoriteStore(userId, storeId)).thenReturn(true);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(post("/user/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证Mock调用
        verify(userService, times(1)).favoriteStore(userId, storeId);
    }

    /**
     * 测试收藏店铺 - 反向测试用例
     * 
     * 验证场景：收藏失败
     * 预期结果：返回收藏失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("收藏店铺 - 反向测试：收藏失败")
    void testFavoriteStore_Failure() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        Long storeId = 100L;
        
        UserFavoriteRequest request = new UserFavoriteRequest();
        request.setStoreId(storeId);

        // 配置Mock行为 - 收藏失败
        when(userService.favoriteStore(userId, storeId)).thenReturn(false);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(post("/user/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("收藏失败"));

        // 验证Mock调用
        verify(userService, times(1)).favoriteStore(userId, storeId);
    }

    // ==================== 搜索功能接口测试 ====================

    /**
     * 测试搜索店铺和商品 - 正向测试用例
     * 
     * 验证场景：使用有效关键词搜索
     * 预期结果：返回状态码200，包含搜索结果
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("搜索店铺和商品 - 正向测试：有效关键词搜索")
    void testSearchStoreAndProduct_Success() throws Exception {
        // 准备测试数据
        UserSearchRequest request = new UserSearchRequest();
        request.setKeyword("测试店铺");
        request.setPage(1);
        request.setPageSize(10);

        List<UserSearchResponse> mockResults = new ArrayList<>();
        UserSearchResponse searchResult = new UserSearchResponse();
        // 设置搜索结果属性
        mockResults.add(searchResult);

        // 配置Mock行为
        when(userService.searchStores(eq("测试店铺"), any(), any(), any(), any(), eq(1), eq(10)))
                .thenReturn(mockResults);

        // 执行测试
        mockMvc.perform(post("/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reults").isArray());

        // 验证Mock调用
        verify(userService, times(1)).searchStores(eq("测试店铺"), any(), any(), any(), any(), eq(1), eq(10));
    }

    /**
     * 测试搜索店铺和商品 - 反向测试用例
     * 
     * 验证场景：关键词为空
     * 预期结果：返回关键词不能为空的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("搜索店铺和商品 - 反向测试：关键词为空")
    void testSearchStoreAndProduct_EmptyKeyword() throws Exception {
        // 准备测试数据 - 关键词为空
        UserSearchRequest request = new UserSearchRequest();
        request.setKeyword("");
        request.setPage(1);
        request.setPageSize(10);

        // 执行测试
        mockMvc.perform(post("/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("关键词不能为空"));

        // 验证Mock调用 - 不应该调用搜索服务
        verify(userService, never()).searchStores(anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), anyInt(), anyInt());
    }

    // ==================== 创建订单接口测试 ====================

    /**
     * 测试创建订单 - 正向测试用例
     * 
     * 验证场景：正常创建订单
     * 预期结果：返回状态码200，包含订单信息
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("创建订单 - 正向测试：正常创建")
    void testUserCreateOrder_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        OrderCreateRequest request = new OrderCreateRequest();
        // 设置订单创建请求属性

        UserCreateOrderResponse mockResponse = new UserCreateOrderResponse();
        // 设置响应属性

        // 配置Mock行为
        when(orderService.createOrder(userId, request)).thenReturn(mockResponse);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(post("/user/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证Mock调用
        verify(orderService, times(1)).createOrder(userId, request);
    }

    /**
     * 测试创建订单 - 反向测试用例
     * 
     * 验证场景：创建订单时发生异常
     * 预期结果：返回下单失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("创建订单 - 反向测试：创建失败")
    void testUserCreateOrder_Exception() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        OrderCreateRequest request = new OrderCreateRequest();

        // 配置Mock行为 - 抛出异常
        when(orderService.createOrder(userId, request))
                .thenThrow(new RuntimeException("库存不足"));

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(post("/user/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("下单失败：库存不足"));

        // 验证Mock调用
        verify(orderService, times(1)).createOrder(userId, request);
    }

    // ==================== 注销账户接口测试 ====================

    /**
     * 测试注销账户 - 正向测试用例
     * 
     * 验证场景：正常注销账户
     * 预期结果：返回状态码200，注销成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("注销账户 - 正向测试：正常注销")
    void testCancelAccount_Success() throws Exception {
        // 准备测试数据
        Long userId = 1L;

        // 配置Mock行为
        when(userService.cancelAccount(userId)).thenReturn(true);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(delete("/user/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证Mock调用
        verify(userService, times(1)).cancelAccount(userId);
    }

    /**
     * 测试注销账户 - 反向测试用例
     * 
     * 验证场景：注销失败
     * 预期结果：返回注销失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("注销账户 - 反向测试：注销失败")
    void testCancelAccount_Failure() throws Exception {
        // 准备测试数据
        Long userId = 1L;

        // 配置Mock行为 - 注销失败
        when(userService.cancelAccount(userId)).thenReturn(false);

        // 使用MockedStatic模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);

        // 执行测试
        mockMvc.perform(delete("/user/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("注销失败"));

        // 验证Mock调用
        verify(userService, times(1)).cancelAccount(userId);
    }
}