/**
 * HTTP客户端配置类
 * 配置高性能的HTTP连接池和客户端
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * HTTP客户端配置类
 * 提供高性能的HTTP客户端和连接池配置
 */
@Slf4j
@Configuration
public class HttpClientConfiguration {

    /**
     * 路由配置
     */
    @Autowired
    private RouteConfiguration routeConfiguration;

    /**
     * 创建HTTP连接池管理器
     * 
     * @return PoolingHttpClientConnectionManager 连接池管理器
     */
    @Bean
    public PoolingHttpClientConnectionManager connectionManager() {
        RouteConfiguration.ConnectionPoolConfig poolConfig = routeConfiguration.getConnectionPool();
        
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        
        // 设置最大连接数
        connectionManager.setMaxTotal(poolConfig.getMaxTotal());
        
        // 设置每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(poolConfig.getMaxPerRoute());
        
        // 设置连接空闲验证时间
        connectionManager.setValidateAfterInactivity(Timeout.ofMilliseconds(poolConfig.getValidateAfterInactivity()));
        
        log.info("HTTP连接池配置完成 - 最大连接数: {}, 每路由最大连接数: {}, 空闲验证时间: {}ms", 
                poolConfig.getMaxTotal(), poolConfig.getMaxPerRoute(), poolConfig.getValidateAfterInactivity());
        
        return connectionManager;
    }

    /**
     * 创建请求配置
     * 
     * @return RequestConfig 请求配置
     */
    @Bean
    public RequestConfig requestConfig() {
        RouteConfiguration.ConnectionPoolConfig poolConfig = routeConfiguration.getConnectionPool();
        
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(poolConfig.getConnectionRequestTimeout()))
                .setResponseTimeout(Timeout.ofMilliseconds(poolConfig.getSocketTimeout()))
                .build();
        
        log.info("HTTP请求配置完成 - 连接请求超时: {}ms, 响应超时: {}ms", 
                poolConfig.getConnectionRequestTimeout(), poolConfig.getSocketTimeout());
        
        return requestConfig;
    }

    /**
     * 创建HTTP客户端
     * 
     * @param connectionManager 连接池管理器
     * @param requestConfig 请求配置
     * @return CloseableHttpClient HTTP客户端
     */
    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connectionManager, 
                                         RequestConfig requestConfig) {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(Timeout.ofSeconds(30))
                .build();
        
        log.info("HTTP客户端创建完成，支持连接池和自动清理过期连接");
        
        return httpClient;
    }

    /**
     * 创建RestTemplate
     * 
     * @param httpClient HTTP客户端
     * @return RestTemplate REST模板
     */
    @Bean
    public RestTemplate restTemplate(CloseableHttpClient httpClient) {
        RouteConfiguration.ConnectionPoolConfig poolConfig = routeConfiguration.getConnectionPool();
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(poolConfig.getConnectionTimeout());
        factory.setConnectionRequestTimeout(poolConfig.getConnectionRequestTimeout());
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        log.info("RestTemplate创建完成 - 连接超时: {}ms, 连接请求超时: {}ms", 
                poolConfig.getConnectionTimeout(), poolConfig.getConnectionRequestTimeout());
        
        return restTemplate;
    }

    /**
     * 连接池监控任务
     * 定期清理过期和空闲连接
     */
    @Bean
    public Runnable connectionPoolMonitor(PoolingHttpClientConnectionManager connectionManager) {
        return () -> {
            try {
                // 清理过期连接
                connectionManager.closeExpired();
                
                // 清理空闲超过30秒的连接
                connectionManager.closeIdle(Timeout.ofSeconds(30));
                
                log.debug("连接池清理完成 - 总连接数: {}, 租用连接数: {}, 可用连接数: {}", 
                        connectionManager.getTotalStats().getMax(),
                        connectionManager.getTotalStats().getLeased(),
                        connectionManager.getTotalStats().getAvailable());
            } catch (Exception e) {
                log.error("连接池清理异常", e);
            }
        };
    }
}