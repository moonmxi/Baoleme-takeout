/**
 * WebClient配置类
 * 配置WebClient Bean用于HTTP客户端调用
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient配置类
 * 提供WebClient.Builder Bean用于依赖注入
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