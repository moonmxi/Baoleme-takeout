/**
 * 健康检查控制器
 * 提供服务健康状态检查和监控功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward.controller;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.forward.config.RouteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器类
 * 提供服务自身和下游服务的健康状态检查
 */
@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * REST模板，用于检查下游服务
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 路由配置
     */
    @Autowired
    private RouteConfiguration routeConfiguration;

    /**
     * 服务启动时间
     */
    private final LocalDateTime startTime = LocalDateTime.now();

    /**
     * 基础健康检查
     * 
     * @return ResponseEntity 健康状态响应
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        try {
            healthInfo.put("status", "UP");
            healthInfo.put("service", "gateway-forward-service");
            healthInfo.put("version", "1.0.0");
            healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            healthInfo.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            healthInfo.put("uptime", calculateUptime());
            
            // 添加系统信息
            Map<String, Object> systemInfo = new HashMap<>();
            Runtime runtime = Runtime.getRuntime();
            systemInfo.put("processors", runtime.availableProcessors());
            systemInfo.put("totalMemory", formatBytes(runtime.totalMemory()));
            systemInfo.put("freeMemory", formatBytes(runtime.freeMemory()));
            systemInfo.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
            systemInfo.put("maxMemory", formatBytes(runtime.maxMemory()));
            
            healthInfo.put("system", systemInfo);
            
            log.debug("健康检查完成: 服务状态正常");
            return ResponseEntity.ok(healthInfo);
            
        } catch (Exception e) {
            log.error("健康检查失败", e);
            healthInfo.put("status", "DOWN");
            healthInfo.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(healthInfo);
        }
    }

    /**
     * 详细健康检查（包括下游服务）
     * 
     * @return ResponseEntity 详细健康状态响应
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        boolean allServicesHealthy = true;
        
        try {
            // 基础服务信息
            healthInfo.put("service", "gateway-forward-service");
            healthInfo.put("version", "1.0.0");
            healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            healthInfo.put("uptime", calculateUptime());
            
            // 检查下游服务健康状态
            Map<String, Object> downstreamServices = new HashMap<>();
            
            if (routeConfiguration.getServices() != null) {
                for (Map.Entry<String, RouteConfiguration.ServiceConfig> entry : 
                     routeConfiguration.getServices().entrySet()) {
                    
                    String serviceName = entry.getKey();
                    RouteConfiguration.ServiceConfig serviceConfig = entry.getValue();
                    
                    Map<String, Object> serviceHealth = checkServiceHealth(serviceName, serviceConfig);
                    downstreamServices.put(serviceName, serviceHealth);
                    
                    if (!"UP".equals(serviceHealth.get("status"))) {
                        allServicesHealthy = false;
                    }
                }
            }
            
            healthInfo.put("downstreamServices", downstreamServices);
            healthInfo.put("status", allServicesHealthy ? "UP" : "DEGRADED");
            
            // 添加路由配置信息
            Map<String, Object> routeInfo = new HashMap<>();
            if (routeConfiguration.getRoutes() != null) {
                routeInfo.put("totalRoutes", routeConfiguration.getRoutes().size());
                routeInfo.put("totalServices", routeConfiguration.getServices() != null ? 
                             routeConfiguration.getServices().size() : 0);
            }
            healthInfo.put("routing", routeInfo);
            
            HttpStatus status = allServicesHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
            log.info("详细健康检查完成: 整体状态={}, 下游服务数={}", 
                    allServicesHealthy ? "正常" : "降级", downstreamServices.size());
            
            return ResponseEntity.status(status).body(healthInfo);
            
        } catch (Exception e) {
            log.error("详细健康检查失败", e);
            healthInfo.put("status", "DOWN");
            healthInfo.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(healthInfo);
        }
    }

    /**
     * 检查单个服务的健康状态
     * 
     * @param serviceName 服务名称
     * @param serviceConfig 服务配置
     * @return Map 服务健康状态信息
     */
    private Map<String, Object> checkServiceHealth(String serviceName, 
                                                   RouteConfiguration.ServiceConfig serviceConfig) {
        Map<String, Object> serviceHealth = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            String healthCheckUrl = serviceConfig.getUrl() + 
                    (serviceConfig.getHealthCheck() != null ? serviceConfig.getHealthCheck() : "/actuator/health");
            
            ResponseEntity<String> response = restTemplate.getForEntity(healthCheckUrl, String.class);
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (response.getStatusCode().is2xxSuccessful()) {
                serviceHealth.put("status", "UP");
                serviceHealth.put("responseTime", responseTime + "ms");
                serviceHealth.put("url", serviceConfig.getUrl());
                log.debug("服务 {} 健康检查通过 ({}ms)", serviceName, responseTime);
            } else {
                serviceHealth.put("status", "DOWN");
                serviceHealth.put("error", "HTTP " + response.getStatusCode());
                serviceHealth.put("responseTime", responseTime + "ms");
                log.warn("服务 {} 健康检查失败: HTTP {}", serviceName, response.getStatusCode());
            }
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            serviceHealth.put("status", "DOWN");
            serviceHealth.put("error", e.getMessage());
            serviceHealth.put("responseTime", responseTime + "ms");
            log.warn("服务 {} 健康检查异常: {}", serviceName, e.getMessage());
        }
        
        return serviceHealth;
    }

    /**
     * 计算服务运行时间
     * 
     * @return String 运行时间描述
     */
    private String calculateUptime() {
        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(startTime, now).getSeconds();
        
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (days > 0) {
            return String.format("%d天 %d小时 %d分钟 %d秒", days, hours, minutes, secs);
        } else if (hours > 0) {
            return String.format("%d小时 %d分钟 %d秒", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%d分钟 %d秒", minutes, secs);
        } else {
            return String.format("%d秒", secs);
        }
    }

    /**
     * 格式化字节数
     * 
     * @param bytes 字节数
     * @return String 格式化后的字符串
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}