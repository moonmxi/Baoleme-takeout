/**
 * Spring Security配置类
 * 配置网关服务的安全策略和访问控制
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * 配置网关服务的HTTP安全策略
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * JWT认证过滤器
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置HTTP安全过滤器链
     * 
     * @param http HttpSecurity配置对象
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，因为我们使用JWT
            .csrf(csrf -> csrf.disable())
            // 配置会话管理为无状态
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                // 允许健康检查接口公开访问
                .requestMatchers("/actuator/**").permitAll()
                // 临时允许评论接口公开访问用于测试
                .requestMatchers("/store/reviews/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            // 添加JWT认证过滤器（用于解析用户信息到UserHolder）
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}