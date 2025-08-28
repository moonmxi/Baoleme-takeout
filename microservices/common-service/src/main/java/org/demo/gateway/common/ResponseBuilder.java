/**
 * 响应构建器工具类
 * 用于快速构建统一格式的API响应
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.common;

/**
 * 响应构建器类
 * 提供静态方法快速构建成功和失败的响应
 */
public class ResponseBuilder {

    /**
     * 成功响应状态码
     */
    private static final Integer SUCCESS_CODE = 200;

    /**
     * 失败响应状态码
     */
    private static final Integer FAIL_CODE = 400;

    /**
     * 服务器错误状态码
     */
    private static final Integer ERROR_CODE = 500;

    /**
     * 构建成功响应（带数据）
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return CommonResponse 成功响应对象
     */
    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(SUCCESS_CODE, "操作成功", data);
    }

    /**
     * 构建成功响应（无数据）
     * 
     * @return CommonResponse 成功响应对象
     */
    public static CommonResponse<Void> ok() {
        return new CommonResponse<>(SUCCESS_CODE, "操作成功", null);
    }

    /**
     * 构建成功响应（自定义消息）
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return CommonResponse 成功响应对象
     */
    public static <T> CommonResponse<T> ok(String message, T data) {
        return new CommonResponse<>(SUCCESS_CODE, message, data);
    }

    /**
     * 构建失败响应
     * 
     * @param message 错误消息
     * @return CommonResponse 失败响应对象
     */
    public static CommonResponse<Void> fail(String message) {
        return new CommonResponse<>(FAIL_CODE, message, null);
    }

    /**
     * 构建失败响应（带数据）
     * 
     * @param message 错误消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return CommonResponse 失败响应对象
     */
    public static <T> CommonResponse<T> fail(String message, T data) {
        return new CommonResponse<>(FAIL_CODE, message, data);
    }

    /**
     * 构建服务器错误响应
     * 
     * @param message 错误消息
     * @return CommonResponse 错误响应对象
     */
    public static CommonResponse<Void> error(String message) {
        return new CommonResponse<>(ERROR_CODE, message, null);
    }
}