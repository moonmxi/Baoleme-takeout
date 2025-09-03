/**
 * MerchantController单元测试类
 * 测试商家相关的所有接口功能，包括注册、登录、获取信息、更新信息、登出和删除等操作
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.merchant.*;
import org.demo.baoleme.pojo.Merchant;
import org.demo.baoleme.service.MerchantService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MerchantController测试类
 * 使用MockMvc进行Web层测试，模拟HTTP请求和响应
 * 使用Mockito模拟Service层和Redis依赖
 */
@SpringBootTest(classes = org.demo.baoleme.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class MerchantControllerTest {

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
     * 模拟的MerchantService依赖
     */
    @MockBean
    private MerchantService merchantService;

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
     * Mock MerchantMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.MerchantMapper merchantMapper;

    /**
     * 测试用商家数据
     */
    private Merchant testMerchant;
    
    /**
     * 测试用JWT令牌
     */
    private static final String TEST_TOKEN = "test-jwt-token";
    
    /**
     * 测试用用户ID
     */
    private static final Long TEST_USER_ID = 1L;

    /**
     * 测试前的初始化设置
     * 准备测试数据和模拟对象行为
     */
    @BeforeEach
    void setUp() {
        // 初始化测试商家数据
        testMerchant = new Merchant();
        testMerchant.setId(TEST_USER_ID);
        testMerchant.setUsername("testMerchant");
        testMerchant.setPhone("13812345678");
        testMerchant.setPassword(new BCryptPasswordEncoder().encode("password123"));
        testMerchant.setAvatar("http://example.com/avatar.jpg");

        // 模拟RedisTemplate的ValueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // ==================== 商家注册测试 ====================

    /**
     * 测试商家注册成功场景
     * 验证商家能够成功注册
     */
    @Test
    @DisplayName("商家注册 - 成功")
    void testRegister_Success() throws Exception {
        // 准备测试数据
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("newMerchant");
        request.setPassword("password123");
        request.setPhone("13987654321");

        // 模拟Service层行为
        when(merchantService.createMerchant(any(Merchant.class))).thenReturn(testMerchant);

        // 执行测试
        mockMvc.perform(post("/merchant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data.username").value("testMerchant"))
                .andExpect(jsonPath("$.data.phone").value("13812345678"));

        // 验证Service方法调用
        verify(merchantService).createMerchant(any(Merchant.class));
    }

    /**
     * 测试商家注册失败场景（用户名或手机号重复）
     * 验证重复注册时的处理
     */
    @Test
    @DisplayName("商家注册 - 用户名或手机号重复")
    void testRegister_DuplicateInfo() throws Exception {
        // 准备测试数据
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername("existingMerchant");
        request.setPassword("password123");
        request.setPhone("13812345678");

        // 模拟Service层返回null（注册失败）
        when(merchantService.createMerchant(any(Merchant.class))).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/merchant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("注册失败：用户名或手机号重复"));

        // 验证Service方法调用
        verify(merchantService).createMerchant(any(Merchant.class));
    }

    /**
     * 测试商家注册参数验证失败场景
     * 验证无效参数时的处理
     */
    @Test
    @DisplayName("商家注册 - 参数验证失败")
    void testRegister_ValidationFailed() throws Exception {
        // 准备测试数据（无效数据）
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setUsername(""); // 空用户名
        request.setPassword("123"); // 密码太短
        request.setPhone("invalid"); // 无效手机号

        // 执行测试
        mockMvc.perform(post("/merchant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 验证Service方法未被调用
        verify(merchantService, never()).createMerchant(any(Merchant.class));
    }

    // ==================== 商家登录测试 ====================

    /**
     * 测试商家登录成功场景
     * 验证商家能够成功登录
     */
    @Test
    @DisplayName("商家登录 - 成功")
    void testLogin_Success() throws Exception {
        // 准备测试数据
        MerchantLoginRequest request = new MerchantLoginRequest();
        request.setPhone("13812345678");
        request.setPassword("password123");

        // 模拟Service层和Redis行为
        when(merchantService.getMerchantByPhone("13812345678")).thenReturn(testMerchant);
        when(redisTemplate.hasKey("merchant:login:" + TEST_USER_ID)).thenReturn(false);
        
        // 模拟JwtUtils静态方法
        try (MockedStatic<JwtUtils> mockedJwtUtils = mockStatic(JwtUtils.class)) {
            mockedJwtUtils.when(() -> JwtUtils.createToken(TEST_USER_ID, "merchant", "testMerchant"))
                    .thenReturn(TEST_TOKEN);

            // 执行测试
            mockMvc.perform(post("/merchant/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.token").value(TEST_TOKEN))
                    .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID));
        }

        // 验证Service方法调用
        verify(merchantService).getMerchantByPhone("13812345678");
        verify(valueOperations).set("merchant:token:" + TEST_TOKEN, TEST_USER_ID, 1, java.util.concurrent.TimeUnit.DAYS);
        verify(valueOperations).set("merchant:login:" + TEST_USER_ID, TEST_TOKEN, 1, java.util.concurrent.TimeUnit.DAYS);
    }

    /**
     * 测试商家登录失败场景（手机号不存在）
     * 验证不存在的手机号登录时的处理
     */
    @Test
    @DisplayName("商家登录 - 手机号不存在")
    void testLogin_PhoneNotExists() throws Exception {
        // 准备测试数据
        MerchantLoginRequest request = new MerchantLoginRequest();
        request.setPhone("13999999999");
        request.setPassword("password123");

        // 模拟Service层返回null（手机号不存在）
        when(merchantService.getMerchantByPhone("13999999999")).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/merchant/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("手机号不存在"));

        // 验证Service方法调用
        verify(merchantService).getMerchantByPhone("13999999999");
    }

    /**
     * 测试商家登录失败场景（密码错误）
     * 验证密码错误时的处理
     */
    @Test
    @DisplayName("商家登录 - 密码错误")
    void testLogin_WrongPassword() throws Exception {
        // 准备测试数据
        MerchantLoginRequest request = new MerchantLoginRequest();
        request.setPhone("13812345678");
        request.setPassword("wrongpassword");

        // 模拟Service层行为
        when(merchantService.getMerchantByPhone("13812345678")).thenReturn(testMerchant);

        // 执行测试
        mockMvc.perform(post("/merchant/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("密码错误"));

        // 验证Service方法调用
        verify(merchantService).getMerchantByPhone("13812345678");
    }

    /**
     * 测试商家登录失败场景（已登录）
     * 验证重复登录时的处理
     */
    @Test
    @DisplayName("商家登录 - 已登录")
    void testLogin_AlreadyLoggedIn() throws Exception {
        // 准备测试数据
        MerchantLoginRequest request = new MerchantLoginRequest();
        request.setPhone("13812345678");
        request.setPassword("password123");

        // 模拟Service层和Redis行为
        when(merchantService.getMerchantByPhone("13812345678")).thenReturn(testMerchant);
        when(redisTemplate.hasKey("merchant:login:" + TEST_USER_ID)).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/merchant/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("该商家已登录，请先登出"));

        // 验证Service方法调用
        verify(merchantService).getMerchantByPhone("13812345678");
    }

    // ==================== 获取商家信息测试 ====================

    /**
     * 测试获取商家信息成功场景
     * 验证能够成功获取商家信息
     */
    @Test
    @DisplayName("获取商家信息 - 成功")
    void testGetInfo_Success() throws Exception {
        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(merchantService.getMerchantById(TEST_USER_ID)).thenReturn(testMerchant);

            // 执行测试
            mockMvc.perform(get("/merchant/info")
                            .header("Authorization", "Bearer " + TEST_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID))
                    .andExpect(jsonPath("$.data.username").value("testMerchant"))
                    .andExpect(jsonPath("$.data.phone").value("13812345678"));
        }

        // 验证Service方法调用
        verify(merchantService).getMerchantById(TEST_USER_ID);
    }

    /**
     * 测试获取商家信息失败场景（用户不存在）
     * 验证用户不存在时的处理
     */
    @Test
    @DisplayName("获取商家信息 - 用户不存在")
    void testGetInfo_UserNotExists() throws Exception {
        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(merchantService.getMerchantById(TEST_USER_ID)).thenReturn(null);

            // 执行测试
            mockMvc.perform(get("/merchant/info")
                            .header("Authorization", "Bearer " + TEST_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("当前身份无效或用户不存在"));
        }

        // 验证Service方法调用
        verify(merchantService).getMerchantById(TEST_USER_ID);
    }

    // ==================== 更新商家信息测试 ====================

    /**
     * 测试更新商家信息成功场景（不修改用户名）
     * 验证能够成功更新商家信息
     */
    @Test
    @DisplayName("更新商家信息 - 成功（不修改用户名）")
    void testUpdate_Success_NoUsernameChange() throws Exception {
        // 准备测试数据
        MerchantUpdateRequest request = new MerchantUpdateRequest();
        request.setPhone("13987654321");
        request.setAvatar("http://example.com/new-avatar.jpg");

        Merchant updatedMerchant = new Merchant();
        updatedMerchant.setId(TEST_USER_ID);
        updatedMerchant.setUsername("testMerchant");
        updatedMerchant.setPhone("13987654321");
        updatedMerchant.setAvatar("http://example.com/new-avatar.jpg");

        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(merchantService.getMerchantById(TEST_USER_ID)).thenReturn(testMerchant);
            when(merchantService.updateInfo(any(Merchant.class))).thenReturn(updatedMerchant);

            // 执行测试
            mockMvc.perform(put("/merchant/update")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID))
                    .andExpect(jsonPath("$.data.phone").value("13987654321"))
                    .andExpect(jsonPath("$.data.avatar").value("http://example.com/new-avatar.jpg"));
        }

        // 验证Service方法调用
        verify(merchantService).getMerchantById(TEST_USER_ID);
        verify(merchantService).updateInfo(any(Merchant.class));
    }

    /**
     * 测试更新商家信息成功场景（修改用户名）
     * 验证修改用户名时会生成新Token
     */
    @Test
    @DisplayName("更新商家信息 - 成功（修改用户名）")
    void testUpdate_Success_UsernameChanged() throws Exception {
        // 准备测试数据
        MerchantUpdateRequest request = new MerchantUpdateRequest();
        request.setUsername("newMerchantName");
        request.setPhone("13987654321");

        Merchant updatedMerchant = new Merchant();
        updatedMerchant.setId(TEST_USER_ID);
        updatedMerchant.setUsername("newMerchantName");
        updatedMerchant.setPhone("13987654321");
        updatedMerchant.setAvatar("http://example.com/avatar.jpg");

        String newToken = "new-jwt-token";

        // 模拟UserHolder、Service层和JwtUtils行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class);
             MockedStatic<JwtUtils> mockedJwtUtils = mockStatic(JwtUtils.class)) {
            
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(merchantService.getMerchantById(TEST_USER_ID)).thenReturn(testMerchant);
            when(merchantService.updateInfo(any(Merchant.class))).thenReturn(updatedMerchant);
            mockedJwtUtils.when(() -> JwtUtils.createToken(TEST_USER_ID, "merchant", "newMerchantName"))
                    .thenReturn(newToken);

            // 执行测试
            mockMvc.perform(put("/merchant/update")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.user_id").value(TEST_USER_ID))
                    .andExpect(jsonPath("$.data.username").value("newMerchantName"))
                    .andExpect(jsonPath("$.data.new_token").value(newToken));
        }

        // 验证Service方法调用
        verify(merchantService).getMerchantById(TEST_USER_ID);
        verify(merchantService).updateInfo(any(Merchant.class));
        verify(redisTemplate).delete("merchant:token:" + TEST_TOKEN);
        verify(redisTemplate).delete("merchant:login:" + TEST_USER_ID);
    }

    /**
     * 测试更新商家信息失败场景
     * 验证更新失败时的处理
     */
    @Test
    @DisplayName("更新商家信息 - 失败")
    void testUpdate_Failed() throws Exception {
        // 准备测试数据
        MerchantUpdateRequest request = new MerchantUpdateRequest();
        request.setPhone("13987654321");

        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(merchantService.getMerchantById(TEST_USER_ID)).thenReturn(testMerchant);
            when(merchantService.updateInfo(any(Merchant.class))).thenReturn(null);

            // 执行测试
            mockMvc.perform(put("/merchant/update")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("更新失败，请检查字段"));
        }

        // 验证Service方法调用
        verify(merchantService).getMerchantById(TEST_USER_ID);
        verify(merchantService).updateInfo(any(Merchant.class));
    }

    // ==================== 商家登出测试 ====================

    /**
     * 测试商家登出成功场景
     * 验证商家能够成功登出
     */
    @Test
    @DisplayName("商家登出 - 成功")
    void testLogout_Success() throws Exception {
        // 模拟Redis行为
        when(valueOperations.get("merchant:token:" + TEST_TOKEN)).thenReturn(TEST_USER_ID);

        // 执行测试
        mockMvc.perform(post("/merchant/logout")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Redis操作
        verify(redisTemplate).delete("merchant:login:" + TEST_USER_ID);
        verify(redisTemplate).delete("merchant:token:" + TEST_TOKEN);
    }

    // ==================== 删除商家账户测试 ====================

    /**
     * 测试删除商家账户成功场景
     * 验证商家能够成功删除账户
     */
    @Test
    @DisplayName("删除商家账户 - 成功")
    void testDelete_Success() throws Exception {
        // 模拟UserHolder、Service层和Redis行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(merchantService.deleteMerchant(TEST_USER_ID)).thenReturn(true);
            when(valueOperations.get("merchant:token:" + TEST_TOKEN)).thenReturn(TEST_USER_ID);

            // 执行测试
            mockMvc.perform(delete("/merchant/delete")
                            .header("Authorization", "Bearer " + TEST_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        // 验证Service方法调用
        verify(merchantService).deleteMerchant(TEST_USER_ID);
        verify(redisTemplate).delete("merchant:login:" + TEST_USER_ID);
        verify(redisTemplate).delete("merchant:token:" + TEST_TOKEN);
    }

    /**
     * 测试删除商家账户失败场景
     * 验证删除失败时的处理
     */
    @Test
    @DisplayName("删除商家账户 - 失败")
    void testDelete_Failed() throws Exception {
        // 模拟UserHolder、Service层和Redis行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(merchantService.deleteMerchant(TEST_USER_ID)).thenReturn(false);
            when(valueOperations.get("merchant:token:" + TEST_TOKEN)).thenReturn(TEST_USER_ID);

            // 执行测试
            mockMvc.perform(delete("/merchant/delete")
                            .header("Authorization", "Bearer " + TEST_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("注销失败"));
        }

        // 验证Service方法调用
        verify(merchantService).deleteMerchant(TEST_USER_ID);
        verify(redisTemplate).delete("merchant:login:" + TEST_USER_ID);
        verify(redisTemplate).delete("merchant:token:" + TEST_TOKEN);
    }
}