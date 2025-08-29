/**
 * JWT安全配置类
 * 配置Spring Security以支持JWT认证
 * 集成JWT认证过滤器到安全过滤器链中
 * 
 * 主要功能：
 * 1. 配置JWT认证过滤器
 * 2. 设置安全策略和访问控制
 * 3. 配置CORS跨域支持
 * 4. 定义公开访问的端点
 * 5. 处理认证异常
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.config;

import org.demo.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * JWT安全配置
 * 配置基于JWT的无状态认证机制
 */
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {

    /**
     * JWT认证过滤器
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     * 
     * @param http HttpSecurity配置对象
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF，因为使用JWT无状态认证
            .csrf(AbstractHttpConfigurer::disable)
            
            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 配置会话管理为无状态
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置授权规则
            .authorizeHttpRequests(authz -> authz
                // 公开访问的端点
                .requestMatchers("/", "/api/database/health", "/actuator/**", "/error").permitAll()
                
                // OPTIONS请求允许通过（CORS预检请求）
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 数据库API需要认证但不需要特定角色权限
                .requestMatchers("/api/database/**").authenticated()
                
                // 所有其他请求需要认证
                .anyRequest().authenticated()
            )
            
            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 禁用默认的登录页面
            .formLogin(AbstractHttpConfigurer::disable)
            
            // 禁用HTTP Basic认证
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * 配置CORS跨域资源共享
     * 
     * @return CorsConfigurationSource CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(List.of("*"));
        
        // 允许发送认证信息
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "Accept", 
            "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}