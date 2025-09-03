/**
 * 测试基础类
 * 提供通用的测试方法和配置，所有测试类可以继承此类
 *
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.baoleme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.baoleme.config.TestConfig;
import org.demo.baoleme.common.UserHolder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基础类
 * 提供通用的测试配置和工具方法
 */
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@Import(TestConfig.class)
@Transactional
public abstract class BaseTest {

    /**
     * MockMvc实例，用于模拟HTTP请求
     */
    @Autowired
    protected MockMvc mockMvc;

    /**
     * JSON序列化工具
     */
    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * 测试用户ID常量
     */
    protected static final Long TEST_USER_ID = 1L;
    protected static final Long TEST_MERCHANT_ID = 1L;
    protected static final Long TEST_RIDER_ID = 1L;
    protected static final Long TEST_ADMIN_ID = 1L;
    protected static final Long TEST_STORE_ID = 1L;
    protected static final Long TEST_PRODUCT_ID = 1L;
    protected static final Long TEST_ORDER_ID = 1L;

    /**
     * 测试前的初始化方法
     * 清理UserHolder中的用户信息
     */
    @BeforeEach
    public void setUp() {
        // 清理UserHolder
        UserHolder.clear();
    }

    /**
     * 创建POST请求构建器
     *
     * @param url 请求URL
     * @param requestBody 请求体对象
     * @return MockHttpServletRequestBuilder POST请求构建器
     * @throws Exception JSON序列化异常
     */
    protected MockHttpServletRequestBuilder createPostRequest(String url, Object requestBody) throws Exception {
        return MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));
    }

    /**
     * 创建PUT请求构建器
     *
     * @param url 请求URL
     * @param requestBody 请求体对象
     * @return MockHttpServletRequestBuilder PUT请求构建器
     * @throws Exception JSON序列化异常
     */
    protected MockHttpServletRequestBuilder createPutRequest(String url, Object requestBody) throws Exception {
        return MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));
    }

    /**
     * 创建GET请求构建器
     *
     * @param url 请求URL
     * @return MockHttpServletRequestBuilder GET请求构建器
     */
    protected MockHttpServletRequestBuilder createGetRequest(String url) {
        return MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON);
    }

    /**
     * 创建DELETE请求构建器
     *
     * @param url 请求URL
     * @return MockHttpServletRequestBuilder DELETE请求构建器
     */
    protected MockHttpServletRequestBuilder createDeleteRequest(String url) {
        return MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON);
    }

    /**
     * 创建带请求体的DELETE请求构建器
     *
     * @param url 请求URL
     * @param requestBody 请求体对象
     * @return MockHttpServletRequestBuilder DELETE请求构建器
     * @throws Exception JSON序列化异常
     */
    protected MockHttpServletRequestBuilder createDeleteRequest(String url, Object requestBody) throws Exception {
        return MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));
    }

    /**
     * 设置当前用户为普通用户
     *
     * @param userId 用户ID
     */
    protected void setCurrentUser(Long userId) {
        UserHolder.set(userId, "user");
    }

    /**
     * 设置当前用户为商家
     *
     * @param merchantId 商家ID
     */
    protected void setCurrentMerchant(Long merchantId) {
        UserHolder.set(merchantId, "merchant");
    }

    /**
     * 设置当前用户为骑手
     *
     * @param riderId 骑手ID
     */
    protected void setCurrentRider(Long riderId) {
        UserHolder.set(riderId, "rider");
    }

    /**
     * 设置当前用户为管理员
     *
     * @param adminId 管理员ID
     */
    protected void setCurrentAdmin(Long adminId) {
        UserHolder.set(adminId, "admin");
    }

    /**
     * 清理当前用户信息
     */
    protected void clearCurrentUser() {
        UserHolder.clear();
    }
}