/**
 * StatsController单元测试类
 * 测试销售统计相关的接口功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.dto.request.salesStats.SaleOverviewStatsRequest;
import org.demo.baoleme.dto.request.salesStats.SaleTrendStatsRequest;
import org.demo.baoleme.dto.response.salesStats.SaleTrendData;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.service.SalesStatsService;
import org.demo.baoleme.service.StoreService;
import org.demo.baoleme.common.UserHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * StatsController测试类
 * 包含销售概览和销售趋势接口的正向和反向测试用例
 */
@SpringBootTest(classes = org.demo.baoleme.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 销售统计服务Mock对象
     */
    @MockBean
    private SalesStatsService salesStatsService;

    /**
     * 店铺服务Mock对象
     */
    @MockBean
    private StoreService storeService;

    /**
     * 用户持有者Mock对象
     */
    @MockBean
    private UserHolder userHolder;

    /**
     * 测试前的初始化设置
     * 设置用户ID和基础Mock行为
     */
    @BeforeEach
    void setUp() {
        // 设置用户ID
        when(UserHolder.getId()).thenReturn(1L);
    }

    /**
     * 测试获取销售概览 - 正向测试用例
     * 验证正常业务场景下的销售概览数据获取
     */
    @Test
    @DisplayName("获取销售概览 - 正向测试")
    void testGetSalesOverview_Success() throws Exception {
        // 准备测试数据
        SaleOverviewStatsRequest request = new SaleOverviewStatsRequest();
        request.setStoreId(1L);
        request.setTimeRange(SaleOverviewStatsRequest.TimeRange.TODAY);

        // 准备热门商品数据
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("热门商品1");
        product1.setPrice(new BigDecimal("29.99"));
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("热门商品2");
        product2.setPrice(new BigDecimal("39.99"));
        
        List<Product> popularProducts = Arrays.asList(product1, product2);

        // Mock服务层方法
        when(storeService.validateStoreOwnership(1L, 1L)).thenReturn(true);
        when(salesStatsService.getTotalSales(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1000.00"));
        when(salesStatsService.getOrderCount(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(50);
        when(salesStatsService.getPopularProducts(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(popularProducts);

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/overview")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total_sales").value(1000.00))
                .andExpect(jsonPath("$.data.order_count").value(50))
                .andExpect(jsonPath("$.data.popular_products").isArray())
                .andExpect(jsonPath("$.data.popular_products[0].name").value("热门商品1"));
    }

    /**
     * 测试获取销售概览 - 无权限访问
     * 验证商家无权查看其他店铺数据的异常处理
     */
    @Test
    @DisplayName("获取销售概览 - 无权限访问")
    void testGetSalesOverview_NoPermission() throws Exception {
        // 准备测试数据
        SaleOverviewStatsRequest request = new SaleOverviewStatsRequest();
        request.setStoreId(2L); // 不属于当前用户的店铺
        request.setTimeRange(SaleOverviewStatsRequest.TimeRange.TODAY);

        // Mock服务层方法 - 返回无权限
        when(storeService.validateStoreOwnership(2L, 1L)).thenReturn(false);

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/overview")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("商家无权查看"));
    }

    /**
     * 测试获取销售概览 - 参数验证失败
     * 验证请求参数为空时的异常处理
     */
    @Test
    @DisplayName("获取销售概览 - 参数验证失败")
    void testGetSalesOverview_InvalidParams() throws Exception {
        // 准备无效的测试数据（店铺ID为空）
        SaleOverviewStatsRequest request = new SaleOverviewStatsRequest();
        request.setStoreId(null);
        request.setTimeRange(SaleOverviewStatsRequest.TimeRange.TODAY);

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/overview")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试获取销售趋势 - 正向测试用例
     * 验证正常业务场景下的销售趋势数据获取
     */
    @Test
    @DisplayName("获取销售趋势 - 正向测试")
    void testGetSalesTrend_Success() throws Exception {
        // 准备测试数据
        SaleTrendStatsRequest request = new SaleTrendStatsRequest();
        request.setStoreId(1L);
        request.setType(SaleTrendStatsRequest.TimeAxis.BY_DAY);
        request.setNumOfRecentDays(7);

        // 准备趋势数据
        SaleTrendData trendData1 = new SaleTrendData();
        trendData1.setDateLabel("2025-01-20");
        trendData1.setValue(new BigDecimal("150.00"));
        
        SaleTrendData trendData2 = new SaleTrendData();
        trendData2.setDateLabel("2025-01-21");
        trendData2.setValue(new BigDecimal("200.00"));
        
        List<SaleTrendData> trendDataList = Arrays.asList(trendData1, trendData2);

        // Mock服务层方法
        when(storeService.validateStoreOwnership(1L, 1L)).thenReturn(true);
        when(salesStatsService.getSalesTrend(1L, SaleTrendStatsRequest.TimeAxis.BY_DAY, 7))
                .thenReturn(trendDataList);

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/trend")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.dates").isArray())
                .andExpect(jsonPath("$.data.values").isArray())
                .andExpect(jsonPath("$.data.dates[0]").value("2025-01-20"))
                .andExpect(jsonPath("$.data.values[0]").value(150));
    }

    /**
     * 测试获取销售趋势 - 无权限访问
     * 验证商家无权查看其他店铺趋势数据的异常处理
     */
    @Test
    @DisplayName("获取销售趋势 - 无权限访问")
    void testGetSalesTrend_NoPermission() throws Exception {
        // 准备测试数据
        SaleTrendStatsRequest request = new SaleTrendStatsRequest();
        request.setStoreId(2L); // 不属于当前用户的店铺
        request.setType(SaleTrendStatsRequest.TimeAxis.BY_DAY);
        request.setNumOfRecentDays(7);

        // Mock服务层方法 - 返回无权限
        when(storeService.validateStoreOwnership(2L, 1L)).thenReturn(false);

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/trend")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("商家无权查看"));
    }

    /**
     * 测试获取销售趋势 - 参数验证失败
     * 验证请求参数为空时的异常处理
     */
    @Test
    @DisplayName("获取销售趋势 - 参数验证失败")
    void testGetSalesTrend_InvalidParams() throws Exception {
        // 准备无效的测试数据（店铺ID为空）
        SaleTrendStatsRequest request = new SaleTrendStatsRequest();
        request.setStoreId(null);
        request.setType(SaleTrendStatsRequest.TimeAxis.BY_DAY);
        request.setNumOfRecentDays(7);

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/trend")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试获取销售趋势 - 按周统计
     * 验证按周统计的销售趋势数据获取
     */
    @Test
    @DisplayName("获取销售趋势 - 按周统计")
    void testGetSalesTrend_ByWeek() throws Exception {
        // 准备测试数据
        SaleTrendStatsRequest request = new SaleTrendStatsRequest();
        request.setStoreId(1L);
        request.setType(SaleTrendStatsRequest.TimeAxis.BY_WEEK);
        request.setNumOfRecentDays(28); // 4周

        // 准备趋势数据
        SaleTrendData trendData1 = new SaleTrendData();
        trendData1.setDateLabel("2025-03");
        trendData1.setValue(new BigDecimal("1500.00"));
        
        List<SaleTrendData> trendDataList = Arrays.asList(trendData1);

        // Mock服务层方法
        when(storeService.validateStoreOwnership(1L, 1L)).thenReturn(true);
        when(salesStatsService.getSalesTrend(1L, SaleTrendStatsRequest.TimeAxis.BY_WEEK, 28))
                .thenReturn(trendDataList);

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/trend")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.dates").isArray())
                .andExpect(jsonPath("$.data.values").isArray());
    }

    /**
     * 测试获取销售趋势 - 按月统计
     * 验证按月统计的销售趋势数据获取
     */
    @Test
    @DisplayName("获取销售趋势 - 按月统计")
    void testGetSalesTrend_ByMonth() throws Exception {
        // 准备测试数据
        SaleTrendStatsRequest request = new SaleTrendStatsRequest();
        request.setStoreId(1L);
        request.setType(SaleTrendStatsRequest.TimeAxis.BY_MONTH);
        request.setNumOfRecentDays(90); // 3个月

        // 准备趋势数据
        SaleTrendData trendData1 = new SaleTrendData();
        trendData1.setDateLabel("2025-01");
        trendData1.setValue(new BigDecimal("5000.00"));
        
        List<SaleTrendData> trendDataList = Arrays.asList(trendData1);

        // Mock服务层方法
        when(storeService.validateStoreOwnership(1L, 1L)).thenReturn(true);
        when(salesStatsService.getSalesTrend(1L, SaleTrendStatsRequest.TimeAxis.BY_MONTH, 90))
                .thenReturn(trendDataList);

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/trend")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.dates").isArray())
                .andExpect(jsonPath("$.data.values").isArray());
    }

    /**
     * 测试获取销售概览 - 本周数据
     * 验证本周时间范围的销售概览数据获取
     */
    @Test
    @DisplayName("获取销售概览 - 本周数据")
    void testGetSalesOverview_ThisWeek() throws Exception {
        // 准备测试数据
        SaleOverviewStatsRequest request = new SaleOverviewStatsRequest();
        request.setStoreId(1L);
        request.setTimeRange(SaleOverviewStatsRequest.TimeRange.THIS_WEEK);

        // Mock服务层方法
        when(storeService.validateStoreOwnership(1L, 1L)).thenReturn(true);
        when(salesStatsService.getTotalSales(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("3000.00"));
        when(salesStatsService.getOrderCount(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(120);
        when(salesStatsService.getPopularProducts(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList());

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/overview")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total_sales").value(3000.00))
                .andExpect(jsonPath("$.data.order_count").value(120));
    }

    /**
     * 测试获取销售概览 - 本月数据
     * 验证本月时间范围的销售概览数据获取
     */
    @Test
    @DisplayName("获取销售概览 - 本月数据")
    void testGetSalesOverview_ThisMonth() throws Exception {
        // 准备测试数据
        SaleOverviewStatsRequest request = new SaleOverviewStatsRequest();
        request.setStoreId(1L);
        request.setTimeRange(SaleOverviewStatsRequest.TimeRange.THIS_MONTH);

        // Mock服务层方法
        when(storeService.validateStoreOwnership(1L, 1L)).thenReturn(true);
        when(salesStatsService.getTotalSales(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("15000.00"));
        when(salesStatsService.getOrderCount(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(500);
        when(salesStatsService.getPopularProducts(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList());

        // 执行请求并验证结果
        mockMvc.perform(post("/stats-store/overview")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total_sales").value(15000.00))
                .andExpect(jsonPath("$.data.order_count").value(500));
    }
}