/**
 * 测试应用启动类
 * 专门用于测试环境，排除MyBatis自动配置以避免Mapper扫描问题
 *
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.baoleme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * 测试专用的Spring Boot应用类
 * 排除MyBatis相关的自动配置，避免在测试环境中出现Mapper扫描问题
 */
@TestConfiguration
@EnableWebSocket
@ComponentScan(basePackages = "org.demo.baoleme", excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.demo\\.baoleme\\.mapper\\..*")
})
public class TestApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}