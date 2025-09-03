/**
 * ProductController单元测试类
 * 测试商品相关的所有接口功能，包括创建、查看、更新、删除等操作
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.product.*;
import org.demo.baoleme.dto.request.user.UserGetProductInfoRequest;
import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.pojo.Review;
import org.demo.baoleme.service.ProductService;
import org.demo.baoleme.service.ReviewService;
import org.demo.baoleme.service.SalesStatsService;
import org.demo.baoleme.service.StoreService;
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
 * ProductController测试类
 * 使用MockMvc进行Web层测试，模拟HTTP请求和响应
 * 使用Mockito模拟Service层依赖
 */
@SpringBootTest(classes = org.demo.baoleme.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class ProductControllerTest {

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
     * 模拟的ProductService依赖
     */
    @MockBean
    private ProductService productService;

    /**
     * 模拟的StoreService依赖
     */
    @MockBean
    private StoreService storeService;

    /**
     * 模拟的ReviewService依赖
     */
    @MockBean
    private ReviewService reviewService;

    /**
     * 模拟的SalesStatsService依赖
     */
    @MockBean
    private SalesStatsService salesStatsService;

    /**
     * Mock ProductMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.ProductMapper productMapper;

    /**
     * Mock StoreMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.StoreMapper storeMapper;

    /**
     * Mock ReviewMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.ReviewMapper reviewMapper;

    /**
     * Mock SaleMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.SaleMapper saleMapper;

    /**
     * 测试用商品数据
     */
    private Product testProduct;
    
    /**
     * 测试用JWT令牌
     */
    private static final String TEST_TOKEN = "test-jwt-token";
    
    /**
     * 测试用用户ID
     */
    private static final Long TEST_USER_ID = 1L;
    
    /**
     * 测试用店铺ID
     */
    private static final Long TEST_STORE_ID = 1L;
    
    /**
     * 测试用商品ID
     */
    private static final Long TEST_PRODUCT_ID = 1L;

    /**
     * 测试前的初始化设置
     * 准备测试数据和模拟对象行为
     */
    @BeforeEach
    void setUp() {
        // 初始化测试商品数据
        testProduct = new Product();
        testProduct.setId(TEST_PRODUCT_ID);
        testProduct.setStoreId(TEST_STORE_ID);
        testProduct.setName("测试商品");
        testProduct.setDescription("测试商品描述");
        testProduct.setPrice(new BigDecimal("29.99"));
        testProduct.setCategory("测试分类");
        testProduct.setStock(100);
        testProduct.setImage("http://example.com/product.jpg");
        testProduct.setStatus(1);
        testProduct.setRating(new BigDecimal("4.5"));
        testProduct.setCreatedAt(LocalDateTime.now());
    }

    // ==================== 创建商品测试 ====================

    /**
     * 测试创建商品成功场景
     * 验证商品能够成功创建
     */
    @Test
    @DisplayName("创建商品 - 成功")
    void testCreateProduct_Success() throws Exception {
        // 准备测试数据
        ProductCreateRequest request = new ProductCreateRequest();
        request.setStoreId(TEST_STORE_ID);
        request.setName("新商品");
        request.setDescription("新商品描述");
        request.setPrice(new BigDecimal("19.99"));
        request.setCategory("新分类");
        request.setStock(50);
        request.setImage("http://example.com/new-product.jpg");

        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID)).thenReturn(true);
            when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

            // 执行测试
            mockMvc.perform(post("/product/create")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.productId").value(TEST_PRODUCT_ID));
        }

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID);
        verify(productService).createProduct(any(Product.class));
    }

    /**
     * 测试创建商品失败场景（无权限）
     * 验证无权限时的处理
     */
    @Test
    @DisplayName("创建商品 - 无权限")
    void testCreateProduct_NoPermission() throws Exception {
        // 准备测试数据
        ProductCreateRequest request = new ProductCreateRequest();
        request.setStoreId(TEST_STORE_ID);
        request.setName("新商品");
        request.setPrice(new BigDecimal("19.99"));
        request.setStock(50);

        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID)).thenReturn(false);

            // 执行测试
            mockMvc.perform(post("/product/create")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("商品创建失败，商家没有权限"));
        }

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID);
        verify(productService, never()).createProduct(any(Product.class));
    }

    /**
     * 测试创建商品失败场景（创建失败）
     * 验证创建失败时的处理
     */
    @Test
    @DisplayName("创建商品 - 创建失败")
    void testCreateProduct_CreateFailed() throws Exception {
        // 准备测试数据
        ProductCreateRequest request = new ProductCreateRequest();
        request.setStoreId(TEST_STORE_ID);
        request.setName("新商品");
        request.setPrice(new BigDecimal("19.99"));
        request.setStock(50);

        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID)).thenReturn(true);
            when(productService.createProduct(any(Product.class))).thenReturn(null);

            // 执行测试
            mockMvc.perform(post("/product/create")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("商品创建失败，请检查输入参数"));
        }

        // 验证Service方法调用
        verify(productService).createProduct(any(Product.class));
    }

    // ==================== 查看商品测试 ====================

    /**
     * 测试查看商品成功场景
     * 验证能够成功查看商品详情
     */
    @Test
    @DisplayName("查看商品 - 成功")
    void testGetProductById_Success() throws Exception {
        // 准备测试数据
        ProductViewRequest request = new ProductViewRequest();
        request.setProductId(TEST_PRODUCT_ID);
        request.setStoreId(TEST_STORE_ID);

        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(productService.getProductById(TEST_PRODUCT_ID)).thenReturn(testProduct);
            when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID)).thenReturn(true);

            // 执行测试
            mockMvc.perform(post("/product/view")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(TEST_PRODUCT_ID))
                    .andExpect(jsonPath("$.data.name").value("测试商品"));
        }

        // 验证Service方法调用
        verify(productService).getProductById(TEST_PRODUCT_ID);
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID);
    }

    /**
     * 测试查看商品失败场景（商品不存在）
     * 验证商品不存在时的处理
     */
    @Test
    @DisplayName("查看商品 - 商品不存在")
    void testGetProductById_ProductNotExists() throws Exception {
        // 准备测试数据
        ProductViewRequest request = new ProductViewRequest();
        request.setProductId(999L);
        request.setStoreId(TEST_STORE_ID);

        // 模拟Service层行为
        when(productService.getProductById(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(post("/product/view")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("商品不存在"));

        // 验证Service方法调用
        verify(productService).getProductById(999L);
    }

    // ==================== 获取店铺商品测试 ====================

    /**
     * 测试获取店铺商品成功场景
     * 验证能够成功获取店铺商品列表
     */
    @Test
    @DisplayName("获取店铺商品 - 成功")
    void testGetProductsByStore_Success() throws Exception {
        // 准备测试数据
        ProductViewRequest request = new ProductViewRequest();
        request.setStoreId(TEST_STORE_ID);
        request.setPage(1);
        request.setPageSize(10);

        Page<Product> page = new Page<>();
        page.setList(Arrays.asList(testProduct));
        page.setCurrPage(1);
        page.setPageCount(1);
        page.setCount(1);

        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID)).thenReturn(true);
            when(productService.getProductsByStore(TEST_STORE_ID, 1, 10)).thenReturn(page);
            when(salesStatsService.getProductVolume(TEST_PRODUCT_ID)).thenReturn(100);

            // 执行测试
            mockMvc.perform(post("/product/store-products")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.products").isArray())
                    .andExpect(jsonPath("$.data.currentPage").value(1))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.totalItems").value(1));
        }

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID);
        verify(productService).getProductsByStore(TEST_STORE_ID, 1, 10);
        verify(salesStatsService).getProductVolume(TEST_PRODUCT_ID);
    }

    /**
     * 测试获取店铺商品失败场景（无权限）
     * 验证无权限时的处理
     */
    @Test
    @DisplayName("获取店铺商品 - 无权限")
    void testGetProductsByStore_NoPermission() throws Exception {
        // 准备测试数据
        ProductViewRequest request = new ProductViewRequest();
        request.setStoreId(TEST_STORE_ID);
        request.setPage(1);
        request.setPageSize(10);

        // 模拟UserHolder和Service层行为
        try (MockedStatic<UserHolder> mockedUserHolder = mockStatic(UserHolder.class)) {
            mockedUserHolder.when(UserHolder::getId).thenReturn(TEST_USER_ID);
            when(storeService.validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID)).thenReturn(false);

            // 执行测试
            mockMvc.perform(post("/product/store-products")
                            .header("Authorization", "Bearer " + TEST_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("商品创建失败，商家没有权限"));
        }

        // 验证Service方法调用
        verify(storeService).validateStoreOwnership(TEST_STORE_ID, TEST_USER_ID);
        verify(productService, never()).getProductsByStore(anyLong(), anyInt(), anyInt());
    }

    // ==================== 更新商品测试 ====================

    /**
     * 测试更新商品成功场景
     * 验证能够成功更新商品信息
     */
    @Test
    @DisplayName("更新商品 - 成功")
    void testUpdateProduct_Success() throws Exception {
        // 准备测试数据
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setProductId(TEST_PRODUCT_ID);
        request.setName("更新后的商品");
        request.setPrice(new BigDecimal("39.99"));
        request.setStock(200);

        Product updatedProduct = new Product();
        updatedProduct.setId(TEST_PRODUCT_ID);
        updatedProduct.setName("更新后的商品");
        updatedProduct.setPrice(new BigDecimal("39.99"));
        updatedProduct.setStock(200);

        // 模拟Service层行为
        when(productService.updateProduct(any(Product.class))).thenReturn(true);
        when(productService.getProductById(TEST_PRODUCT_ID)).thenReturn(updatedProduct);

        // 执行测试
        mockMvc.perform(put("/product/update")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(TEST_PRODUCT_ID))
                .andExpect(jsonPath("$.data.name").value("更新后的商品"));

        // 验证Service方法调用
        verify(productService).updateProduct(any(Product.class));
        verify(productService).getProductById(TEST_PRODUCT_ID);
    }

    /**
     * 测试更新商品失败场景
     * 验证更新失败时的处理
     */
    @Test
    @DisplayName("更新商品 - 失败")
    void testUpdateProduct_Failed() throws Exception {
        // 准备测试数据
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setProductId(TEST_PRODUCT_ID);
        request.setName("更新后的商品");

        // 模拟Service层行为
        when(productService.updateProduct(any(Product.class))).thenReturn(false);

        // 执行测试
        mockMvc.perform(put("/product/update")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("更新失败，请检查商品 ID 是否存在"));

        // 验证Service方法调用
        verify(productService).updateProduct(any(Product.class));
    }

    // ==================== 更新商品状态测试 ====================

    /**
     * 测试更新商品状态成功场景
     * 验证能够成功更新商品状态
     */
    @Test
    @DisplayName("更新商品状态 - 成功")
    void testUpdateProductStatus_Success() throws Exception {
        // 准备测试数据
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setProductId(TEST_PRODUCT_ID);
        request.setStatus(0); // 下架

        // 模拟Service层行为
        when(productService.updateProductStatus(TEST_PRODUCT_ID, 0)).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/product/status")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("商品状态更新成功"));

        // 验证Service方法调用
        verify(productService).updateProductStatus(TEST_PRODUCT_ID, 0);
    }

    /**
     * 测试更新商品状态失败场景
     * 验证更新状态失败时的处理
     */
    @Test
    @DisplayName("更新商品状态 - 失败")
    void testUpdateProductStatus_Failed() throws Exception {
        // 准备测试数据
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setProductId(TEST_PRODUCT_ID);
        request.setStatus(0);

        // 模拟Service层行为
        when(productService.updateProductStatus(TEST_PRODUCT_ID, 0)).thenReturn(false);

        // 执行测试
        mockMvc.perform(put("/product/status")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("状态更新失败"));

        // 验证Service方法调用
        verify(productService).updateProductStatus(TEST_PRODUCT_ID, 0);
    }

    // ==================== 删除商品测试 ====================

    /**
     * 测试删除商品成功场景
     * 验证能够成功删除商品
     */
    @Test
    @DisplayName("删除商品 - 成功")
    void testDeleteProduct_Success() throws Exception {
        // 准备测试数据
        ProductDeleteRequest request = new ProductDeleteRequest();
        request.setId(TEST_PRODUCT_ID);

        // 模拟Service层行为
        when(productService.deleteProduct(TEST_PRODUCT_ID)).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/product/delete")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("商品删除成功"));

        // 验证Service方法调用
        verify(productService).deleteProduct(TEST_PRODUCT_ID);
    }

    /**
     * 测试删除商品失败场景
     * 验证删除失败时的处理
     */
    @Test
    @DisplayName("删除商品 - 失败")
    void testDeleteProduct_Failed() throws Exception {
        // 准备测试数据
        ProductDeleteRequest request = new ProductDeleteRequest();
        request.setId(TEST_PRODUCT_ID);

        // 模拟Service层行为
        when(productService.deleteProduct(TEST_PRODUCT_ID)).thenReturn(false);

        // 执行测试
        mockMvc.perform(post("/product/delete")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败，商品可能不存在"));

        // 验证Service方法调用
        verify(productService).deleteProduct(TEST_PRODUCT_ID);
    }

    // ==================== 获取商品信息测试 ====================

    /**
     * 测试获取商品信息成功场景
     * 验证能够成功获取商品详细信息和评论
     */
    @Test
    @DisplayName("获取商品信息 - 成功")
    void testGetProductInfo_Success() throws Exception {
        // 准备测试数据
        UserGetProductInfoRequest request = new UserGetProductInfoRequest();
        request.setId(TEST_PRODUCT_ID);

        Review testReview = new Review();
        testReview.setId(1L);
        testReview.setProductId(TEST_PRODUCT_ID);
        testReview.setRating(new BigDecimal("5"));
        testReview.setComment("很好的商品");
        List<Review> reviews = Arrays.asList(testReview);

        // 模拟Service层行为
        when(productService.getProductById(TEST_PRODUCT_ID)).thenReturn(testProduct);
        when(reviewService.getReviewsByProductId(TEST_PRODUCT_ID)).thenReturn(reviews);

        // 执行测试
        mockMvc.perform(post("/product/productInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(TEST_PRODUCT_ID))
                .andExpect(jsonPath("$.data.name").value("测试商品"))
                .andExpect(jsonPath("$.data.reviews").isArray());

        // 验证Service方法调用
        verify(productService).getProductById(TEST_PRODUCT_ID);
        verify(reviewService).getReviewsByProductId(TEST_PRODUCT_ID);
    }

    /**
     * 测试获取商品信息失败场景（商品不存在）
     * 验证商品不存在时的处理
     */
    @Test
    @DisplayName("获取商品信息 - 商品不存在")
    void testGetProductInfo_ProductNotExists() throws Exception {
        // 准备测试数据
        UserGetProductInfoRequest request = new UserGetProductInfoRequest();
        request.setId(999L);

        // 模拟Service层行为
        when(productService.getProductById(999L)).thenReturn(null);

        // 执行测试（期望抛出异常）
        mockMvc.perform(post("/product/productInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());

        // 验证Service方法调用
        verify(productService).getProductById(999L);
    }

    /**
     * 测试参数验证失败场景
     * 验证无效参数时的处理
     */
    @Test
    @DisplayName("创建商品 - 参数验证失败")
    void testCreateProduct_ValidationFailed() throws Exception {
        // 准备测试数据（无效数据）
        ProductCreateRequest request = new ProductCreateRequest();
        // 缺少必填字段

        // 执行测试
        mockMvc.perform(post("/product/create")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 验证Service方法未被调用
        verify(productService, never()).createProduct(any(Product.class));
    }
}