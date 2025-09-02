/**
 * 路由配置类
 * 定义微服务路由映射规则和服务端点配置
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 路由配置属性类
 * 从application.yml中读取路由配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.forward")
public class RouteConfiguration {

    /**
     * 微服务配置映射
     */
    private Map<String, ServiceConfig> services;

    /**
     * 路由规则列表
     */
    private List<RouteRule> routes;

    /**
     * 连接池配置
     */
    private ConnectionPoolConfig connectionPool;

    /**
     * 重试配置
     */
    private RetryConfig retry;

    /**
     * 熔断器配置
     */
    private CircuitBreakerConfig circuitBreaker;

    /**
     * 微服务配置类
     */
    @Data
    public static class ServiceConfig {
        /**
         * 服务URL
         */
        private String url;

        /**
         * 健康检查端点
         */
        private String healthCheck;

        /**
         * 请求超时时间（毫秒）
         */
        private int timeout = 30000;
    }

    /**
     * 路由规则类
     */
    @Data
    public static class RouteRule {
        /**
         * 路径匹配模式
         */
        private String path;

        /**
         * 目标服务名称
         */
        private String service;

        /**
         * 是否去除路径前缀
         */
        private boolean stripPrefix = false;
    }

    /**
     * 连接池配置类
     */
    @Data
    public static class ConnectionPoolConfig {
        /**
         * 最大连接数
         */
        private int maxTotal = 200;

        /**
         * 每个路由的最大连接数
         */
        private int maxPerRoute = 50;

        /**
         * 连接超时时间（毫秒）
         */
        private int connectionTimeout = 5000;

        /**
         * Socket超时时间（毫秒）
         */
        private int socketTimeout = 30000;

        /**
         * 连接请求超时时间（毫秒）
         */
        private int connectionRequestTimeout = 5000;

        /**
         * 连接空闲验证时间（毫秒）
         */
        private int validateAfterInactivity = 2000;
    }

    /**
     * 重试配置类
     */
    @Data
    public static class RetryConfig {
        /**
         * 最大重试次数
         */
        private int maxAttempts = 3;

        /**
         * 重试延迟时间（毫秒）
         */
        private long delay = 1000;

        /**
         * 延迟倍数
         */
        private double multiplier = 2.0;
    }

    /**
     * 熔断器配置类
     */
    @Data
    public static class CircuitBreakerConfig {
        /**
         * 失败阈值
         */
        private int failureThreshold = 5;

        /**
         * 超时时间（毫秒）
         */
        private long timeout = 60000;

        /**
         * 重置超时时间（毫秒）
         */
        private long resetTimeout = 30000;
    }
}