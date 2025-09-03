/**
 * 《饱了么》订单控制器单元测试类
 * 
 * 本测试类为OrderController提供完整的单元测试覆盖，包括：
 * - 获取可用订单、抢单、取消订单功能测试
 * - 更新订单状态、获取骑手订单、获取骑手收入功能测试
 * - 商家更新订单状态功能测试
 * - 正向测试用例（验证正常业务场景）
 * - 反向测试用例（验证异常处理逻辑）
 * 
 * @author 测试开发团队
 * @version 1.0
 * @since 2024-01-20
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.dto.request.order.*;
import org.demo.baoleme.dto.request.rider.RiderOrderHistoryQueryRequest;
import org.demo.baoleme.dto.response.order.*;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.OrderService;
import org.demo.baoleme.service.StoreService;
import org.demo.baoleme.service.RiderService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OrderController单元测试类
 * 
 * 测试覆盖范围：
 * 1. 获取可用订单接口 - 正向/反向测试
 * 2. 抢单接口 - 正向/反向测试
 * 3. 取消订单接口 - 正向/反向测试
 * 4. 更新订单状态接口 - 正向/反向测试
 * 5. 获取骑手订单接口 - 正向/反向测试
 * 6. 获取骑手收入接口 - 正向/反向测试
 * 7. 商家更新订单接口 - 正向/反向测试
 */
@SpringBootTest(classes = org.demo.baoleme.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class OrderControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private StoreService storeService;

    @MockBean
    private RiderService riderService;

    @MockBean
    private JwtUtils jwtUtils;

    /**
     * Mock OrderMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.OrderMapper orderMapper;

    /**
     * Mock UserMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.UserMapper userMapper;

    /**
     * Mock CouponMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.CouponMapper couponMapper;

    /**
     * Mock ProductMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.ProductMapper productMapper;

    /**
     * Mock OrderItemMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.OrderItemMapper orderItemMapper;

    /**
     * Mock StoreMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.StoreMapper storeMapper;

    /**
     * Mock RiderMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.RiderMapper riderMapper;

    /**
     * 测试前置设置
     * 初始化Mock对象和通用测试数据
     */
    @BeforeEach
    void setUp() {
        // 初始化通用Mock设置
    }

    // ==================== 获取可用订单接口测试 ====================

    /**
     * 测试获取可用订单 - 正向测试用例
     * 
     * 验证场景：骑手正常获取可用订单列表
     * 预期结果：返回状态码200，包含可用订单列表
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取可用订单 - 正向测试：正常获取可用订单")
    void testGetAvailableOrders_Success() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        List<Order> mockOrders = new ArrayList<>();
        
        Order orderResponse = new Order();
        orderResponse.setId(100L);
        orderResponse.setTotalPrice(new BigDecimal("50.00"));
        orderResponse.setDeliveryPrice(new BigDecimal("5.00"));
        orderResponse.setUserLocation("测试地址");
        orderResponse.setCreatedAt(LocalDateTime.now());
        mockOrders.add(orderResponse);

        // 配置Mock行为
        when(orderService.getAvailableOrders(anyInt(), anyInt())).thenReturn(mockOrders);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(get("/orders/available")
                        .param("page", "1")
                        .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders[0].id").value(100L))
                .andExpect(jsonPath("$.data.orders[0].totalPrice").value(50.00));

        // 验证Mock调用
        verify(orderService, times(1)).getAvailableOrders(1, 10);
    }

    /**
     * 测试获取可用订单 - 反向测试用例
     * 
     * 验证场景：非骑手角色访问
     * 预期结果：返回权限错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取可用订单 - 反向测试：权限不足")
    void testGetAvailableOrders_NoPermission() throws Exception {
        // 准备测试数据
        Long userId = 1L;

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("user"); // 非骑手角色

        // 执行测试
        mockMvc.perform(get("/orders/available")
                        .param("page", "1")
                        .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无权限访问，仅骑手可操作"));

        // 验证Mock调用 - 不应该调用服务
        verify(orderService, never()).getAvailableOrders(anyInt(), anyInt());
    }

    /**
     * 测试获取可用订单 - 反向测试用例
     * 
     * 验证场景：获取订单时发生异常
     * 预期结果：返回获取失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取可用订单 - 反向测试：获取失败")
    void testGetAvailableOrders_Exception() throws Exception {
        // 准备测试数据
        Long riderId = 1L;

        // 配置Mock行为 - 抛出异常
        when(orderService.getAvailableOrders(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(get("/orders/available")
                        .param("page", "1")
                        .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("获取可用订单失败：数据库连接失败"));

        // 验证Mock调用
        verify(orderService, times(1)).getAvailableOrders(1, 10);
    }

    // ==================== 抢单接口测试 ====================

    /**
     * 测试抢单 - 正向测试用例
     * 
     * 验证场景：骑手正常抢单
     * 预期结果：返回状态码200，抢单成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("抢单 - 正向测试：正常抢单")
    void testGrabOrder_Success() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        Long orderId = 100L;
        
        OrderGrabRequest request = new OrderGrabRequest();
        request.setOrderId(orderId);

        // 配置Mock行为
        when(orderService.grabOrder(orderId, riderId)).thenReturn(true);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(put("/orders/grab")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderId").value(orderId));

        // 验证Mock调用
        verify(orderService, times(1)).grabOrder(orderId, riderId);
    }

    /**
     * 测试抢单 - 反向测试用例
     * 
     * 验证场景：抢单失败（订单已被抢或不存在）
     * 预期结果：返回抢单失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("抢单 - 反向测试：抢单失败")
    void testGrabOrder_Failure() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        Long orderId = 100L;
        
        OrderGrabRequest request = new OrderGrabRequest();
        request.setOrderId(orderId);

        // 配置Mock行为 - 抢单失败
        when(orderService.grabOrder(orderId, riderId)).thenReturn(false);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(put("/orders/grab")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单已被抢或不存在"));

        // 验证Mock调用
        verify(orderService, times(1)).grabOrder(orderId, riderId);
    }

    /**
     * 测试抢单 - 反向测试用例
     * 
     * 验证场景：非骑手角色抢单
     * 预期结果：返回权限错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("抢单 - 反向测试：权限不足")
    void testGrabOrder_NoPermission() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        Long orderId = 100L;
        
        OrderGrabRequest request = new OrderGrabRequest();
        request.setOrderId(orderId);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("user"); // 非骑手角色

        // 执行测试
        mockMvc.perform(put("/orders/grab")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无权限访问，仅骑手可操作"));

        // 验证Mock调用 - 不应该调用服务
        verify(orderService, never()).grabOrder(anyLong(), anyLong());
    }

    // ==================== 取消订单接口测试 ====================

    /**
     * 测试取消订单 - 正向测试用例
     * 
     * 验证场景：骑手正常取消已接订单
     * 预期结果：返回状态码200，取消成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("取消订单 - 正向测试：正常取消")
    void testCancelOrder_Success() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        Long orderId = 100L;
        
        OrderCancelRequest request = new OrderCancelRequest();
        request.setOrderId(orderId);

        // 配置Mock行为
        when(orderService.riderCancelOrder(orderId, riderId)).thenReturn(true);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(put("/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.order_id").value(orderId))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        // 验证Mock调用
        verify(orderService, times(1)).riderCancelOrder(orderId, riderId);
    }

    /**
     * 测试取消订单 - 反向测试用例
     * 
     * 验证场景：取消订单失败
     * 预期结果：返回取消失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("取消订单 - 反向测试：取消失败")
    void testCancelOrder_Failure() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        Long orderId = 100L;
        
        OrderCancelRequest request = new OrderCancelRequest();
        request.setOrderId(orderId);

        // 配置Mock行为 - 取消失败
        when(orderService.riderCancelOrder(orderId, riderId)).thenReturn(false);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(put("/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("当前状态不可取消或订单不存在"));

        // 验证Mock调用
        verify(orderService, times(1)).riderCancelOrder(orderId, riderId);
    }

    /**
     * 测试取消订单 - 反向测试用例
     * 
     * 验证场景：非骑手角色取消订单
     * 预期结果：返回权限错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("取消订单 - 反向测试：权限不足")
    void testCancelOrder_NoPermission() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        Long orderId = 100L;
        
        OrderCancelRequest request = new OrderCancelRequest();
        request.setOrderId(orderId);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("merchant"); // 非骑手角色

        // 执行测试
        mockMvc.perform(put("/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无权限访问，仅骑手可操作"));

        // 验证Mock调用 - 不应该调用服务
        verify(orderService, never()).riderCancelOrder(anyLong(), anyLong());
    }

    // ==================== 更新订单状态接口测试 ====================

    /**
     * 测试更新订单状态 - 正向测试用例
     * 
     * 验证场景：骑手正常更新订单状态
     * 预期结果：返回状态码200，更新成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("更新订单状态 - 正向测试：正常更新")
    void testUpdateOrderStatus_Success() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        Long orderId = 100L;
        Integer targetStatus = 3; // 配送中
        
        OrderStatusRiderUpdateRequest request = new OrderStatusRiderUpdateRequest();
        request.setOrderId(orderId);
        request.setTargetStatus(targetStatus);

        // 配置Mock行为
        when(orderService.riderUpdateOrderStatus(orderId, riderId, targetStatus)).thenReturn(true);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(post("/orders/rider-update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.status").value(targetStatus));

        // 验证Mock调用
        verify(orderService, times(1)).riderUpdateOrderStatus(orderId, riderId, targetStatus);
    }

    /**
     * 测试更新订单状态 - 反向测试用例
     * 
     * 验证场景：更新订单状态失败
     * 预期结果：返回更新失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("更新订单状态 - 反向测试：更新失败")
    void testUpdateOrderStatus_Failure() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        Long orderId = 100L;
        Integer targetStatus = 3;
        
        OrderStatusRiderUpdateRequest request = new OrderStatusRiderUpdateRequest();
        request.setOrderId(orderId);
        request.setTargetStatus(targetStatus);

        // 配置Mock行为 - 更新失败
        when(orderService.riderUpdateOrderStatus(orderId, riderId, targetStatus)).thenReturn(false);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(post("/orders/rider-update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单状态更新失败"));

        // 验证Mock调用
        verify(orderService, times(1)).riderUpdateOrderStatus(orderId, riderId, targetStatus);
    }

    /**
     * 测试更新订单状态 - 反向测试用例
     * 
     * 验证场景：非骑手角色更新订单状态
     * 预期结果：返回权限错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("更新订单状态 - 反向测试：权限不足")
    void testUpdateOrderStatus_NoPermission() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        Long orderId = 100L;
        Integer targetStatus = 3;
        
        OrderStatusRiderUpdateRequest request = new OrderStatusRiderUpdateRequest();
        request.setOrderId(orderId);
        request.setTargetStatus(targetStatus);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("user"); // 非骑手角色

        // 执行测试
        mockMvc.perform(post("/orders/rider-update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无权限访问，仅骑手可操作"));

        // 验证Mock调用 - 不应该调用服务
        verify(orderService, never()).riderUpdateOrderStatus(anyLong(), anyLong(), anyInt());
    }

    // ==================== 获取骑手订单接口测试 ====================

    /**
     * 测试获取骑手订单 - 正向测试用例
     * 
     * 验证场景：骑手正常获取自己的订单列表
     * 预期结果：返回状态码200，包含订单列表
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取骑手订单 - 正向测试：正常获取")
    void testGetRiderOrders_Success() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        List<Order> mockOrders = new ArrayList<>();
        
        Order orderResponse = new Order();
        orderResponse.setId(100L);
        orderResponse.setStatus(2); // 已接单
        orderResponse.setTotalPrice(new BigDecimal("50.00"));
        orderResponse.setDeliveryPrice(new BigDecimal("5.00"));
        mockOrders.add(orderResponse);

        // 配置Mock行为
        when(orderService.getRiderOrders(eq(riderId), any(), any(), any(), anyInt(), anyInt())).thenReturn(mockOrders);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(post("/orders/rider-history-query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RiderOrderHistoryQueryRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders[0].orderId").value(100L))
                .andExpect(jsonPath("$.data.orders[0].status").value(2));

        // 验证Mock调用
        verify(orderService, times(1)).getRiderOrders(eq(riderId), any(), any(), any(), anyInt(), anyInt());
    }

    /**
     * 测试获取骑手订单 - 反向测试用例
     * 
     * 验证场景：非骑手角色获取骑手订单
     * 预期结果：返回权限错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取骑手订单 - 反向测试：权限不足")
    void testGetRiderOrders_NoPermission() throws Exception {
        // 准备测试数据
        Long userId = 1L;

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("merchant"); // 非骑手角色

        // 执行测试
        mockMvc.perform(post("/orders/rider-history-query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RiderOrderHistoryQueryRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无权限访问，仅骑手可操作"));

        // 验证Mock调用 - 不应该调用服务
        verify(orderService, never()).getRiderOrders(any(), any(), any(), any(), anyInt(), anyInt());
    }

    /**
     * 测试获取骑手订单 - 反向测试用例
     * 
     * 验证场景：获取订单时发生异常
     * 预期结果：返回获取失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取骑手订单 - 反向测试：获取失败")
    void testGetRiderOrders_Exception() throws Exception {
        // 准备测试数据
        Long riderId = 1L;

        // 配置Mock行为 - 抛出异常
        when(orderService.getRiderOrders(eq(riderId), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("数据库查询失败"));

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(post("/orders/rider-history-query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RiderOrderHistoryQueryRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("获取骑手订单失败：数据库查询失败"));

        // 验证Mock调用
        verify(orderService, times(1)).getRiderOrders(eq(riderId), any(), any(), any(), anyInt(), anyInt());
    }

    // ==================== 获取骑手收入接口测试 ====================

    /**
     * 测试获取骑手收入 - 正向测试用例
     * 
     * 验证场景：骑手正常获取收入统计
     * 预期结果：返回状态码200，包含收入信息
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取骑手收入 - 正向测试：正常获取")
    void testGetRiderEarnings_Success() throws Exception {
        // 准备测试数据
        Long riderId = 1L;
        Map<String, Object> mockEarnings = new HashMap<>();
        mockEarnings.put("totalEarnings", new BigDecimal("500.00"));
        mockEarnings.put("todayEarnings", new BigDecimal("50.00"));
        mockEarnings.put("completedOrders", 25);
        mockEarnings.put("todayOrders", 3);

        // 配置Mock行为
        when(orderService.getRiderEarnings(riderId)).thenReturn(mockEarnings);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(get("/orders/rider-earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalEarnings").value(500.00))
                .andExpect(jsonPath("$.data.currentMonth").value(50.00))
                .andExpect(jsonPath("$.data.completedOrders").value(25));

        // 验证Mock调用
        verify(orderService, times(1)).getRiderEarnings(riderId);
    }

    /**
     * 测试获取骑手收入 - 反向测试用例
     * 
     * 验证场景：非骑手角色获取收入
     * 预期结果：返回权限错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取骑手收入 - 反向测试：权限不足")
    void testGetRiderEarnings_NoPermission() throws Exception {
        // 准备测试数据
        Long userId = 1L;

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("user"); // 非骑手角色

        // 执行测试
        mockMvc.perform(get("/orders/rider-earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("无权限访问，仅骑手可操作"));

        // 验证Mock调用 - 不应该调用服务
        verify(orderService, never()).getRiderEarnings(any());
    }

    /**
     * 测试获取骑手收入 - 反向测试用例
     * 
     * 验证场景：获取收入时发生异常
     * 预期结果：返回获取失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("获取骑手收入 - 反向测试：获取失败")
    void testGetRiderEarnings_Exception() throws Exception {
        // 准备测试数据
        Long riderId = 1L;

        // 配置Mock行为 - 抛出异常
        when(orderService.getRiderEarnings(riderId))
                .thenThrow(new RuntimeException("统计计算失败"));

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(riderId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("rider");

        // 执行测试
        mockMvc.perform(get("/orders/rider-earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("获取收入统计失败：统计计算失败"));

        // 验证Mock调用
        verify(orderService, times(1)).getRiderEarnings(riderId);
    }

    // ==================== 商家更新订单接口测试 ====================

    /**
     * 测试商家更新订单 - 正向测试用例
     * 
     * 验证场景：商家正常更新订单状态
     * 预期结果：返回状态码200，更新成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("商家更新订单 - 正向测试：正常更新")
    void testUpdateOrderByMerchant_Success() throws Exception {
        // 准备测试数据
        Long merchantId = 1L;
        Long orderId = 100L;
        Integer newStatus = 1; // 已确认
        
        OrderUpdateByMerchantRequest request = new OrderUpdateByMerchantRequest();
        request.setId(orderId);
        request.setNewStatus(newStatus);
        request.setCancelReason(null);

        // 配置Mock行为
        when(orderService.updateOrderByMerchant(merchantId, orderId, newStatus))
                .thenReturn(true);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(merchantId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("merchant");

        // 执行测试
        mockMvc.perform(put("/orders/merchant-update")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.newStatus").value(newStatus));

        // 验证Mock调用
        verify(orderService, times(1)).updateOrderByMerchant(merchantId, orderId, newStatus);
    }

    /**
     * 测试商家更新订单 - 正向测试用例
     * 
     * 验证场景：商家取消订单并提供取消原因
     * 预期结果：返回状态码200，取消成功
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("商家更新订单 - 正向测试：取消订单")
    void testUpdateOrderByMerchant_CancelWithReason() throws Exception {
        // 准备测试数据
        Long merchantId = 1L;
        Long orderId = 100L;
        Integer newStatus = -1; // 已取消
        String cancelReason = "商品缺货";
        
        OrderUpdateByMerchantRequest request = new OrderUpdateByMerchantRequest();
        request.setId(orderId);
        request.setNewStatus(newStatus);
        request.setCancelReason(cancelReason);

        // 配置Mock行为
        when(orderService.updateOrderByMerchant(merchantId, orderId, newStatus))
                .thenReturn(true);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(merchantId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("merchant");

        // 执行测试
        mockMvc.perform(put("/orders/merchant-update")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.newStatus").value(newStatus));

        // 验证Mock调用
        verify(orderService, times(1)).updateOrderByMerchant(merchantId, orderId, newStatus);
    }

    /**
     * 测试商家更新订单 - 反向测试用例
     * 
     * 验证场景：更新订单失败
     * 预期结果：返回更新失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("商家更新订单 - 反向测试：更新失败")
    void testUpdateOrderByMerchant_Failure() throws Exception {
        // 准备测试数据
        Long merchantId = 1L;
        Long orderId = 100L;
        Integer newStatus = 1;
        
        OrderUpdateByMerchantRequest request = new OrderUpdateByMerchantRequest();
        request.setId(orderId);
        request.setNewStatus(newStatus);

        // 配置Mock行为 - 更新失败
        when(orderService.updateOrderByMerchant(merchantId, orderId, newStatus))
                .thenReturn(false);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(merchantId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("merchant");

        // 执行测试
        mockMvc.perform(put("/orders/merchant-update")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单更新失败：权限不足或订单不存在"));

        // 验证Mock调用
        verify(orderService, times(1)).updateOrderByMerchant(merchantId, orderId, newStatus);
    }

    /**
     * 测试商家更新订单 - 反向测试用例
     * 
     * 验证场景：非商家角色更新订单
     * 预期结果：返回权限错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("商家更新订单 - 反向测试：权限不足")
    void testUpdateOrderByMerchant_NoPermission() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        Long orderId = 100L;
        Integer newStatus = 1;
        
        OrderUpdateByMerchantRequest request = new OrderUpdateByMerchantRequest();
        request.setId(orderId);
        request.setNewStatus(newStatus);

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(userId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("user"); // 非商家角色

        // 执行测试
        mockMvc.perform(put("/orders/merchant-update")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无权限访问，仅商家可操作"));

        // 验证Mock调用 - 不应该调用服务
        verify(orderService, never()).updateOrderByMerchant(any(), any(), any());
    }

    /**
     * 测试商家更新订单 - 反向测试用例
     * 
     * 验证场景：更新订单时发生异常
     * 预期结果：返回更新失败的错误响应
     * 
     * @throws Exception 测试异常
     */
    @Test
    @DisplayName("商家更新订单 - 反向测试：更新异常")
    void testUpdateOrderByMerchant_Exception() throws Exception {
        // 准备测试数据
        Long merchantId = 1L;
        Long orderId = 100L;
        Integer newStatus = 1;
        
        OrderUpdateByMerchantRequest request = new OrderUpdateByMerchantRequest();
        request.setId(orderId);
        request.setNewStatus(newStatus);

        // 配置Mock行为 - 抛出异常
        when(orderService.updateOrderByMerchant(merchantId, orderId, newStatus))
                .thenThrow(new RuntimeException("数据库更新失败"));

        // 使用继承的mockedUserHolder模拟UserHolder
        mockedUserHolder.when(UserHolder::getId).thenReturn(merchantId);
        mockedUserHolder.when(UserHolder::getRole).thenReturn("merchant");

        // 执行测试
        mockMvc.perform(put("/orders/merchant-update")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单状态更新失败：数据库更新失败"));

        // 验证Mock调用
        verify(orderService, times(1)).updateOrderByMerchant(merchantId, orderId, newStatus);
    }
}