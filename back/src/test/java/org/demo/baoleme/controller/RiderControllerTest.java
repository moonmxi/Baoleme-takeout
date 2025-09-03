/**
 * RiderController单元测试类
 * 测试骑手相关的所有接口功能，包括注册、登录、信息管理、接单模式切换等操作
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.common.JwtInterceptor;
import org.demo.baoleme.dto.request.rider.*;
import org.demo.baoleme.pojo.Rider;
import org.demo.baoleme.service.RiderService;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RiderController测试类
 * 使用MockMvc进行Web层测试，模拟HTTP请求和响应
 * 使用Mockito模拟Service层依赖和Redis操作
 */
@SpringBootTest(classes = org.demo.baoleme.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class RiderControllerTest extends BaseControllerTest {

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
     * 模拟的RiderService依赖
     */
    @MockBean
    private RiderService riderService;

    /**
     * 模拟的RedisTemplate依赖
     */
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 模拟的ValueOperations依赖
     */
    @MockBean
    private ValueOperations<String, Object> valueOperations;

    /**
     * 模拟的RiderMapper依赖，用于避免MyBatis配置冲突
     */
    @MockBean
    private org.demo.baoleme.mapper.RiderMapper riderMapper;

    /**
     * Mock JwtInterceptor to avoid JWT authentication issues
     */
    @MockBean
    private JwtInterceptor jwtInterceptor;

    /**
     * 测试用骑手数据
     */
    private Rider testRider;
    
    /**
     * 测试用JWT令牌
     */
    private static final String TEST_TOKEN = "test-jwt-token";
    
    /**
     * 测试用用户ID
     */
    private static final Long TEST_USER_ID = 1L;
    
    /**
     * 测试用用户名
     */
    private static final String TEST_USERNAME = "testRider";
    
    /**
     * 测试用手机号
     */
    private static final String TEST_PHONE = "13812345678";
    
    /**
     * 测试用密码
     */
    private static final String TEST_PASSWORD = "password123";

    /**
     * 测试前的初始化设置
     * 准备测试数据和模拟对象行为
     */
    @BeforeEach
    void setUp() throws Exception {
        // Mock JwtInterceptor让所有请求通过
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        
        // 初始化测试骑手数据
        testRider = new Rider();
        testRider.setId(TEST_USER_ID);
        testRider.setUsername(TEST_USERNAME);
        testRider.setPhone(TEST_PHONE);
        testRider.setPassword(TEST_PASSWORD);
        testRider.setOrderStatus(1); // 空闲
        testRider.setDispatchMode(0); // 手动接单
        testRider.setAvatar("http://example.com/avatar.jpg");
        testRider.setCreatedAt(LocalDateTime.now());
        
        // 模拟RedisTemplate的ValueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // ==================== 注册测试 ====================

    /**
     * 测试骑手注册成功场景
     * 验证骑手能够成功注册
     */
    @Test
    @DisplayName("骑手注册 - 成功")
    void testRegister_Success() throws Exception {
        // 准备测试数据
        RiderRegisterRequest request = new RiderRegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        request.setPhone(TEST_PHONE);

        // 模拟Service层行为
        when(riderService.register(any(Rider.class))).thenReturn(testRider);

        // 执行测试
        mockMvc.perform(post("/rider/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.phone").value(TEST_PHONE));

        // 验证Service方法调用
        verify(riderService).register(any(Rider.class));
    }

    /**
     * 测试骑手注册失败场景（用户名或手机号重复）
     * 验证重复注册时的处理
     */
    @Test
    @DisplayName("骑手注册 - 用户名或手机号重复")
    void testRegister_DuplicateUserOrPhone() throws Exception {
        // 准备测试数据
        RiderRegisterRequest request = new RiderRegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        request.setPhone(TEST_PHONE);

        // 模拟Service层行为（返回null表示注册失败）
        when(riderService.register(any(Rider.class))).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/rider/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("注册失败：用户名或手机号重复"));

        // 验证Service方法调用
        verify(riderService).register(any(Rider.class));
    }

    /**
     * 测试骑手注册参数验证失败场景
     * 验证无效参数时的处理
     */
    @Test
    @DisplayName("骑手注册 - 参数验证失败")
    void testRegister_ValidationFailed() throws Exception {
        // 准备测试数据（无效数据）
        RiderRegisterRequest request = new RiderRegisterRequest();
        request.setUsername(""); // 空用户名
        request.setPassword("123"); // 密码太短
        request.setPhone("invalid"); // 无效手机号

        // 执行测试
        mockMvc.perform(post("/rider/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 验证Service方法未被调用
        verify(riderService, never()).register(any(Rider.class));
    }

    // ==================== 登录测试 ====================

    /**
     * 测试骑手登录成功场景
     * 验证骑手能够成功登录
     */
    @Test
    @DisplayName("骑手登录 - 成功")
    void testLogin_Success() throws Exception {
        // 准备测试数据
        RiderLoginRequest request = new RiderLoginRequest();
        request.setPhone(TEST_PHONE);
        request.setPassword(TEST_PASSWORD);

        // 模拟Service层和Redis行为
        when(riderService.login(TEST_PHONE, TEST_PASSWORD)).thenReturn(testRider);
        when(redisTemplate.hasKey("rider:login:" + TEST_USER_ID)).thenReturn(false);
        
        // 模拟JwtUtils静态方法
        try (MockedStatic<JwtUtils> mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.createToken(TEST_USER_ID, "rider", TEST_USERNAME))
                    .thenReturn(TEST_TOKEN);

            // 执行测试
            mockMvc.perform(post("/rider/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value(TEST_TOKEN))
                    .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                    .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID));
        }

        // 验证Service和Redis方法调用
        verify(riderService).login(TEST_PHONE, TEST_PASSWORD);
        verify(redisTemplate).hasKey("rider:login:" + TEST_USER_ID);
        verify(valueOperations).set("rider:token:" + TEST_TOKEN, TEST_USER_ID, 1, java.util.concurrent.TimeUnit.DAYS);
        verify(valueOperations).set("rider:login:" + TEST_USER_ID, TEST_TOKEN, 1, java.util.concurrent.TimeUnit.DAYS);
    }

    /**
     * 测试骑手登录失败场景（手机号或密码错误）
     * 验证登录凭据错误时的处理
     */
    @Test
    @DisplayName("骑手登录 - 手机号或密码错误")
    void testLogin_InvalidCredentials() throws Exception {
        // 准备测试数据
        RiderLoginRequest request = new RiderLoginRequest();
        request.setPhone(TEST_PHONE);
        request.setPassword("wrongpassword");

        // 模拟Service层行为（返回null表示登录失败）
        when(riderService.login(TEST_PHONE, "wrongpassword")).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/rider/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("手机号或密码错误"));

        // 验证Service方法调用
        verify(riderService).login(TEST_PHONE, "wrongpassword");
        verify(redisTemplate, never()).hasKey(anyString());
    }

    /**
     * 测试骑手登录失败场景（已登录）
     * 验证重复登录时的处理
     */
    @Test
    @DisplayName("骑手登录 - 已登录")
    void testLogin_AlreadyLoggedIn() throws Exception {
        // 准备测试数据
        RiderLoginRequest request = new RiderLoginRequest();
        request.setPhone(TEST_PHONE);
        request.setPassword(TEST_PASSWORD);

        // 模拟Service层和Redis行为
        when(riderService.login(TEST_PHONE, TEST_PASSWORD)).thenReturn(testRider);
        when(redisTemplate.hasKey("rider:login:" + TEST_USER_ID)).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/rider/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("该骑手已登录，请先登出"));

        // 验证Service和Redis方法调用
        verify(riderService).login(TEST_PHONE, TEST_PASSWORD);
        verify(redisTemplate).hasKey("rider:login:" + TEST_USER_ID);
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }

    // ==================== 获取信息测试 ====================

    /**
     * 测试获取骑手信息成功场景
     * 验证能够成功获取骑手信息
     */
    @Test
    @DisplayName("获取骑手信息 - 成功")
    void testGetInfo_Success() throws Exception {
        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.getInfo(TEST_USER_ID)).thenReturn(testRider);

        // 执行测试
        mockMvc.perform(get("/rider/info")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.phone").value(TEST_PHONE));

        // 验证Service方法调用
        verify(riderService).getInfo(TEST_USER_ID);
    }

    /**
     * 测试获取骑手信息失败场景（用户不存在）
     * 验证用户不存在时的处理
     */
    @Test
    @DisplayName("获取骑手信息 - 用户不存在")
    void testGetInfo_UserNotExists() throws Exception {
        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.getInfo(TEST_USER_ID)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/rider/info")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("当前身份无效或用户不存在"));

        // 验证Service方法调用
        verify(riderService).getInfo(TEST_USER_ID);
    }

    // ==================== 更新信息测试 ====================

    /**
     * 测试更新骑手信息成功场景（不修改用户名）
     * 验证能够成功更新骑手信息
     */
    @Test
    @DisplayName("更新骑手信息 - 成功（不修改用户名）")
    void testUpdate_Success_NoUsernameChange() throws Exception {
        // 准备测试数据
        RiderUpdateRequest request = new RiderUpdateRequest();
        request.setPhone("13987654321");
        request.setOrderStatus(0);
        request.setDispatchMode(1);

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.getInfo(TEST_USER_ID)).thenReturn(testRider);
        when(riderService.updateInfo(any(Rider.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/rider/update")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(riderService).getInfo(TEST_USER_ID);
        verify(riderService).updateInfo(any(Rider.class));
    }

    /**
     * 测试更新骑手信息成功场景（修改用户名）
     * 验证修改用户名时重新签发token
     */
    @Test
    @DisplayName("更新骑手信息 - 成功（修改用户名）")
    void testUpdate_Success_UsernameChanged() throws Exception {
        // 准备测试数据
        RiderUpdateRequest request = new RiderUpdateRequest();
        request.setUsername("newRiderName");
        request.setPhone("13987654321");

        String newToken = "new-jwt-token";

        // 模拟UserHolder和Service层行为
        try (MockedStatic<JwtUtils> mockedJwtUtils = mockStatic(JwtUtils.class)) {
            
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(riderService.getInfo(TEST_USER_ID)).thenReturn(testRider);
            when(riderService.updateInfo(any(Rider.class))).thenReturn(true);
            mockedJwtUtils.when(() -> JwtUtils.createToken(TEST_USER_ID, "rider", "newRiderName"))
                    .thenReturn(newToken);

            // 执行测试
            mockMvc.perform(put("/rider/update")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value(newToken))
                    .andExpect(jsonPath("$.data.username").value("newRiderName"))
                    .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID));
        }

        // 验证Service和Redis方法调用
        verify(riderService).getInfo(TEST_USER_ID);
        verify(riderService).updateInfo(any(Rider.class));
        verify(redisTemplate).delete("rider:token:" + TEST_TOKEN);
        verify(redisTemplate).delete("rider:login:" + TEST_USER_ID);
        verify(valueOperations).set("rider:token:" + newToken, TEST_USER_ID, 1, java.util.concurrent.TimeUnit.DAYS);
        verify(valueOperations).set("rider:login:" + TEST_USER_ID, newToken, 1, java.util.concurrent.TimeUnit.DAYS);
    }

    /**
     * 测试更新骑手信息失败场景（用户不存在）
     * 验证用户不存在时的处理
     */
    @Test
    @DisplayName("更新骑手信息 - 用户不存在")
    void testUpdate_UserNotExists() throws Exception {
        // 准备测试数据
        RiderUpdateRequest request = new RiderUpdateRequest();
        request.setPhone("13987654321");

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.getInfo(TEST_USER_ID)).thenReturn(null);

        // 执行测试
        mockMvc.perform(put("/rider/update")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        // 验证Service方法调用
        verify(riderService).getInfo(TEST_USER_ID);
        verify(riderService, never()).updateInfo(any(Rider.class));
    }

    /**
     * 测试更新骑手信息失败场景（更新失败）
     * 验证更新失败时的处理
     */
    @Test
    @DisplayName("更新骑手信息 - 更新失败")
    void testUpdate_UpdateFailed() throws Exception {
        // 准备测试数据
        RiderUpdateRequest request = new RiderUpdateRequest();
        request.setPhone("13987654321");

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.getInfo(TEST_USER_ID)).thenReturn(testRider);
        when(riderService.updateInfo(any(Rider.class))).thenReturn(false);

        // 执行测试
        mockMvc.perform(put("/rider/update")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("更新失败，请检查字段"));

        // 验证Service方法调用
        verify(riderService).getInfo(TEST_USER_ID);
        verify(riderService).updateInfo(any(Rider.class));
    }

    // ==================== 切换接单模式测试 ====================

    /**
     * 测试切换接单模式成功场景
     * 验证能够成功切换接单模式
     */
    @Test
    @DisplayName("切换接单模式 - 成功")
    void testSwitchDispatchMode_Success() throws Exception {
        // 准备测试数据
        RiderDispatchModeRequest request = new RiderDispatchModeRequest();
        request.setDispatchMode(1); // 切换到自动接单

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.updateInfo(any(Rider.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(patch("/rider/dispatch-mode")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.current_mode").value(1))
                .andExpect(jsonPath("$.data.mode_changed_at").exists());

        // 验证Service方法调用
        verify(riderService).updateInfo(any(Rider.class));
    }

    /**
     * 测试切换接单模式失败场景
     * 验证切换失败时的处理
     */
    @Test
    @DisplayName("切换接单模式 - 失败")
    void testSwitchDispatchMode_Failed() throws Exception {
        // 准备测试数据
        RiderDispatchModeRequest request = new RiderDispatchModeRequest();
        request.setDispatchMode(1);

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.updateInfo(any(Rider.class))).thenReturn(false);

        // 执行测试
        mockMvc.perform(patch("/rider/dispatch-mode")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("切换接单模式失败"));

        // 验证Service方法调用
        verify(riderService).updateInfo(any(Rider.class));
    }

    // ==================== 登出测试 ====================

    /**
     * 测试骑手登出成功场景
     * 验证能够成功登出
     */
    @Test
    @DisplayName("骑手登出 - 成功")
    void testLogout_Success() throws Exception {
        // 模拟UserHolder、Service层和Redis行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(valueOperations.get("rider:token:" + TEST_TOKEN)).thenReturn(TEST_USER_ID);
        when(riderService.updateInfo(any(Rider.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/rider/logout")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Redis和Service方法调用
        verify(valueOperations).get("rider:token:" + TEST_TOKEN);
        verify(redisTemplate).delete("rider:login:" + TEST_USER_ID);
        verify(redisTemplate).delete("rider:token:" + TEST_TOKEN);
        verify(riderService).updateInfo(any(Rider.class));
    }

    /**
     * 测试骑手登出失败场景
     * 验证登出失败时的处理
     */
    @Test
    @DisplayName("骑手登出 - 失败")
    void testLogout_Failed() throws Exception {
        // 模拟UserHolder、Service层和Redis行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(valueOperations.get("rider:token:" + TEST_TOKEN)).thenReturn(TEST_USER_ID);
        when(riderService.updateInfo(any(Rider.class))).thenReturn(false);

        // 执行测试
        mockMvc.perform(post("/rider/logout")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("登出失败"));

        // 验证Service方法调用
        verify(riderService).updateInfo(any(Rider.class));
    }

    // ==================== 删除账户测试 ====================

    /**
     * 测试删除骑手账户成功场景
     * 验证能够成功删除账户
     */
    @Test
    @DisplayName("删除骑手账户 - 成功")
    void testDelete_Success() throws Exception {
        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.delete(TEST_USER_ID)).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/rider/delete")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(riderService).delete(TEST_USER_ID);
    }

    /**
     * 测试删除骑手账户失败场景
     * 验证删除失败时的处理
     */
    @Test
    @DisplayName("删除骑手账户 - 失败")
    void testDelete_Failed() throws Exception {
        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.delete(TEST_USER_ID)).thenReturn(false);

        // 执行测试
        mockMvc.perform(delete("/rider/delete")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("注销失败"));

        // 验证Service方法调用
        verify(riderService).delete(TEST_USER_ID);
    }

    // ==================== 自动接单测试 ====================

    /**
     * 测试自动接单成功场景
     * 验证能够成功自动接单
     */
    @Test
    @DisplayName("自动接单 - 成功")
    void testAutoOrderTaking_Success() throws Exception {
        // 准备测试数据（自动接单模式）
        Rider autoRider = new Rider();
        autoRider.setId(TEST_USER_ID);
        autoRider.setDispatchMode(1); // 自动接单

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.getInfo(TEST_USER_ID)).thenReturn(autoRider);
        when(riderService.randomSendOrder(TEST_USER_ID)).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/rider/auto-order-taking")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(riderService).getInfo(TEST_USER_ID);
        verify(riderService).randomSendOrder(TEST_USER_ID);
    }

    /**
     * 测试自动接单场景（手动接单模式）
     * 验证手动接单模式时的处理
     */
    @Test
    @DisplayName("自动接单 - 手动接单模式")
    void testAutoOrderTaking_ManualMode() throws Exception {
        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.getInfo(TEST_USER_ID)).thenReturn(testRider); // dispatchMode = 0

        // 执行测试
        mockMvc.perform(post("/rider/auto-order-taking")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("当前骑手不自动接单"));

        // 验证Service方法调用
        verify(riderService).getInfo(TEST_USER_ID);
        verify(riderService, never()).randomSendOrder(anyLong());
    }

    /**
     * 测试自动接单失败场景（无空闲订单）
     * 验证无空闲订单时的处理
     */
    @Test
    @DisplayName("自动接单 - 无空闲订单")
    void testAutoOrderTaking_NoAvailableOrders() throws Exception {
        // 准备测试数据（自动接单模式）
        Rider autoRider = new Rider();
        autoRider.setId(TEST_USER_ID);
        autoRider.setDispatchMode(1); // 自动接单

        // 模拟UserHolder和Service层行为
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        when(riderService.getInfo(TEST_USER_ID)).thenReturn(autoRider);
        when(riderService.randomSendOrder(TEST_USER_ID)).thenReturn(false);

        // 执行测试
        mockMvc.perform(post("/rider/auto-order-taking")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("目前无空闲订单"));

        // 验证Service方法调用
        verify(riderService).getInfo(TEST_USER_ID);
        verify(riderService).randomSendOrder(TEST_USER_ID);
    }
}