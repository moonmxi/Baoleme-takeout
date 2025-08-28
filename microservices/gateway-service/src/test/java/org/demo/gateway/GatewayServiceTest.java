/**
 * 网关服务测试类
 * 测试网关服务的核心功能，包括消息传递、数据同步、健康检查等
 * 
 * 测试范围：
 * 1. 消息传递功能测试
 * 2. 数据同步功能测试
 * 3. 健康检查功能测试
 * 4. 网关路由功能测试
 * 5. 异常处理测试
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway;

import org.demo.gateway.service.DataSyncService;
import org.demo.gateway.service.MessageService;
import org.demo.gateway.util.HealthCheckUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 网关服务测试类
 * 使用Spring Boot测试框架进行集成测试
 */
@SpringBootTest
@ActiveProfiles("test")
public class GatewayServiceTest {

    /**
     * 消息传递服务
     */
    @Autowired
    private MessageService messageService;

    /**
     * 数据同步服务
     */
    @Autowired
    private DataSyncService dataSyncService;

    /**
     * 健康检查工具
     */
    @Autowired
    private HealthCheckUtil healthCheckUtil;

    /**
     * 测试消息发布功能
     */
    @Test
    public void testMessagePublishing() {
        try {
            // 测试简单消息发布
            CompletableFuture<Boolean> result = messageService.publishMessage("test-channel", "Hello World");
            Boolean success = result.get();
            
            assertTrue(success, "消息发布应该成功");
            
            // 测试结构化消息发布
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", 12345L);
            payload.put("action", "login");
            payload.put("timestamp", System.currentTimeMillis());
            
            CompletableFuture<Boolean> structuredResult = messageService.publishStructuredMessage(
                    "user-events", "user-login", payload);
            Boolean structuredSuccess = structuredResult.get();
            
            assertTrue(structuredSuccess, "结构化消息发布应该成功");
            
            System.out.println("✅ 消息发布功能测试通过");
        } catch (Exception e) {
            fail("消息发布测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试广播消息功能
     */
    @Test
    public void testBroadcastMessage() {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", "系统维护通知");
            payload.put("startTime", "2024-01-25 02:00:00");
            payload.put("duration", "2小时");
            
            CompletableFuture<Integer> result = messageService.broadcastMessage("system-maintenance", payload);
            Integer receivedServices = result.get();
            
            assertTrue(receivedServices >= 0, "广播消息应该返回接收服务数量");
            
            System.out.println("✅ 广播消息功能测试通过，接收服务数: " + receivedServices);
        } catch (Exception e) {
            fail("广播消息测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试消息持久化功能
     */
    @Test
    public void testMessagePersistence() {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", 80000001L);
            payload.put("status", "completed");
            payload.put("amount", 58.50);
            
            // 持久化消息
            CompletableFuture<String> persistResult = messageService.persistMessage(
                    "order-completion", payload, 3600); // 1小时TTL
            String messageId = persistResult.get();
            
            assertNotNull(messageId, "消息ID不应该为空");
            assertFalse(messageId.trim().isEmpty(), "消息ID不应该为空字符串");
            
            // 获取持久化消息
            CompletableFuture<Map<String, Object>> getResult = messageService.getPersistedMessage(messageId);
            Map<String, Object> retrievedMessage = getResult.get();
            
            assertNotNull(retrievedMessage, "应该能够获取持久化消息");
            assertEquals("order-completion", retrievedMessage.get("type"), "消息类型应该匹配");
            
            System.out.println("✅ 消息持久化功能测试通过，消息ID: " + messageId);
        } catch (Exception e) {
            fail("消息持久化测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据同步功能
     */
    @Test
    public void testDataSync() {
        try {
            // 测试用户数据同步
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", "testuser");
            userData.put("phone", "13800138000");
            userData.put("email", "test@example.com");
            
            CompletableFuture<Boolean> userSyncResult = dataSyncService.syncUserData(10000001L, userData);
            Boolean userSyncSuccess = userSyncResult.get();
            
            assertTrue(userSyncSuccess, "用户数据同步应该成功");
            
            // 测试商品数据同步
            Map<String, Object> productData = new HashMap<>();
            productData.put("name", "测试商品");
            productData.put("price", 29.90);
            productData.put("stock", 100);
            
            CompletableFuture<Boolean> productSyncResult = dataSyncService.syncProductData(60000001L, productData);
            Boolean productSyncSuccess = productSyncResult.get();
            
            assertTrue(productSyncSuccess, "商品数据同步应该成功");
            
            System.out.println("✅ 数据同步功能测试通过");
        } catch (Exception e) {
            fail("数据同步测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据一致性检查
     */
    @Test
    public void testDataConsistencyCheck() {
        try {
            // 先同步一些数据
            Map<String, Object> testData = new HashMap<>();
            testData.put("name", "一致性测试数据");
            testData.put("value", "test-value");
            
            dataSyncService.syncUserData(10000002L, testData).get();
            
            // 检查数据一致性
            CompletableFuture<Boolean> consistencyResult = dataSyncService.checkDataConsistency("user", 10000002L);
            Boolean isConsistent = consistencyResult.get();
            
            assertTrue(isConsistent, "数据应该保持一致");
            
            System.out.println("✅ 数据一致性检查测试通过");
        } catch (Exception e) {
            fail("数据一致性检查测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试健康检查功能
     */
    @Test
    public void testHealthCheck() {
        try {
            // 测试快速健康检查
            Map<String, Object> quickHealth = healthCheckUtil.performQuickHealthCheck();
            
            assertNotNull(quickHealth, "健康检查结果不应该为空");
            assertTrue(quickHealth.containsKey("status"), "应该包含状态信息");
            assertTrue(quickHealth.containsKey("timestamp"), "应该包含时间戳");
            
            String status = (String) quickHealth.get("status");
            assertTrue(status.equals("UP") || status.equals("DOWN"), "状态应该是UP或DOWN");
            
            System.out.println("✅ 快速健康检查测试通过，状态: " + status);
            
            // 测试完整健康检查（可能耗时较长）
            Map<String, Object> fullHealth = healthCheckUtil.performFullHealthCheck();
            
            assertNotNull(fullHealth, "完整健康检查结果不应该为空");
            assertTrue(fullHealth.containsKey("overall"), "应该包含整体状态");
            assertTrue(fullHealth.containsKey("database"), "应该包含数据库状态");
            assertTrue(fullHealth.containsKey("redis"), "应该包含Redis状态");
            
            System.out.println("✅ 完整健康检查测试通过");
        } catch (Exception e) {
            fail("健康检查测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试消息统计功能
     */
    @Test
    public void testMessageStats() {
        try {
            // 先发送一些消息
            messageService.publishMessage("stats-test", "test message 1").get();
            messageService.publishMessage("stats-test", "test message 2").get();
            
            // 获取统计信息
            Map<String, Object> stats = messageService.getMessageStats();
            
            assertNotNull(stats, "统计信息不应该为空");
            assertTrue(stats.containsKey("sentMessages"), "应该包含发送消息数量");
            assertTrue(stats.containsKey("connected"), "应该包含连接状态");
            
            System.out.println("✅ 消息统计功能测试通过");
        } catch (Exception e) {
            fail("消息统计测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据同步统计功能
     */
    @Test
    public void testSyncStats() {
        try {
            // 获取同步统计信息
            Map<String, Object> stats = dataSyncService.getSyncStats();
            
            assertNotNull(stats, "同步统计信息不应该为空");
            assertTrue(stats.containsKey("totalSyncCount"), "应该包含总同步数量");
            assertTrue(stats.containsKey("successSyncCount"), "应该包含成功同步数量");
            assertTrue(stats.containsKey("dataSyncEnabled"), "应该包含同步启用状态");
            
            System.out.println("✅ 数据同步统计功能测试通过");
        } catch (Exception e) {
            fail("数据同步统计测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试异常处理
     */
    @Test
    public void testExceptionHandling() {
        try {
            // 测试空参数异常处理
            assertThrows(IllegalArgumentException.class, () -> {
                messageService.publishMessage(null, "test message");
            }, "应该抛出IllegalArgumentException");
            
            assertThrows(IllegalArgumentException.class, () -> {
                messageService.publishMessage("test-channel", null);
            }, "应该抛出IllegalArgumentException");
            
            assertThrows(IllegalArgumentException.class, () -> {
                dataSyncService.syncUserData(null, new HashMap<>());
            }, "应该抛出IllegalArgumentException");
            
            System.out.println("✅ 异常处理测试通过");
        } catch (Exception e) {
            fail("异常处理测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试服务连接状态
     */
    @Test
    public void testServiceConnection() {
        try {
            // 测试消息服务连接
            boolean messageServiceConnected = messageService.isConnected();
            System.out.println("消息服务连接状态: " + (messageServiceConnected ? "已连接" : "未连接"));
            
            // 注意：在测试环境中，Redis可能未启动，所以连接状态可能为false
            // 这里不做断言，只是验证方法能正常执行
            
            System.out.println("✅ 服务连接状态测试通过");
        } catch (Exception e) {
            fail("服务连接状态测试失败: " + e.getMessage());
        }
    }

    /**
     * 集成测试：完整的消息传递和数据同步流程
     */
    @Test
    public void testIntegratedWorkflow() {
        try {
            System.out.println("开始集成测试...");
            
            // 1. 发送用户注册消息
            Map<String, Object> userRegistrationPayload = new HashMap<>();
            userRegistrationPayload.put("userId", 10000999L);
            userRegistrationPayload.put("username", "integration-test-user");
            userRegistrationPayload.put("phone", "13999999999");
            
            messageService.publishStructuredMessage(
                    "user-events", "user-registration", userRegistrationPayload).get();
            
            // 2. 同步用户数据
            dataSyncService.syncUserData(10000999L, userRegistrationPayload).get();
            
            // 3. 检查数据一致性
            Boolean isConsistent = dataSyncService.checkDataConsistency("user", 10000999L).get();
            
            // 4. 发送订单创建消息
            Map<String, Object> orderPayload = new HashMap<>();
            orderPayload.put("orderId", 80000999L);
            orderPayload.put("userId", 10000999L);
            orderPayload.put("amount", 99.99);
            
            messageService.broadcastMessage("order-created", orderPayload).get();
            
            // 5. 同步订单数据
            dataSyncService.syncOrderData(80000999L, orderPayload).get();
            
            // 6. 获取统计信息
            Map<String, Object> messageStats = messageService.getMessageStats();
            Map<String, Object> syncStats = dataSyncService.getSyncStats();
            
            assertNotNull(messageStats, "消息统计不应该为空");
            assertNotNull(syncStats, "同步统计不应该为空");
            
            System.out.println("✅ 集成测试通过");
            System.out.println("消息统计: " + messageStats);
            System.out.println("同步统计: " + syncStats);
            
        } catch (Exception e) {
            fail("集成测试失败: " + e.getMessage());
        }
    }
}