/**
 * Redis配置类
 * 配置Redis连接和RedisTemplate
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis配置类
 * 配置商家服务的Redis连接和操作模板
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate Bean
     * 
     * @param connectionFactory Lettuce连接工厂
     * @return RedisTemplate<String, Object> Redis操作模板
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}