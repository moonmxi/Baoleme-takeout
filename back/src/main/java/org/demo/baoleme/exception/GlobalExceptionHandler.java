/**
 * 全局异常处理器
 * 统一处理应用程序中的异常，提供友好的错误响应
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.baoleme.exception;

import lombok.extern.slf4j.Slf4j;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器类
 * 捕获并处理各种类型的异常，返回统一格式的错误响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数验证异常（@RequestBody @Valid）
     * 
     * @param ex 方法参数验证异常
     * @return CommonResponse 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("请求参数验证失败: {}", ex.getMessage());
        
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseBuilder.fail("参数验证失败: " + errorMessage);
    }

    /**
     * 处理表单绑定验证异常
     * 
     * @param ex 绑定异常
     * @return CommonResponse 错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleBindException(BindException ex) {
        log.warn("表单参数验证失败: {}", ex.getMessage());
        
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseBuilder.fail("参数验证失败: " + errorMessage);
    }

    /**
     * 处理约束验证异常（@PathVariable @RequestParam @Valid）
     * 
     * @param ex 约束验证异常
     * @return CommonResponse 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("约束验证失败: {}", ex.getMessage());
        
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseBuilder.fail("参数验证失败: " + errorMessage);
    }

    /**
     * 处理非法参数异常
     * 
     * @param ex 非法参数异常
     * @return CommonResponse 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("参数异常: {}", ex.getMessage());
        return ResponseBuilder.fail("请求参数无效: " + ex.getMessage());
    }

    /**
     * 处理运行时异常
     * 
     * @param ex 运行时异常
     * @return CommonResponse 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handleRuntimeException(RuntimeException ex) {
        log.error("运行时异常: {}", ex.getMessage(), ex);
        return ResponseBuilder.fail("服务异常，请稍后重试");
    }

    /**
     * 处理通用异常
     * 
     * @param ex 异常
     * @return CommonResponse 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handleGenericException(Exception ex) {
        log.error("未知异常: {}", ex.getMessage(), ex);
        return ResponseBuilder.fail("服务异常，请稍后重试");
    }
}