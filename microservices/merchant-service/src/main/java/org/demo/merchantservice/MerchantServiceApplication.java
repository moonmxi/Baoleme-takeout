/**
 * 商家服务启动类
 * 负责启动商家微服务，处理商家、店铺、商品相关的业务逻辑
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商家服务应用程序启动类
 * 提供商家注册、店铺管理、商品管理等功能
 */
@SpringBootApplication
public class MerchantServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MerchantServiceApplication.class, args);
    }

}