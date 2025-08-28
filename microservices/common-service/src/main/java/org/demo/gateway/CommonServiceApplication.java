/**
 * 网关服务启动类
 * 负责启动API网关，处理跨服务交互和路由转发
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关服务应用程序启动类
 * 提供API路由、负载均衡、跨服务协调等功能
 */
@SpringBootApplication
public class CommonServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(CommonServiceApplication.class, args);
    }

}