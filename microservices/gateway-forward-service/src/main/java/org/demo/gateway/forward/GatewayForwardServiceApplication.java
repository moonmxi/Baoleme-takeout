/**
 * 网关转发服务启动类
 * 负责启动高性能接口转发服务，将HTTP请求转发到对应的微服务端点
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 网关转发服务应用程序启动类
 * 提供高性能的HTTP请求转发功能，支持所有微服务的接口路由
 */
@Slf4j
@SpringBootApplication
public class GatewayForwardServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayForwardServiceApplication.class, args);
    }

    /**
     * 应用启动完成事件监听器
     * 打印服务启动信息和访问地址
     * 
     * @param event 应用就绪事件
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8090");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            log.info("\n" +
                    "=================================================================\n" +
                    "  网关转发服务启动成功！\n" +
                    "  服务端口: {}\n" +
                    "  本地访问: http://localhost:{}{}\n" +
                    "  网络访问: http://{}:{}{}\n" +
                    "  健康检查: http://localhost:{}{}/actuator/health\n" +
                    "  API文档: 支持转发所有微服务接口\n" +
                    "=================================================================\n",
                    port, port, contextPath, hostAddress, port, contextPath, port, contextPath);
        } catch (UnknownHostException e) {
            log.warn("无法获取本机IP地址: {}", e.getMessage());
            log.info("\n" +
                    "=================================================================\n" +
                    "  网关转发服务启动成功！\n" +
                    "  服务端口: {}\n" +
                    "  本地访问: http://localhost:{}{}\n" +
                    "  健康检查: http://localhost:{}{}/actuator/health\n" +
                    "=================================================================\n",
                    port, port, contextPath, port, contextPath);
        }
    }
}