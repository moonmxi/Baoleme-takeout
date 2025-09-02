/**
 * 核心转发控制器
 * 处理所有HTTP请求的转发，支持GET、POST、PUT、DELETE、PATCH等方法
 * 保持请求参数、headers和body内容不变，准确转发到对应的微服务端点
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward.controller;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.forward.service.ForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 转发控制器类
 * 接收所有HTTP请求并转发到对应的微服务端点
 * 支持所有HTTP方法，保持请求完整性
 */
@Slf4j
@RestController
public class ForwardController {

    /**
     * 转发服务
     */
    @Autowired
    private ForwardService forwardService;

    /**
     * 处理GET请求转发
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return ResponseEntity 转发结果
     * @throws IOException IO异常
     */
    @GetMapping("/**")
    public ResponseEntity<String> handleGetRequest(HttpServletRequest request, 
                                                  HttpServletResponse response) throws IOException {
        log.debug("接收到GET请求: {}", request.getRequestURI());
        return forwardService.forwardRequest(request, response);
    }

    /**
     * 处理POST请求转发
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return ResponseEntity 转发结果
     * @throws IOException IO异常
     */
    @PostMapping("/**")
    public ResponseEntity<String> handlePostRequest(HttpServletRequest request, 
                                                   HttpServletResponse response) throws IOException {
        log.debug("接收到POST请求: {}", request.getRequestURI());
        return forwardService.forwardRequest(request, response);
    }

    /**
     * 处理PUT请求转发
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return ResponseEntity 转发结果
     * @throws IOException IO异常
     */
    @PutMapping("/**")
    public ResponseEntity<String> handlePutRequest(HttpServletRequest request, 
                                                  HttpServletResponse response) throws IOException {
        log.debug("接收到PUT请求: {}", request.getRequestURI());
        return forwardService.forwardRequest(request, response);
    }

    /**
     * 处理DELETE请求转发
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return ResponseEntity 转发结果
     * @throws IOException IO异常
     */
    @DeleteMapping("/**")
    public ResponseEntity<String> handleDeleteRequest(HttpServletRequest request, 
                                                     HttpServletResponse response) throws IOException {
        log.debug("接收到DELETE请求: {}", request.getRequestURI());
        return forwardService.forwardRequest(request, response);
    }

    /**
     * 处理PATCH请求转发
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return ResponseEntity 转发结果
     * @throws IOException IO异常
     */
    @PatchMapping("/**")
    public ResponseEntity<String> handlePatchRequest(HttpServletRequest request, 
                                                    HttpServletResponse response) throws IOException {
        log.debug("接收到PATCH请求: {}", request.getRequestURI());
        return forwardService.forwardRequest(request, response);
    }

    /**
     * 处理OPTIONS请求转发
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return ResponseEntity 转发结果
     * @throws IOException IO异常
     */
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<String> handleOptionsRequest(HttpServletRequest request, 
                                                      HttpServletResponse response) throws IOException {
        log.debug("接收到OPTIONS请求: {}", request.getRequestURI());
        return forwardService.forwardRequest(request, response);
    }

    /**
     * 处理HEAD请求转发
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return ResponseEntity 转发结果
     * @throws IOException IO异常
     */
    @RequestMapping(value = "/**", method = RequestMethod.HEAD)
    public ResponseEntity<String> handleHeadRequest(HttpServletRequest request, 
                                                   HttpServletResponse response) throws IOException {
        log.debug("接收到HEAD请求: {}", request.getRequestURI());
        return forwardService.forwardRequest(request, response);
    }
}