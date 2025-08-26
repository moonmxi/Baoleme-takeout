/**
 * 管理员服务启动类
 * 负责启动管理员微服务，处理管理员相关的业务逻辑和统计功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.adminservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 管理员服务应用程序启动类
 * 提供系统管理、数据统计、用户管理等功能
 */
@SpringBootApplication
public class AdminServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }

}