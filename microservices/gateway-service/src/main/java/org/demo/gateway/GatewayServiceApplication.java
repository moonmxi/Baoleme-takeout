/**
 * 网关服务启动类
 * 专注于跨数据库操作的微服务网关
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.gateway;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.interceptor.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * 网关服务启动类
 * 提供跨数据库操作功能的统一网关服务
 */
@Slf4j
@SpringBootApplication
public class GatewayServiceApplication implements WebMvcConfigurer {

    /**
     * 安全拦截器
     */
    @Autowired
    private SecurityInterceptor securityInterceptor;

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
        log.info("=== 网关服务启动成功 ===");
        log.info("专注于跨数据库操作的微服务网关已就绪");
        log.info("访问地址: http://localhost:8080");
        log.info("安全认证: 已启用");
    }

    /**
     * 注册拦截器
     * 
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/api/database/**")
                .excludePathPatterns("/api/database/health", "/actuator/**");
        log.info("安全拦截器已注册");
    }

    /**
     * 跨域配置
     * 
     * @return CorsConfigurationSource 跨域配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}