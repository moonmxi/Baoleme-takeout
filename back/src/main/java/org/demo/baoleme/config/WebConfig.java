// src/main/java/org/demo/baoleme/config/WebConfig.java
package org.demo.baoleme.config;

import org.demo.baoleme.common.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置Web MVC相关组件，包括拦截器等
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
@Configuration
@Profile("!test")  // 非测试环境才启用此配置
public class WebConfig implements WebMvcConfigurer {

    /**
     * JWT拦截器
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
