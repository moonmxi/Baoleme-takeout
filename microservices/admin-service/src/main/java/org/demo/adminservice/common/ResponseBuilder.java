/**
 * 响应构建器工具类
 * 提供快捷构建统一响应体的方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.adminservice.common;

/**
 * 快捷构建统一响应体
 */
public class ResponseBuilder {

    /**
     * 构建成功响应（无数据）
     * 
     * @return 成功响应对象
     */
    public static CommonResponse ok() {
        return CommonResponse.builder()
                .success(true)
                .code(200)
                .message("success")
                .build();
    }

    /**
     * 构建成功响应（带数据）
     * 
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static CommonResponse ok(Object data) {
        return CommonResponse.builder()
                .success(true)
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    /**
     * 构建失败响应
     * 
     * @param message 失败消息
     * @return 失败响应对象
     */
    public static CommonResponse fail(String message) {
        return CommonResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .build();
    }

    /**
     * 构建失败响应（带数据）
     * 
     * @param message 失败消息
     * @param data 响应数据
     * @return 失败响应对象
     */
    public static CommonResponse fail(String message, Object data) {
        return CommonResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 构建自定义响应
     * 
     * @param success 是否成功
     * @param code 状态码
     * @param message 响应消息
     * @param data 响应数据
     * @return 自定义响应对象
     */
    public static CommonResponse custom(boolean success, int code, String message, Object data) {
        return CommonResponse.builder()
                .success(success)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}