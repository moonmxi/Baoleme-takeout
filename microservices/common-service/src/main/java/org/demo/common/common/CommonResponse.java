/**
 * 通用响应类
 * 用于统一API响应格式
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.common;

import lombok.Data;

/**
 * 通用响应类
 * 包含响应状态码、消息和数据
 * 
 * @param <T> 响应数据类型
 */
@Data
public class CommonResponse<T> {

    /**
     * 响应状态码
     * 200: 成功
     * 400: 客户端错误
     * 500: 服务器错误
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 默认构造函数
     */
    public CommonResponse() {}

    /**
     * 带参数的构造函数
     * 
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     */
    public CommonResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}