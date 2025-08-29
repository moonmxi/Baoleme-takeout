/**
 * 数据库操作控制器
 * 提供统一的HTTP接口，支持跨数据库的CRUD操作
 * 
 * 主要功能：
 * 1. 统一数据库操作API - 提供标准化的REST接口
 * 2. 自动数据源路由 - 根据表名自动选择正确的数据库
 * 3. 参数验证 - 严格的输入参数验证
 * 4. 异常处理 - 统一的错误处理和响应格式
 * 5. 操作日志 - 详细的操作日志记录
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.service.DatabaseOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作控制器
 * 为其他微服务提供统一的数据库操作接口
 */
@Slf4j
@RestController
@RequestMapping("/api/database")
@Validated
public class DatabaseController {

    @Autowired
    private DatabaseOperationService databaseService;

    /**
     * 根据ID查询单条记录
     * 
     * @param tableName 表名
     * @param id 主键ID
     * @param request HTTP请求对象（用于获取用户信息）
     * @return 查询结果
     */
    @GetMapping("/{tableName}/{id}")
    public ResponseEntity<ApiResponse> selectById(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @PathVariable @NotNull(message = "ID不能为空") @Min(value = 1, message = "ID必须大于0") Long id,
            HttpServletRequest request) {
        
        try {
            // 获取当前用户信息
            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            String username = (String) request.getAttribute("username");
            
            log.info("用户 {} (角色: {}) 查询单条记录: 表={}, ID={}", username, role, tableName, id);
            
            Map<String, Object> result = databaseService.selectById(tableName, id);
            
            if (result == null) {
                return ResponseEntity.ok(ApiResponse.success("记录不存在", null));
            }
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
            
        } catch (Exception e) {
            log.error("查询单条记录失败: 表={}, ID={}", tableName, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据条件查询记录列表
     * 
     * @param tableName 表名
     * @param conditionRequest 查询条件
     * @param request HTTP请求对象（用于获取用户信息）
     * @return 查询结果列表
     */
    @PostMapping("/{tableName}/select")
    public ResponseEntity<ApiResponse> selectByConditions(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestBody(required = false) ConditionRequest conditionRequest,
            HttpServletRequest request) {
        
        try {
            // 获取当前用户信息
            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            String username = (String) request.getAttribute("username");
            
            log.info("用户 {} (角色: {}) 条件查询记录: 表={}, 条件={}", 
                    username, role, tableName, conditionRequest != null ? conditionRequest.getCondition() : null);
            
            Map<String, Object> conditions = conditionRequest != null ? conditionRequest.getCondition() : new HashMap<>();
            List<Map<String, Object>> results = databaseService.selectByConditions(tableName, conditions);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("records", results);
            responseData.put("total", results.size());
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", responseData));
            
        } catch (Exception e) {
            log.error("条件查询记录失败: 表={}", tableName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 分页查询记录列表
     * 
     * @param tableName 表名
     * @param page 页码
     * @param pageSize 每页大小
     * @param conditionRequest 查询条件
     * @param request HTTP请求对象（用于获取用户信息）
     * @return 分页查询结果
     */
    @PostMapping("/{tableName}/page")
    public ResponseEntity<ApiResponse> selectByPage(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小必须大于0") int pageSize,
            @RequestBody(required = false) ConditionRequest conditionRequest,
            HttpServletRequest request) {
        
        try {
            // 获取当前用户信息
            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            String username = (String) request.getAttribute("username");
            
            log.info("用户 {} (角色: {}) 分页查询记录: 表={}, 页码={}, 页大小={}, 条件={}", 
                    username, role, tableName, page, pageSize, conditionRequest != null ? conditionRequest.getCondition() : null);
            
            Map<String, Object> conditions = conditionRequest != null ? conditionRequest.getCondition() : new HashMap<>();
            
            List<Map<String, Object>> records = databaseService.selectByPage(tableName, conditions, page, pageSize);
            long total = databaseService.countByConditions(tableName, conditions);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("records", records);
            responseData.put("total", total);
            responseData.put("page", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalPages", (total + pageSize - 1) / pageSize);
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", responseData));
            
        } catch (Exception e) {
            log.error("分页查询记录失败: 表={}, 页码={}, 页大小={}", tableName, page, pageSize, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 插入单条记录
     * 
     * @param tableName 表名
     * @param data 插入数据
     * @return 插入结果
     */
    @PostMapping("/{tableName}")
    public ResponseEntity<ApiResponse> insert(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestBody @Valid Map<String, Object> data) {
        
        try {
            log.info("插入记录: 表={}, 数据={}", tableName, data);
            
            int result = databaseService.insert(tableName, data);
            
            if (result > 0) {
                return ResponseEntity.ok(ApiResponse.success("插入成功", Map.of("affectedRows", result)));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("插入失败"));
            }
            
        } catch (Exception e) {
            log.error("插入记录失败: 表={}", tableName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("插入失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID更新记录
     * 
     * @param tableName 表名
     * @param id 主键ID
     * @param data 更新数据
     * @return 更新结果
     */
    @PutMapping("/{tableName}/{id}")
    public ResponseEntity<ApiResponse> updateById(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @PathVariable @NotNull(message = "ID不能为空") @Min(value = 1, message = "ID必须大于0") Long id,
            @RequestBody @Valid Map<String, Object> data) {
        
        try {
            log.info("根据ID更新记录: 表={}, ID={}, 数据={}", tableName, id, data);
            
            int result = databaseService.updateById(tableName, id, data);
            
            if (result > 0) {
                return ResponseEntity.ok(ApiResponse.success("更新成功", Map.of("affectedRows", result)));
            } else {
                return ResponseEntity.ok(ApiResponse.success("没有记录被更新", Map.of("affectedRows", 0)));
            }
            
        } catch (Exception e) {
            log.error("根据ID更新记录失败: 表={}, ID={}", tableName, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID删除记录
     * 
     * @param tableName 表名
     * @param id 主键ID
     * @return 删除结果
     */
    @DeleteMapping("/{tableName}/{id}")
    public ResponseEntity<ApiResponse> deleteById(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @PathVariable @NotNull(message = "ID不能为空") @Min(value = 1, message = "ID必须大于0") Long id) {
        
        try {
            log.info("根据ID删除记录: 表={}, ID={}", tableName, id);
            
            int result = databaseService.deleteById(tableName, id);
            
            if (result > 0) {
                return ResponseEntity.ok(ApiResponse.success("删除成功", Map.of("affectedRows", result)));
            } else {
                return ResponseEntity.ok(ApiResponse.success("没有记录被删除", Map.of("affectedRows", 0)));
            }
            
        } catch (Exception e) {
            log.error("根据ID删除记录失败: 表={}, ID={}", tableName, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }

    /**
     * 获取数据源健康状态
     * 
     * @return 健康状态信息
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> getDataSourceHealth() {
        try {
            log.info("获取数据源健康状态");
            
            Map<String, Object> health = databaseService.getDataSourceHealth();
            
            return ResponseEntity.ok(ApiResponse.success("健康检查完成", health));
            
        } catch (Exception e) {
            log.error("获取数据源健康状态失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("健康检查失败: " + e.getMessage()));
        }
    }

    /**
     * 统一响应格式
     */
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private String timestamp;

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        public static ApiResponse success(String message, Object data) {
            return new ApiResponse(true, message, data);
        }

        public static ApiResponse error(String message) {
            return new ApiResponse(false, message, null);
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 查询条件请求
     */
    public static class ConditionRequest {
        private Map<String, Object> condition;

        public ConditionRequest() {}

        public ConditionRequest(Map<String, Object> condition) {
            this.condition = condition;
        }

        public Map<String, Object> getCondition() { return condition; }
        public void setCondition(Map<String, Object> condition) { this.condition = condition; }
    }
}