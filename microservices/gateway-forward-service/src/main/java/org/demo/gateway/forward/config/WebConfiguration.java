/**
 * Web配置类
 * 配置Web相关组件，包括拦截器、CORS、静态资源等
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward.config;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.forward.interceptor.RequestLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Arrays;

/**
 * Web配置类
 * 提供Web层的各种配置
 */
@Slf4j
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * 请求日志拦截器
     */
    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;

    /**
     * 添加拦截器
     * 
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册请求日志拦截器
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/**",  // 排除监控端点
                        "/favicon.ico",  // 排除图标请求
                        "/error"         // 排除错误页面
                );
        
        log.info("请求日志拦截器注册完成");
    }

    /**
     * 配置CORS跨域支持
     * 
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        
        log.info("CORS跨域配置完成");
    }

    /**
     * 配置静态资源处理
     * 
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 禁用默认静态资源处理，因为这是一个纯API转发服务
        registry.addResourceHandler("/actuator/**")
                .addResourceLocations("classpath:/META-INF/resources/");
        
        log.debug("静态资源处理配置完成");
    }

    /**
     * 配置内容协商
     * 
     * @param configurer 内容协商配置器
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON)
                  .favorParameter(false)
                  .favorPathExtension(false)
                  .ignoreAcceptHeader(false);
        
        log.debug("内容协商配置完成");
    }

    /**
     * 配置异步支持
     * 
     * @param configurer 异步支持配置器
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(30000); // 30秒超时
        configurer.setTaskExecutor(null); // 使用默认执行器
        
        log.debug("异步支持配置完成");
    }

    /**
     * 创建CORS配置源
     * 
     * @return CorsConfigurationSource CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求缓存时间
        configuration.setMaxAge(3600L);
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", 
                "Accept", "Origin", "Access-Control-Request-Method", 
                "Access-Control-Request-Headers"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("CORS配置源创建完成");
        return source;
    }

    /**
     * 创建请求日志过滤器
     * 
     * @return CommonsRequestLoggingFilter 请求日志过滤器
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        
        // 包含查询字符串
        filter.setIncludeQueryString(true);
        
        // 包含请求载荷（仅用于调试，生产环境建议关闭）
        filter.setIncludePayload(false);
        
        // 包含请求头
        filter.setIncludeHeaders(false);
        
        // 包含客户端信息
        filter.setIncludeClientInfo(true);
        
        // 设置载荷最大长度
        filter.setMaxPayloadLength(1000);
        
        // 设置日志前缀
        filter.setBeforeMessagePrefix("REQUEST: ");
        filter.setAfterMessagePrefix("RESPONSE: ");
        
        log.info("请求日志过滤器创建完成");
        return filter;
    }

    /**
     * 配置路径匹配
     * 
     * @param configurer 路径匹配配置器
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 启用后缀模式匹配
        configurer.setUseSuffixPatternMatch(false);
        
        // 启用尾部斜杠匹配
        configurer.setUseTrailingSlashMatch(true);
        
        log.debug("路径匹配配置完成");
    }
}