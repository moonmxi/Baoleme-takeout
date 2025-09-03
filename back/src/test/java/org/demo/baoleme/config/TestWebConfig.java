/**
 * 测试环境Web配置类
 * 专门为测试环境配置拦截器
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.baoleme.config;

import org.demo.baoleme.common.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 测试环境Web配置类
 * 在测试环境中注册Mock的JwtInterceptor
 */
@TestConfiguration
@Profile("test")
public class TestWebConfig implements WebMvcConfigurer {

    /**
     * JWT拦截器（在测试中会被Mock）
     */
    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 添加拦截器配置
     * 注册JWT拦截器并配置拦截和排除路径
     * 
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                // 排除不需要JWT验证的路径
                .excludePathPatterns(
                        "/rider/register",
                        "/rider/login",
                        "/merchant/register",
                        "/merchant/login",
                        "/user/register",
                        "/user/login",
                        "/admin/login",
                        "/error",
                        "/images/**"    // 静态资源路径
                );
    }
}