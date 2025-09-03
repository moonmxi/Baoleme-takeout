/**
 * 基础控制器测试类
 * 提供通用的测试配置和Mock对象管理
 * 
 * @author Generated
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.baoleme.controller;

import org.demo.baoleme.common.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mockStatic;

/**
 * 基础控制器测试类
 * 为所有控制器测试提供通用的Mock配置和生命周期管理
 */
@ActiveProfiles("test")
public abstract class BaseControllerTest {

    /**
     * UserHolder静态Mock对象
     * 用于模拟用户上下文信息
     */
    protected MockedStatic<UserHolder> mockedUserHolder;

    /**
     * 测试前置方法
     * 初始化通用的Mock对象
     */
    @BeforeEach
    void setUpBase() {
        // 初始化UserHolder静态Mock
        mockedUserHolder = mockStatic(UserHolder.class);
    }

    /**
     * 测试后置方法
     * 清理Mock对象资源
     */
    @AfterEach
    void tearDownBase() {
        // 关闭UserHolder静态Mock
        if (mockedUserHolder != null) {
            mockedUserHolder.close();
        }
    }
}