/**
 * WebClient配置类
 * 配置HTTP客户端用于调用其他微服务
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient配置类
 * 提供WebClient Bean用于HTTP调用
 */
@Configuration
public class WebClientConfig {

    /**
     * 创建WebClient.Builder Bean
     * 
     * @return WebClient.Builder实例
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}