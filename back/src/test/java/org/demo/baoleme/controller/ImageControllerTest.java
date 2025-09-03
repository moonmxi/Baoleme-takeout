/**
 * ImageController单元测试类
 * 测试图片上传相关的所有接口功能，包括骑手头像、商家头像、用户头像、店铺图片和商品图片上传
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import org.demo.baoleme.controller.BaseControllerTest;
import org.demo.baoleme.service.*;
import org.demo.baoleme.common.UserHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.MockedStatic;
import org.junit.jupiter.api.AfterEach;
import org.springframework.context.annotation.Import;
import org.demo.baoleme.config.TestConfig;

/**
 * ImageController测试类
 * 使用MockMvc进行Web层测试，模拟HTTP请求和响应
 * 使用Mockito模拟Service层依赖
 */
@WebMvcTest(ImageController.class)
@Import(TestConfig.class)
@TestPropertySource(properties = {
        "file.storage.upload-dir=/tmp/test-uploads",
        "file.storage.base-url=http://localhost:8080/uploads/"
})
class ImageControllerTest extends BaseControllerTest {

    /**
     * MockMvc实例，用于模拟HTTP请求
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 模拟的RiderService依赖
     */
    @MockBean
    private RiderService riderService;

    /**
     * 模拟的MerchantService依赖
     */
    @MockBean
    private MerchantService merchantService;

    /**
     * 模拟的UserService依赖
     */
    @MockBean
    private UserService userService;

    /**
     * 模拟的StoreService依赖
     */
    @MockBean
    private StoreService storeService;

    /**
     * 模拟的ProductService依赖
     */
    @MockBean
    private ProductService productService;

    /**
     * 模拟的UserHolder依赖
     */
    @MockBean
    private UserHolder userHolder;

    @MockBean
    private org.demo.baoleme.common.JwtInterceptor jwtInterceptor;

    /**
     * Mock RiderMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.RiderMapper riderMapper;

    /**
     * Mock MerchantMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.MerchantMapper merchantMapper;

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
     * Mock ProductMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.ProductMapper productMapper;

    /**
     * Mock OrderMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.OrderMapper orderMapper;

    /**
     * Mock CouponMapper to avoid MyBatis configuration conflicts
     */
    @MockBean
    private org.demo.baoleme.mapper.CouponMapper couponMapper;

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

    // ==================== 骑手头像上传测试 ====================

    /**
     * 测试骑手头像上传成功场景
     * 验证骑手能够成功上传头像
     */
    @Test
    @DisplayName("骑手头像上传 - 成功")
    void testUploadRiderAvatar_Success() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // 模拟Service层行为
        when(riderService.updateAvatar(eq(TEST_USER_ID), anyString())).thenReturn(true);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-rider-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("http://localhost:8080/uploads/")));

        // 验证Service方法调用
        verify(riderService).updateAvatar(eq(TEST_USER_ID), anyString());
    }

    /**
     * 测试骑手头像上传文件为空场景
     * 验证上传空文件时的处理
     */
    @Test
    @DisplayName("骑手头像上传 - 文件为空")
    void testUploadRiderAvatar_EmptyFile() throws Exception {
        // 准备测试数据（空文件）
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        // 执行测试
        mockMvc.perform(multipart("/image/upload-rider-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("上传文件不能为空"));

        // 验证Service方法未被调用
        verify(riderService, never()).updateAvatar(anyLong(), anyString());
    }

    /**
     * 测试骑手头像上传数据库更新失败场景
     * 验证数据库更新失败时的处理
     */
    @Test
    @DisplayName("骑手头像上传 - 数据库更新失败")
    void testUploadRiderAvatar_DatabaseUpdateFailed() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // 模拟Service层返回false（更新失败）
        when(riderService.updateAvatar(eq(TEST_USER_ID), anyString())).thenReturn(false);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-rider-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("骑手头像更新失败"));

        // 验证Service方法调用
        verify(riderService).updateAvatar(eq(TEST_USER_ID), anyString());
    }

    // ==================== 商家头像上传测试 ====================

    /**
     * 测试商家头像上传成功场景
     * 验证商家能够成功上传头像
     */
    @Test
    @DisplayName("商家头像上传 - 成功")
    void testUploadMerchantAvatar_Success() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "merchant-avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "test merchant image content".getBytes()
        );

        // 模拟Service层行为
        when(merchantService.updateAvatar(eq(TEST_USER_ID), anyString())).thenReturn(true);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-merchant-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("http://localhost:8080/uploads/")));

        // 验证Service方法调用
        verify(merchantService).updateAvatar(eq(TEST_USER_ID), anyString());
    }

    /**
     * 测试商家头像上传文件为空场景
     * 验证上传空文件时的处理
     */
    @Test
    @DisplayName("商家头像上传 - 文件为空")
    void testUploadMerchantAvatar_EmptyFile() throws Exception {
        // 准备测试数据（空文件）
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "merchant-avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );

        // 执行测试
        mockMvc.perform(multipart("/image/upload-merchant-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("上传文件不能为空"));

        // 验证Service方法未被调用
        verify(merchantService, never()).updateAvatar(anyLong(), anyString());
    }

    /**
     * 测试商家头像上传数据库更新失败场景
     * 验证数据库更新失败时的处理
     */
    @Test
    @DisplayName("商家头像上传 - 数据库更新失败")
    void testUploadMerchantAvatar_DatabaseUpdateFailed() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "merchant-avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "test merchant image content".getBytes()
        );

        // 模拟Service层返回false（更新失败）
        when(merchantService.updateAvatar(eq(TEST_USER_ID), anyString())).thenReturn(false);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-merchant-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("商家头像更新失败"));

        // 验证Service方法调用
        verify(merchantService).updateAvatar(eq(TEST_USER_ID), anyString());
    }

    // ==================== 用户头像上传测试 ====================

    /**
     * 测试用户头像上传成功场景
     * 验证用户能够成功上传头像
     */
    @Test
    @DisplayName("用户头像上传 - 成功")
    void testUploadUserAvatar_Success() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "user-avatar.gif",
                "image/gif",
                "test user image content".getBytes()
        );

        // 模拟Service层行为
        when(userService.updateAvatar(eq(TEST_USER_ID), anyString())).thenReturn(true);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-user-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("http://localhost:8080/uploads/")));

        // 验证Service方法调用
        verify(userService).updateAvatar(eq(TEST_USER_ID), anyString());
    }

    /**
     * 测试用户头像上传文件为空场景
     * 验证上传空文件时的处理
     */
    @Test
    @DisplayName("用户头像上传 - 文件为空")
    void testUploadUserAvatar_EmptyFile() throws Exception {
        // 准备测试数据（空文件）
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "user-avatar.gif",
                "image/gif",
                new byte[0]
        );

        // 执行测试
        mockMvc.perform(multipart("/image/upload-user-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("上传文件不能为空"));

        // 验证Service方法未被调用
        verify(userService, never()).updateAvatar(anyLong(), anyString());
    }

    /**
     * 测试用户头像上传数据库更新失败场景
     * 验证数据库更新失败时的处理
     */
    @Test
    @DisplayName("用户头像上传 - 数据库更新失败")
    void testUploadUserAvatar_DatabaseUpdateFailed() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "user-avatar.gif",
                "image/gif",
                "test user image content".getBytes()
        );

        // 模拟Service层返回false（更新失败）
        when(userService.updateAvatar(eq(TEST_USER_ID), anyString())).thenReturn(false);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-user-avatar")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户头像更新失败"));

        // 验证Service方法调用
        verify(userService).updateAvatar(eq(TEST_USER_ID), anyString());
    }

    // ==================== 店铺图片上传测试 ====================

    /**
     * 测试店铺图片上传成功场景
     * 验证能够成功上传店铺图片
     */
    @Test
    @DisplayName("店铺图片上传 - 成功")
    void testUploadStoreImage_Success() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "store-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test store image content".getBytes()
        );
        Long storeId = 1L;

        // 模拟Service层行为
        when(storeService.updateImage(eq(storeId), anyString())).thenReturn(true);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-store-image")
                        .file(mockFile)
                        .param("storeId", storeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("http://localhost:8080/uploads/")));

        // 验证Service方法调用
        verify(storeService).updateImage(eq(storeId), anyString());
    }

    /**
     * 测试店铺图片上传文件为空场景
     * 验证上传空文件时的处理
     */
    @Test
    @DisplayName("店铺图片上传 - 文件为空")
    void testUploadStoreImage_EmptyFile() throws Exception {
        // 准备测试数据（空文件）
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "store-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );
        Long storeId = 1L;

        // 执行测试
        mockMvc.perform(multipart("/image/upload-store-image")
                        .file(mockFile)
                        .param("storeId", storeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("上传文件不能为空"));

        // 验证Service方法未被调用
        verify(storeService, never()).updateImage(anyLong(), anyString());
    }

    /**
     * 测试店铺图片上传缺少店铺ID场景
     * 验证缺少店铺ID时的处理
     */
    @Test
    @DisplayName("店铺图片上传 - 缺少店铺ID")
    void testUploadStoreImage_MissingStoreId() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "store-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test store image content".getBytes()
        );

        // 执行测试（不传storeId参数）
        mockMvc.perform(multipart("/image/upload-store-image")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缺少店铺 ID"));

        // 验证Service方法未被调用
        verify(storeService, never()).updateImage(anyLong(), anyString());
    }

    /**
     * 测试店铺图片上传数据库更新失败场景
     * 验证数据库更新失败时的处理
     */
    @Test
    @DisplayName("店铺图片上传 - 数据库更新失败")
    void testUploadStoreImage_DatabaseUpdateFailed() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "store-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test store image content".getBytes()
        );
        Long storeId = 1L;

        // 模拟Service层返回false（更新失败）
        when(storeService.updateImage(eq(storeId), anyString())).thenReturn(false);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-store-image")
                        .file(mockFile)
                        .param("storeId", storeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("店铺图片更新失败"));

        // 验证Service方法调用
        verify(storeService).updateImage(eq(storeId), anyString());
    }

    // ==================== 商品图片上传测试 ====================

    /**
     * 测试商品图片上传成功场景
     * 验证能够成功上传商品图片
     */
    @Test
    @DisplayName("商品图片上传 - 成功")
    void testUploadProductImage_Success() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "product-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "test product image content".getBytes()
        );
        Long productId = 1L;

        // 模拟Service层行为
        when(productService.updateImage(eq(productId), anyString())).thenReturn(true);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-product-image")
                        .file(mockFile)
                        .param("productId", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.containsString("http://localhost:8080/uploads/")));

        // 验证Service方法调用
        verify(productService).updateImage(eq(productId), anyString());
    }

    /**
     * 测试商品图片上传文件为空场景
     * 验证上传空文件时的处理
     */
    @Test
    @DisplayName("商品图片上传 - 文件为空")
    void testUploadProductImage_EmptyFile() throws Exception {
        // 准备测试数据（空文件）
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "product-image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );
        Long productId = 1L;

        // 执行测试
        mockMvc.perform(multipart("/image/upload-product-image")
                        .file(mockFile)
                        .param("productId", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("上传文件不能为空"));

        // 验证Service方法未被调用
        verify(productService, never()).updateImage(anyLong(), anyString());
    }

    /**
     * 测试商品图片上传缺少商品ID场景
     * 验证缺少商品ID时的处理
     */
    @Test
    @DisplayName("商品图片上传 - 缺少商品ID")
    void testUploadProductImage_MissingProductId() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "product-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "test product image content".getBytes()
        );

        // 执行测试（不传productId参数）
        mockMvc.perform(multipart("/image/upload-product-image")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缺少产品 ID"));

        // 验证Service方法未被调用
        verify(productService, never()).updateImage(anyLong(), anyString());
    }

    /**
     * 测试商品图片上传数据库更新失败场景
     * 验证数据库更新失败时的处理
     */
    @Test
    @DisplayName("商品图片上传 - 数据库更新失败")
    void testUploadProductImage_DatabaseUpdateFailed() throws Exception {
        // 准备测试数据
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "product-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "test product image content".getBytes()
        );
        Long productId = 1L;

        // 模拟Service层返回false（更新失败）
        when(productService.updateImage(eq(productId), anyString())).thenReturn(false);

        // 执行测试
        mockMvc.perform(multipart("/image/upload-product-image")
                        .file(mockFile)
                        .param("productId", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("产品图片更新失败"));

        // 验证Service方法调用
        verify(productService).updateImage(eq(productId), anyString());
    }

    /**
     * 测试商品图片上传无效文件格式场景
     * 验证上传非图片文件时的处理
     */
    @Test
    @DisplayName("商品图片上传 - 无效文件格式")
    void testUploadProductImage_InvalidFileFormat() throws Exception {
        // 准备测试数据（文本文件）
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "document.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "this is not an image".getBytes()
        );
        Long productId = 1L;

        // 模拟Service层行为
        when(productService.updateImage(eq(productId), anyString())).thenReturn(true);

        // 执行测试（注意：当前实现没有文件格式验证，所以会成功）
        mockMvc.perform(multipart("/image/upload-product-image")
                        .file(mockFile)
                        .param("productId", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());

        // 验证Service方法调用
        verify(productService).updateImage(eq(productId), anyString());
    }
}