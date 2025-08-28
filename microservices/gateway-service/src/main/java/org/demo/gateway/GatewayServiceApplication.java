/**
 * 网关服务启动类
 * 统一数据库操作网关服务，提供跨数据库的CRUD操作和数据同步功能
 * 
 * 主要功能：
 * 1. 多数据源动态路由
 * 2. 统一数据库操作API
 * 3. 跨数据库事务支持
 * 4. 数据同步和一致性保证
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关服务启动类
 * 提供统一的数据库操作网关服务
 */
@Slf4j
@SpringBootApplication
@RestController
public class GatewayServiceApplication {

    /**
     * 应用启动入口
     * 
     * @param args 启动参数
     */
    public static void main(String[] args) {
        log.info("启动网关数据库操作服务...");
        SpringApplication.run(GatewayServiceApplication.class, args);
        log.info("网关数据库操作服务启动完成");
        log.info("=== 网关服务功能 ===");
        log.info("1. 多数据源动态路由");
        log.info("2. 统一数据库操作API");
        log.info("3. 跨数据库CRUD操作");
        log.info("4. 数据同步和一致性保证");
        log.info("访问地址: http://localhost:8080");
    }

    /**
     * 根路径健康检查
     * 
     * @return 服务状态
     */
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "gateway-database-service");
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", "Gateway Database Service is running");
        response.put("features", new String[]{
            "Multi-DataSource Dynamic Routing",
            "Unified Database Operations API",
            "Cross-Database CRUD Operations",
            "Data Synchronization Support"
        });
        return response;
    }
}