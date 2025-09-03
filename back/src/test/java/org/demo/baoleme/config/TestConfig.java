/**
 * 测试配置类
 * 用于统一管理测试环境的配置和Mock对象
 *
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.baoleme.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.mockito.Mockito;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.demo.baoleme.common.JwtUtils;
import org.demo.baoleme.common.JwtInterceptor;
import org.springframework.boot.test.mock.mockito.MockBean;


import javax.sql.DataSource;

/**
 * 测试环境配置类
 * 提供测试所需的Bean配置和Mock对象
 */
@TestConfiguration
public class TestConfig {

    /**
     * 配置测试环境的数据源
     *
     * @return DataSource 内存数据库数据源
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("testdb")
                .build();
    }

    // 移除MyBatis配置，改用@MockBean在各测试类中Mock具体的Mapper
    // 这样可以避免复杂的MyBatis配置问题

    /**
     * 配置测试环境的密码编码器
     *
     * @return BCryptPasswordEncoder 密码编码器实例
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置测试环境的Redis连接工厂
     *
     * @return LettuceConnectionFactory Redis连接工厂实例
     */
    @Bean
    @Primary
    public LettuceConnectionFactory lettuceConnectionFactory() {
        // 返回一个模拟的 LettuceConnectionFactory
        return Mockito.mock(LettuceConnectionFactory.class);
    }

    /**
     * 配置测试环境的Redis连接工厂（通用接口）
     *
     * @return RedisConnectionFactory Redis连接工厂实例
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        // 返回一个模拟的 RedisConnectionFactory
        return Mockito.mock(RedisConnectionFactory.class);
    }

    /**
     * 配置测试环境的RedisTemplate
     *
     * @return RedisTemplate Redis操作模板
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = Mockito.mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOps = Mockito.mock(ValueOperations.class);
        Mockito.when(template.opsForValue()).thenReturn(valueOps);
        return template;
    }

    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate() {
        return Mockito.mock(StringRedisTemplate.class);
    }

    /**
     * Mock JWT工具类
     * 在测试环境中提供JWT相关功能的Mock实现
     *
     * @return JwtUtils Mock对象
     */
    @Bean
    @Primary
    public JwtUtils jwtUtils() {
        return Mockito.mock(JwtUtils.class);
    }

    // JwtInterceptor 使用 @MockBean 在各个测试类中单独配置
    // 避免与主应用中的 @Component 注解冲突
}