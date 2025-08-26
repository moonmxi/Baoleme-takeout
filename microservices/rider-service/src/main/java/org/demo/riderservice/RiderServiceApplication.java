/**
 * 骑手服务启动类
 * 负责启动骑手微服务，处理骑手相关的业务逻辑和配送功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.riderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 骑手服务应用程序启动类
 * 提供骑手注册、订单接单、配送管理等功能
 */
@SpringBootApplication
public class RiderServiceApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(RiderServiceApplication.class, args);
    }

}