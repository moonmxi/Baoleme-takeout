/**
 * 用户服务启动类
 * 负责启动用户微服务，处理用户相关的业务逻辑
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务应用程序启动类
 * 提供用户注册、登录、个人信息管理等功能
 */
@SpringBootApplication
@MapperScan("org.demo.userservice.mapper")
public class UserServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}