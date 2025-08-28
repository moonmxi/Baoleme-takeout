/**
 * 网关服务集成测试类
 * 测试新功能的完整性和安全性，包括条件查询、安全认证等功能
 * 
 * 测试范围：
 * 1. 条件查询功能测试
 * 2. 安全认证机制测试
 * 3. 多数据源路由测试
 * 4. API接口完整性测试
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.gateway.config.SecurityConfig;
import org.demo.gateway.service.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 网关服务集成测试类
 * 验证所有新功能的正确性和安全性
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GatewayServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String validToken;

    /**
     * 测试前准备
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 生成有效的认证令牌
        validToken = securityConfig.generateToken("test-service", "database-operation");
    }

    /**
     * 测试健康检查接口（无需认证）
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/database/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.healthStatus").exists())
                .andDo(print());
    }

    /**
     * 测试安全认证 - 生成令牌
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    void testGenerateAuthToken() throws Exception {
        Map<String, Object> tokenRequest = new HashMap<>();
        tokenRequest.put("serviceName", "test-service");
        tokenRequest.put("operation", "database-operation");

        mockMvc.perform(post("/api/database/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.serviceName").value("test-service"))
                .andExpect(jsonPath("$.data.operation").value("database-operation"))
                .andDo(print());
    }

    /**
     * 测试安全认证 - 验证令牌
     */
    @Test
    @org.junit.jupiter.api.Order(3)
    void testValidateAuthToken() throws Exception {
        mockMvc.perform(post("/api/database/auth/validate")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.serviceName").value("test-service"))
                .andDo(print());
    }

    /**
     * 测试安全认证 - 无效令牌
     */
    @Test
    @org.junit.jupiter.api.Order(4)
    void testInvalidAuthToken() throws Exception {
        mockMvc.perform(post("/api/database/auth/validate")
                        .header("X-Gateway-Auth", "invalid-token")
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    /**
     * 测试安全认证 - 缺少令牌
     */
    @Test
    @org.junit.jupiter.api.Order(5)
    void testMissingAuthToken() throws Exception {
        mockMvc.perform(get("/api/database/user/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缺少认证令牌"))
                .andDo(print());
    }

    /**
     * 测试条件查询 - 新格式
     */
    @Test
    @org.junit.jupiter.api.Order(6)
    void testConditionQuery() throws Exception {
        Map<String, Object> conditionRequest = new HashMap<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("username", "test"); // 字符串字段，应使用模糊匹配
        condition.put("id", 1L); // 数字字段，应使用精确匹配
        conditionRequest.put("condition", condition);

        mockMvc.perform(post("/api/database/user/select")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conditionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.count").exists())
                .andExpect(jsonPath("$.data.condition").exists())
                .andDo(print());
    }

    /**
     * 测试分页查询
     */
    @Test
    @org.junit.jupiter.api.Order(7)
    void testPaginationQuery() throws Exception {
        mockMvc.perform(get("/api/database/user")
                        .param("page", "1")
                        .param("pageSize", "5")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(5))
                .andExpect(jsonPath("$.data.totalCount").exists())
                .andDo(print());
    }

    /**
     * 测试数据插入
     */
    @Test
    @org.junit.jupiter.api.Order(8)
    void testDataInsertion() throws Exception {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "test-user-" + System.currentTimeMillis());
        userData.put("password", "test-password");
        userData.put("phone", "13800138000");
        userData.put("description", "测试用户");

        mockMvc.perform(post("/api/database/user")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.affectedRows").value(1))
                .andDo(print());
    }

    /**
     * 测试数据统计
     */
    @Test
    @org.junit.jupiter.api.Order(9)
    void testDataCount() throws Exception {
        mockMvc.perform(get("/api/database/user/count")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.count").exists())
                .andDo(print());
    }

    /**
     * 测试表存在性检查
     */
    @Test
    @org.junit.jupiter.api.Order(10)
    void testTableExists() throws Exception {
        mockMvc.perform(get("/api/database/user/exists")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(true))
                .andDo(print());
    }

    /**
     * 测试表结构查询
     */
    @Test
    @org.junit.jupiter.api.Order(11)
    void testTableStructure() throws Exception {
        mockMvc.perform(get("/api/database/user/structure")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.structure").isArray())
                .andExpect(jsonPath("$.data.columnCount").exists())
                .andDo(print());
    }

    /**
     * 测试多数据源路由 - 不同表的操作
     */
    @Test
    @org.junit.jupiter.api.Order(12)
    void testMultiDataSourceRouting() throws Exception {
        // 测试用户表（user数据源）
        mockMvc.perform(get("/api/database/user/count")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());

        // 测试商家表（merchant数据源）
        mockMvc.perform(get("/api/database/merchant/count")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());

        // 测试订单表（gateway数据源）
        mockMvc.perform(get("/api/database/order/count")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    /**
     * 测试安全配置信息获取
     */
    @Test
    @org.junit.jupiter.api.Order(13)
    void testSecurityInfo() throws Exception {
        mockMvc.perform(get("/api/database/auth/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.security").exists())
                .andExpect(jsonPath("$.data.headers").exists())
                .andExpect(jsonPath("$.data.endpoints").exists())
                .andDo(print());
    }

    /**
     * 测试条件更新
     */
    @Test
    @org.junit.jupiter.api.Order(14)
    void testConditionUpdate() throws Exception {
        Map<String, Object> updateRequest = new HashMap<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("username", "test-user"); // 使用模糊匹配
        
        Map<String, Object> data = new HashMap<>();
        data.put("description", "更新后的描述");
        
        updateRequest.put("condition", condition);
        updateRequest.put("data", data);

        mockMvc.perform(put("/api/database/user/update")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.affectedRows").exists())
                .andExpect(jsonPath("$.data.condition").exists())
                .andDo(print());
    }

    /**
     * 测试服务功能完整性
     */
    @Test
    @org.junit.jupiter.api.Order(15)
    void testServiceIntegrity() {
        // 验证数据库服务是否正常
        assertNotNull(databaseService, "数据库服务应该被正确注入");
        
        // 验证安全配置是否正常
        assertNotNull(securityConfig, "安全配置应该被正确注入");
        assertTrue(securityConfig.isSecurityEnabled(), "安全认证应该被启用");
        
        // 验证令牌生成和验证功能
        String testToken = securityConfig.generateToken("test", "test");
        assertNotNull(testToken, "应该能够生成认证令牌");
        assertTrue(securityConfig.validateToken(testToken), "生成的令牌应该能够通过验证");
        
        // 验证数据源健康状态
        Map<String, Boolean> healthStatus = databaseService.getAllDataSourceHealth();
        assertNotNull(healthStatus, "应该能够获取数据源健康状态");
        assertFalse(healthStatus.isEmpty(), "健康状态不应该为空");
    }

    /**
     * 测试错误处理
     */
    @Test
    @org.junit.jupiter.api.Order(16)
    void testErrorHandling() throws Exception {
        // 测试不存在的表
        mockMvc.perform(get("/api/database/nonexistent_table/count")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());

        // 测试无效的ID
        mockMvc.perform(get("/api/database/user/invalid-id")
                        .header("X-Gateway-Auth", validToken)
                        .header("X-Service-Name", "test-service")
                        .header("X-Operation-Type", "database-operation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }
}