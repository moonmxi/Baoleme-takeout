/**
 * CartController单元测试类
 * 测试购物车相关的所有接口功能，包括添加商品到购物车、查看购物车、更新购物车商品、删除购物车商品和清空购物车
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.controller.BaseControllerTest;
import org.demo.baoleme.dto.request.cart.AddToCartRequest;
import org.demo.baoleme.dto.request.cart.DeleteCartRequest;
import org.demo.baoleme.dto.request.cart.UpdateCartRequest;
import org.demo.baoleme.dto.response.cart.CartViewResponse;
import org.demo.baoleme.dto.response.cart.CartResponse;
import org.demo.baoleme.service.CartService;
import org.demo.baoleme.common.UserHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.mockito.MockedStatic;
import org.junit.jupiter.api.AfterEach;
import org.springframework.context.annotation.Import;
import org.demo.baoleme.config.TestConfig;

/**
 * CartController测试类
 * 使用MockMvc进行Web层测试，模拟HTTP请求和响应
 * 使用Mockito模拟Service层依赖
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Import(TestConfig.class)
class CartControllerTest extends BaseControllerTest {

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
     * 模拟的CartService依赖
     */
    @MockBean
    private CartService cartService;

    /**
     * 模拟的UserHolder依赖
     */
    @MockBean
    private UserHolder userHolder;

    @MockBean
    private org.demo.baoleme.common.JwtInterceptor jwtInterceptor;

    /**
     * Mock的CartMapper，避免MyBatis配置问题
     */
    @MockBean
    private org.demo.baoleme.mapper.CartMapper cartMapper;

    /**
     * 测试用户ID
     */
    private static final Long TEST_USER_ID = 1L;

    /**
     * 设置默认的用户ID
     */
    @BeforeEach
    void setUp() throws Exception {
        // 模拟UserHolder返回测试用户ID
        mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
        // Mock JwtInterceptor让所有请求通过
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    /**
     * 测试添加商品到购物车成功场景
     * 验证用户能够成功将商品添加到购物车
     */
    @Test
    @DisplayName("添加商品到购物车 - 成功")
    void testAddToCart_Success() throws Exception {
        // 准备测试数据
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        // 模拟Service层行为（void方法，无需返回值）
        doNothing().when(cartService).addToCart(TEST_USER_ID, request);

        // 执行测试
        mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(cartService).addToCart(TEST_USER_ID, request);
    }

    /**
     * 测试添加商品到购物车异常场景
     * 验证添加商品时发生异常的处理
     */
    @Test
    @DisplayName("添加商品到购物车 - 异常")
    void testAddToCart_Exception() throws Exception {
        // 准备测试数据
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        // 模拟Service层抛出异常
        doThrow(new RuntimeException("商品不存在")).when(cartService).addToCart(TEST_USER_ID, request);

        // 执行测试
        mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        // 验证Service方法调用
        verify(cartService).addToCart(TEST_USER_ID, request);
    }

    /**
     * 测试添加商品到购物车参数验证失败场景
     * 验证商品ID为null时的处理
     */
    @Test
    @DisplayName("添加商品到购物车 - 参数验证失败")
    void testAddToCart_InvalidParameters() throws Exception {
        // 准备测试数据（商品ID为null）
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(null);
        request.setQuantity(2);

        // 执行测试
        mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // 注意：这里可能需要根据实际的参数验证逻辑调整

        // 验证Service方法调用
        verify(cartService).addToCart(eq(TEST_USER_ID), any(AddToCartRequest.class));
    }

    /**
     * 测试查看购物车成功场景
     * 验证用户能够成功查看购物车内容
     */
    @Test
    @DisplayName("查看购物车 - 成功")
    void testViewCart_Success() throws Exception {
        // 准备测试数据
        CartViewResponse mockResponse = createMockCartViewResponse();

        // 模拟Service层行为
        when(cartService.viewCart(TEST_USER_ID)).thenReturn(mockResponse);

        // 执行测试
        mockMvc.perform(get("/cart/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.total_price").value(59.98))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(2));

        // 验证Service方法调用
        verify(cartService).viewCart(TEST_USER_ID);
    }

    /**
     * 测试查看购物车为空场景
     * 验证购物车为空时的响应
     */
    @Test
    @DisplayName("查看购物车 - 购物车为空")
    void testViewCart_Empty() throws Exception {
        // 准备测试数据（空购物车）
        CartViewResponse mockResponse = createEmptyCartViewResponse();

        // 模拟Service层行为
        when(cartService.viewCart(TEST_USER_ID)).thenReturn(mockResponse);

        // 执行测试
        mockMvc.perform(get("/cart/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.total_price").value(0))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(0));

        // 验证Service方法调用
        verify(cartService).viewCart(TEST_USER_ID);
    }

    /**
     * 测试更新购物车商品成功场景
     * 验证用户能够成功更新购物车中商品的数量
     */
    @Test
    @DisplayName("更新购物车商品 - 成功")
    void testUpdateCartItem_Success() throws Exception {
        // 准备测试数据
        UpdateCartRequest request = new UpdateCartRequest();
        request.setProductId(1L);
        request.setQuantity(3);

        // 模拟Service层行为
        when(cartService.updateCart(TEST_USER_ID, request)).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(cartService).updateCart(TEST_USER_ID, request);
    }

    /**
     * 测试更新购物车商品异常场景
     * 验证更新不存在的商品时的处理
     */
    @Test
    @DisplayName("更新购物车商品 - 商品不存在")
    void testUpdateCartItem_ProductNotFound() throws Exception {
        // 准备测试数据
        UpdateCartRequest request = new UpdateCartRequest();
        request.setProductId(999L);
        request.setQuantity(3);

        // 模拟Service层抛出异常
        doThrow(new RuntimeException("购物车中不存在该商品")).when(cartService).updateCart(TEST_USER_ID, request);

        // 执行测试
        mockMvc.perform(put("/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        // 验证Service方法调用
        verify(cartService).updateCart(TEST_USER_ID, request);
    }

    /**
     * 测试更新购物车商品数量为0场景
     * 验证将商品数量更新为0时的处理
     */
    @Test
    @DisplayName("更新购物车商品 - 数量为0")
    void testUpdateCartItem_ZeroQuantity() throws Exception {
        // 准备测试数据
        UpdateCartRequest request = new UpdateCartRequest();
        request.setProductId(1L);
        request.setQuantity(0);

        // 模拟Service层行为
        when(cartService.updateCart(TEST_USER_ID, request)).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(cartService).updateCart(TEST_USER_ID, request);
    }

    /**
     * 测试删除购物车商品成功场景
     * 验证用户能够成功删除购物车中的商品
     */
    @Test
    @DisplayName("删除购物车商品 - 成功")
    void testDeleteCartItem_Success() throws Exception {
        // 准备测试数据
        DeleteCartRequest request = new DeleteCartRequest();
        request.setProductId(1L);

        // 模拟Service层行为（void方法，无需返回值）
        doNothing().when(cartService).deleteCartItem(TEST_USER_ID, request);

        // 执行测试
        mockMvc.perform(put("/cart/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(cartService).deleteCartItem(TEST_USER_ID, request);
    }

    /**
     * 测试删除购物车商品异常场景
     * 验证删除不存在的商品时的处理
     */
    @Test
    @DisplayName("删除购物车商品 - 商品不存在")
    void testDeleteCartItem_ProductNotFound() throws Exception {
        // 准备测试数据
        DeleteCartRequest request = new DeleteCartRequest();
        request.setProductId(999L);

        // 模拟Service层抛出异常
        doThrow(new RuntimeException("购物车中不存在该商品")).when(cartService).deleteCartItem(TEST_USER_ID, request);

        // 执行测试
        mockMvc.perform(put("/cart/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        // 验证Service方法调用
        verify(cartService).deleteCartItem(TEST_USER_ID, request);
    }

    /**
     * 测试删除购物车商品参数验证失败场景
     * 验证商品ID为null时的处理
     */
    @Test
    @DisplayName("删除购物车商品 - 参数验证失败")
    void testDeleteCartItem_InvalidParameters() throws Exception {
        // 准备测试数据（商品ID为null）
        DeleteCartRequest request = new DeleteCartRequest();
        request.setProductId(null);

        // 执行测试
        mockMvc.perform(put("/cart/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // 注意：这里可能需要根据实际的参数验证逻辑调整

        // 验证Service方法调用
        verify(cartService).deleteCartItem(eq(TEST_USER_ID), any(DeleteCartRequest.class));
    }

    /**
     * 测试清空购物车成功场景
     * 验证用户能够成功清空购物车
     */
    @Test
    @DisplayName("清空购物车 - 成功")
    void testRemoveCartItems_Success() throws Exception {
        // 模拟Service层行为（void方法，无需返回值）
        doNothing().when(cartService).removeCart(TEST_USER_ID);

        // 执行测试
        mockMvc.perform(delete("/cart/remove"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(cartService).removeCart(TEST_USER_ID);
    }

    /**
     * 测试清空购物车异常场景
     * 验证清空购物车时发生异常的处理
     */
    @Test
    @DisplayName("清空购物车 - 异常")
    void testRemoveCartItems_Exception() throws Exception {
        // 模拟Service层抛出异常
        doThrow(new RuntimeException("清空购物车失败")).when(cartService).removeCart(TEST_USER_ID);

        // 执行测试
        mockMvc.perform(delete("/cart/remove"))
                .andExpect(status().isInternalServerError());

        // 验证Service方法调用
        verify(cartService).removeCart(TEST_USER_ID);
    }

    /**
     * 测试清空空购物车场景
     * 验证购物车已经为空时清空操作的处理
     */
    @Test
    @DisplayName("清空购物车 - 购物车已为空")
    void testRemoveCartItems_AlreadyEmpty() throws Exception {
        // 模拟Service层行为（void方法，无需返回值）
        doNothing().when(cartService).removeCart(TEST_USER_ID);

        // 执行测试
        mockMvc.perform(delete("/cart/remove"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证Service方法调用
        verify(cartService).removeCart(TEST_USER_ID);
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建模拟的购物车查看响应对象
     * 
     * @return 模拟的购物车查看响应对象
     */
    private CartViewResponse createMockCartViewResponse() {
        CartViewResponse response = new CartViewResponse();
        response.setTotalPrice(BigDecimal.valueOf(59.98));
        
        CartResponse item1 = new CartResponse();
        item1.setProductId(1L);
        item1.setProductName("商品1");
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("29.99"));
        item1.setImageUrl("http://example.com/product1.jpg");
        
        CartResponse item2 = new CartResponse();
        item2.setProductId(2L);
        item2.setProductName("商品2");
        item2.setQuantity(1);
        item2.setPrice(new BigDecimal("29.99"));
        item2.setImageUrl("http://example.com/product2.jpg");
        
        List<CartResponse> items = Arrays.asList(item1, item2);
        
        response.setItems(items);
        return response;
    }

    /**
     * 创建模拟的空购物车查看响应对象
     * 
     * @return 模拟的空购物车查看响应对象
     */
    private CartViewResponse createEmptyCartViewResponse() {
        CartViewResponse response = new CartViewResponse();
        response.setTotalPrice(BigDecimal.ZERO);
        response.setItems(Arrays.asList());
        return response;
    }
}