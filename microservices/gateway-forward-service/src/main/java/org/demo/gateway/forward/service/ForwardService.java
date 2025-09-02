/**
 * 转发服务类
 * 实现HTTP请求的转发逻辑，包括路由匹配、请求构建、响应处理等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.forward.service;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.forward.config.RouteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 转发服务实现类
 * 负责处理HTTP请求的转发逻辑
 */
@Slf4j
@Service
public class ForwardService {

    /**
     * REST模板，用于发送HTTP请求
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 路由配置
     */
    @Autowired
    private RouteConfiguration routeConfiguration;

    /**
     * 需要过滤的请求头
     */
    private static final Set<String> FILTERED_HEADERS = Set.of(
            "host", "connection", "content-length", "transfer-encoding",
            "upgrade", "proxy-connection", "proxy-authenticate", "proxy-authorization",
            "te", "trailers"
    );

    /**
     * 转发HTTP请求到目标微服务
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return ResponseEntity 转发结果
     * @throws IOException IO异常
     */
    public ResponseEntity<String> forwardRequest(HttpServletRequest request, 
                                               HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        
        try {
            log.info("开始转发请求: {} {}", method, requestUri);
            
            // 1. 匹配路由规则
            RouteConfiguration.RouteRule matchedRoute = findMatchingRoute(requestUri);
            if (matchedRoute == null) {
                log.warn("未找到匹配的路由规则: {}", requestUri);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"未找到匹配的路由规则\",\"path\":\"" + requestUri + "\"}");
            }
            
            // 2. 获取目标服务配置
            RouteConfiguration.ServiceConfig serviceConfig = routeConfiguration.getServices().get(matchedRoute.getService());
            if (serviceConfig == null) {
                log.error("未找到服务配置: {}", matchedRoute.getService());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\":\"服务配置不存在\",\"service\":\"" + matchedRoute.getService() + "\"}");
            }
            
            // 3. 构建目标URL
            String targetUrl = buildTargetUrl(serviceConfig.getUrl(), requestUri, request.getQueryString(), matchedRoute);
            log.debug("目标URL: {}", targetUrl);
            
            // 4. 构建请求头
            HttpHeaders headers = buildHeaders(request);
            
            // 5. 读取请求体
            String requestBody = readRequestBody(request);
            
            // 6. 创建HTTP实体
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            // 7. 发送请求
            ResponseEntity<String> result = restTemplate.exchange(
                    URI.create(targetUrl),
                    HttpMethod.valueOf(method),
                    entity,
                    String.class
            );
            
            // 8. 复制响应头
            copyResponseHeaders(result, response);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("请求转发完成: {} {} -> {} ({}ms)", method, requestUri, targetUrl, duration);
            
            return result;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("请求转发失败: {} {} ({}ms)", method, requestUri, duration, e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"请求转发失败\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * 查找匹配的路由规则
     * 
     * @param requestUri 请求URI
     * @return RouteRule 匹配的路由规则，未找到返回null
     */
    private RouteConfiguration.RouteRule findMatchingRoute(String requestUri) {
        List<RouteConfiguration.RouteRule> routes = routeConfiguration.getRoutes();
        if (routes == null) {
            return null;
        }
        
        for (RouteConfiguration.RouteRule route : routes) {
            if (matchesPath(requestUri, route.getPath())) {
                log.debug("匹配到路由规则: {} -> {}", route.getPath(), route.getService());
                return route;
            }
        }
        
        return null;
    }

    /**
     * 检查路径是否匹配
     * 
     * @param requestUri 请求URI
     * @param pattern 路径模式
     * @return boolean 是否匹配
     */
    private boolean matchesPath(String requestUri, String pattern) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return requestUri.startsWith(prefix);
        } else if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return requestUri.startsWith(prefix) && 
                   requestUri.substring(prefix.length()).indexOf('/') == -1;
        } else {
            return requestUri.equals(pattern);
        }
    }

    /**
     * 构建目标URL
     * 
     * @param serviceUrl 服务基础URL
     * @param requestUri 请求URI
     * @param queryString 查询字符串
     * @param route 路由规则
     * @return String 目标URL
     */
    private String buildTargetUrl(String serviceUrl, String requestUri, String queryString, RouteConfiguration.RouteRule route) {
        String path = requestUri;
        
        // 如果需要去除前缀
        if (route.isStripPrefix()) {
            String routePath = route.getPath();
            if (routePath.endsWith("/**")) {
                String prefix = routePath.substring(0, routePath.length() - 3);
                if (requestUri.startsWith(prefix)) {
                    path = requestUri.substring(prefix.length());
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                }
            }
        }
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl + path);
        
        if (queryString != null && !queryString.isEmpty()) {
            builder.query(queryString);
        }
        
        return builder.toUriString();
    }

    /**
     * 构建请求头
     * 
     * @param request HTTP请求对象
     * @return HttpHeaders 请求头
     */
    private HttpHeaders buildHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            
            // 过滤不需要转发的请求头
            if (FILTERED_HEADERS.contains(headerName.toLowerCase())) {
                continue;
            }
            
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                headers.add(headerName, headerValues.nextElement());
            }
        }
        
        return headers;
    }

    /**
     * 读取请求体
     * 
     * @param request HTTP请求对象
     * @return String 请求体内容
     * @throws IOException IO异常
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        if ("GET".equalsIgnoreCase(request.getMethod()) || 
            "DELETE".equalsIgnoreCase(request.getMethod()) ||
            "HEAD".equalsIgnoreCase(request.getMethod()) ||
            "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        
        try {
            byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
            return new String(bodyBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("读取请求体失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 复制响应头
     * 
     * @param result 转发结果
     * @param response HTTP响应对象
     */
    private void copyResponseHeaders(ResponseEntity<String> result, HttpServletResponse response) {
        HttpHeaders responseHeaders = result.getHeaders();
        
        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            String headerName = entry.getKey();
            
            // 过滤不需要复制的响应头
            if (FILTERED_HEADERS.contains(headerName.toLowerCase())) {
                continue;
            }
            
            for (String headerValue : entry.getValue()) {
                response.addHeader(headerName, headerValue);
            }
        }
    }
}