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
import org.demo.gateway.config.SecurityConfig;
import org.demo.gateway.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作控制器
 * 提供跨数据库操作的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/database")
@Validated
public class DatabaseController {

    /**
     * 数据库操作服务
     */
    @Autowired
    private DatabaseService databaseService;

    /**
     * 安全配置
     */
    @Autowired
    private SecurityConfig securityConfig;

    /**
     * 根据ID查询单条记录
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @return ResponseEntity<ApiResponse> 查询结果
     */
    @GetMapping("/{tableName}/{id}")
    public ResponseEntity<ApiResponse> selectById(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @PathVariable @NotNull(message = "ID不能为空") @Min(value = 1, message = "ID必须大于0") Long id) {
        
        try {
            log.info("查询单条记录 - 表: {}, ID: {}", tableName, id);
            
            Map<String, Object> result = databaseService.selectById(tableName, id);
            
            if (result == null || result.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("记录不存在", null));
            }
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
            
        } catch (IllegalArgumentException e) {
            log.warn("查询参数错误 - 表: {}, ID: {}, 错误: {}", tableName, id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("查询记录失败 - 表: {}, ID: {}, 错误: {}", tableName, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据条件查询多条记录
     * 
     * @param tableName 表名
     * @param request 查询请求，包含condition参数
     * @return ResponseEntity<ApiResponse> 查询结果
     */
    @PostMapping("/{tableName}/select")
    public ResponseEntity<ApiResponse> selectByConditions(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestBody(required = false) ConditionRequest request) {
        
        try {
            Map<String, Object> condition = (request != null && request.getCondition() != null) 
                    ? request.getCondition() : new HashMap<>();
            
            log.info("条件查询记录 - 表: {}, 条件: {}", tableName, condition);
            
            List<Map<String, Object>> result = databaseService.selectByConditions(tableName, condition);
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", Map.of(
                    "data", result,
                    "count", result.size(),
                    "condition", condition
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("查询参数错误 - 表: {}, 错误: {}", tableName, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("条件查询失败 - 表: {}, 错误: {}", tableName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 分页查询所有记录
     * 
     * @param tableName 表名
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return ResponseEntity<ApiResponse> 查询结果
     */
    @GetMapping("/{tableName}")
    public ResponseEntity<ApiResponse> selectAll(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小必须大于0") int pageSize) {
        
        try {
            log.info("分页查询记录 - 表: {}, 页码: {}, 每页大小: {}", tableName, page, pageSize);
            
            List<Map<String, Object>> result = databaseService.selectAll(tableName, page, pageSize);
            long totalCount = databaseService.count(tableName);
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", Map.of(
                    "data", result,
                    "page", page,
                    "pageSize", pageSize,
                    "totalCount", totalCount,
                    "totalPages", (totalCount + pageSize - 1) / pageSize
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("分页查询参数错误 - 表: {}, 页码: {}, 每页大小: {}, 错误: {}", tableName, page, pageSize, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("分页查询失败 - 表: {}, 页码: {}, 每页大小: {}, 错误: {}", tableName, page, pageSize, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 插入单条记录
     * 
     * @param tableName 表名
     * @param data 插入数据
     * @return ResponseEntity<ApiResponse> 插入结果
     */
    @PostMapping("/{tableName}")
    public ResponseEntity<ApiResponse> insert(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestBody @Valid Map<String, Object> data) {
        
        try {
            log.info("插入记录 - 表: {}, 数据: {}", tableName, data);
            
            Long id = databaseService.insert(tableName, data);
            
            return ResponseEntity.ok(ApiResponse.success("插入成功", Map.of(
                    "id", id,
                    "affectedRows", 1
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("插入参数错误 - 表: {}, 数据: {}, 错误: {}", tableName, data, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("插入记录失败 - 表: {}, 数据: {}, 错误: {}", tableName, data, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("插入失败: " + e.getMessage()));
        }
    }

    /**
     * 批量插入记录
     * 
     * @param tableName 表名
     * @param dataList 插入数据列表
     * @return ResponseEntity<ApiResponse> 插入结果
     */
    @PostMapping("/{tableName}/batch")
    public ResponseEntity<ApiResponse> batchInsert(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestBody @Valid List<Map<String, Object>> dataList) {
        
        try {
            log.info("批量插入记录 - 表: {}, 数据量: {}", tableName, dataList.size());
            
            int affectedRows = databaseService.batchInsert(tableName, dataList);
            
            return ResponseEntity.ok(ApiResponse.success("批量插入成功", Map.of(
                    "affectedRows", affectedRows,
                    "totalRecords", dataList.size()
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("批量插入参数错误 - 表: {}, 数据量: {}, 错误: {}", tableName, dataList.size(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("批量插入失败 - 表: {}, 数据量: {}, 错误: {}", tableName, dataList.size(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("批量插入失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID更新记录
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @param data 更新数据
     * @return ResponseEntity<ApiResponse> 更新结果
     */
    @PutMapping("/{tableName}/{id}")
    public ResponseEntity<ApiResponse> updateById(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @PathVariable @NotNull(message = "ID不能为空") @Min(value = 1, message = "ID必须大于0") Long id,
            @RequestBody @Valid Map<String, Object> data) {
        
        try {
            log.info("更新记录 - 表: {}, ID: {}, 数据: {}", tableName, id, data);
            
            int affectedRows = databaseService.updateById(tableName, id, data);
            
            if (affectedRows == 0) {
                return ResponseEntity.ok(ApiResponse.success("记录不存在或无需更新", Map.of(
                        "affectedRows", 0
                )));
            }
            
            return ResponseEntity.ok(ApiResponse.success("更新成功", Map.of(
                    "affectedRows", affectedRows
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("更新参数错误 - 表: {}, ID: {}, 数据: {}, 错误: {}", tableName, id, data, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("更新记录失败 - 表: {}, ID: {}, 数据: {}, 错误: {}", tableName, id, data, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 根据条件更新记录
     * 
     * @param tableName 表名
     * @param request 更新请求
     * @return ResponseEntity<ApiResponse> 更新结果
     */
    @PutMapping("/{tableName}/update")
    public ResponseEntity<ApiResponse> updateByConditions(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestBody @Valid UpdateRequest request) {
        
        try {
            log.info("条件更新记录 - 表: {}, 条件: {}, 数据: {}", tableName, request.getCondition(), request.getData());
            
            int affectedRows = databaseService.updateByConditions(tableName, request.getCondition(), request.getData());
            
            return ResponseEntity.ok(ApiResponse.success("更新成功", Map.of(
                    "affectedRows", affectedRows,
                    "condition", request.getCondition()
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("条件更新参数错误 - 表: {}, 错误: {}", tableName, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("条件更新失败 - 表: {}, 错误: {}", tableName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID删除记录
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @return ResponseEntity<ApiResponse> 删除结果
     */
    @DeleteMapping("/{tableName}/{id}")
    public ResponseEntity<ApiResponse> deleteById(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @PathVariable @NotNull(message = "ID不能为空") @Min(value = 1, message = "ID必须大于0") Long id) {
        
        try {
            log.info("删除记录 - 表: {}, ID: {}", tableName, id);
            
            int affectedRows = databaseService.deleteById(tableName, id);
            
            if (affectedRows == 0) {
                return ResponseEntity.ok(ApiResponse.success("记录不存在", Map.of(
                        "affectedRows", 0
                )));
            }
            
            return ResponseEntity.ok(ApiResponse.success("删除成功", Map.of(
                    "affectedRows", affectedRows
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("删除参数错误 - 表: {}, ID: {}, 错误: {}", tableName, id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("删除记录失败 - 表: {}, ID: {}, 错误: {}", tableName, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }

    /**
     * 批量删除记录
     * 
     * @param tableName 表名
     * @param ids ID列表
     * @return ResponseEntity<ApiResponse> 删除结果
     */
    @DeleteMapping("/{tableName}/batch")
    public ResponseEntity<ApiResponse> batchDelete(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName,
            @RequestBody @Valid List<Long> ids) {
        
        try {
            log.info("批量删除记录 - 表: {}, ID数量: {}", tableName, ids.size());
            
            int affectedRows = databaseService.batchDelete(tableName, ids);
            
            return ResponseEntity.ok(ApiResponse.success("批量删除成功", Map.of(
                    "affectedRows", affectedRows,
                    "totalIds", ids.size()
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("批量删除参数错误 - 表: {}, ID数量: {}, 错误: {}", tableName, ids.size(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("批量删除失败 - 表: {}, ID数量: {}, 错误: {}", tableName, ids.size(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("批量删除失败: " + e.getMessage()));
        }
    }

    /**
     * 统计记录总数
     * 
     * @param tableName 表名
     * @return ResponseEntity<ApiResponse> 统计结果
     */
    @GetMapping("/{tableName}/count")
    public ResponseEntity<ApiResponse> count(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName) {
        
        try {
            log.info("统计记录总数 - 表: {}", tableName);
            
            long count = databaseService.count(tableName);
            
            return ResponseEntity.ok(ApiResponse.success("统计成功", Map.of(
                    "count", count
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("统计参数错误 - 表: {}, 错误: {}", tableName, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("统计记录失败 - 表: {}, 错误: {}", tableName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("统计失败: " + e.getMessage()));
        }
    }

    /**
     * 检查表是否存在
     * 
     * @param tableName 表名
     * @return ResponseEntity<ApiResponse> 检查结果
     */
    @GetMapping("/{tableName}/exists")
    public ResponseEntity<ApiResponse> tableExists(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName) {
        
        try {
            log.info("检查表是否存在 - 表: {}", tableName);
            
            boolean exists = databaseService.tableExists(tableName);
            
            return ResponseEntity.ok(ApiResponse.success("检查完成", Map.of(
                    "exists", exists
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("检查表参数错误 - 表: {}, 错误: {}", tableName, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("检查表失败 - 表: {}, 错误: {}", tableName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("检查失败: " + e.getMessage()));
        }
    }

    /**
     * 获取表结构信息
     * 
     * @param tableName 表名
     * @return ResponseEntity<ApiResponse> 表结构信息
     */
    @GetMapping("/{tableName}/structure")
    public ResponseEntity<ApiResponse> getTableStructure(
            @PathVariable @NotBlank(message = "表名不能为空") String tableName) {
        
        try {
            log.info("获取表结构信息 - 表: {}", tableName);
            
            List<Map<String, Object>> structure = databaseService.getTableStructure(tableName);
            
            return ResponseEntity.ok(ApiResponse.success("获取成功", Map.of(
                    "structure", structure,
                    "columnCount", structure.size()
            )));
            
        } catch (IllegalArgumentException e) {
            log.warn("获取表结构参数错误 - 表: {}, 错误: {}", tableName, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("获取表结构失败 - 表: {}, 错误: {}", tableName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有数据源健康状态
     * 
     * @return ResponseEntity<ApiResponse> 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> getDataSourceHealth() {
        try {
            log.info("获取数据源健康状态");
            
            Map<String, Boolean> healthStatus = databaseService.getAllDataSourceHealth();
            
            return ResponseEntity.ok(ApiResponse.success("获取成功", Map.of(
                    "healthStatus", healthStatus,
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )));
            
        } catch (Exception e) {
            log.error("获取数据源健康状态失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取失败: " + e.getMessage()));
        }
    }

    /**
     * 生成认证令牌（仅用于测试和开发）
     * 
     * @param request 令牌生成请求
     * @return ResponseEntity<ApiResponse> 生成的令牌
     */
    @PostMapping("/auth/token")
    public ResponseEntity<ApiResponse> generateAuthToken(@RequestBody(required = false) TokenRequest request) {
        try {
            String serviceName = (request != null && request.getServiceName() != null) 
                    ? request.getServiceName() : "unknown-service";
            String operation = (request != null && request.getOperation() != null) 
                    ? request.getOperation() : "database-operation";
            
            String token = securityConfig.generateToken(serviceName, operation);
            
            log.info("生成认证令牌 - 服务: {}, 操作: {}", serviceName, operation);
            
            return ResponseEntity.ok(ApiResponse.success("令牌生成成功", Map.of(
                    "token", token,
                    "serviceName", serviceName,
                    "operation", operation,
                    "expirySeconds", 300,
                    "usage", Map.of(
                            "header", "X-Gateway-Auth",
                            "serviceHeader", "X-Service-Name",
                            "operationHeader", "X-Operation-Type"
                    )
            )));
            
        } catch (Exception e) {
            log.error("生成认证令牌失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("令牌生成失败: " + e.getMessage()));
        }
    }

    /**
     * 验证认证令牌
     * 
     * @param request HTTP请求
     * @return ResponseEntity<ApiResponse> 验证结果
     */
    @PostMapping("/auth/validate")
    public ResponseEntity<ApiResponse> validateAuthToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("X-Gateway-Auth");
            String serviceName = request.getHeader("X-Service-Name");
            String operation = request.getHeader("X-Operation-Type");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("缺少认证令牌"));
            }
            
            SecurityConfig.AuthResult result = securityConfig.validateTokenDetailed(token, serviceName, operation);
            
            if (result.isSuccess()) {
                log.info("令牌验证成功 - 服务: {}, 操作: {}", result.getServiceName(), result.getOperation());
                return ResponseEntity.ok(ApiResponse.success("令牌验证成功", Map.of(
                        "valid", true,
                        "serviceName", result.getServiceName(),
                        "operation", result.getOperation(),
                        "timestamp", result.getTimestamp()
                )));
            } else {
                log.warn("令牌验证失败: {}", result.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("令牌验证失败: " + result.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("验证认证令牌失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("验证失败: " + e.getMessage()));
        }
    }

    /**
     * 获取安全配置信息
     * 
     * @return ResponseEntity<ApiResponse> 安全配置信息
     */
    @GetMapping("/auth/info")
    public ResponseEntity<ApiResponse> getSecurityInfo() {
        try {
            Map<String, Object> securityInfo = securityConfig.getSecurityInfo();
            
            return ResponseEntity.ok(ApiResponse.success("获取成功", Map.of(
                    "security", securityInfo,
                    "headers", Map.of(
                            "auth", "X-Gateway-Auth",
                            "service", "X-Service-Name",
                            "operation", "X-Operation-Type"
                    ),
                    "endpoints", Map.of(
                            "generateToken", "/api/database/auth/token",
                            "validateToken", "/api/database/auth/validate",
                            "securityInfo", "/api/database/auth/info"
                    )
            )));
            
        } catch (Exception e) {
            log.error("获取安全配置信息失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取失败: " + e.getMessage()));
        }
    }

    /**
     * API响应类
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
     * 条件查询请求类
     */
    public static class ConditionRequest {
        private Map<String, Object> condition;
        
        public ConditionRequest() {}
        
        public ConditionRequest(Map<String, Object> condition) {
            this.condition = condition;
        }

        // Getters and Setters
        public Map<String, Object> getCondition() { return condition; }
        public void setCondition(Map<String, Object> condition) { this.condition = condition; }
    }

    /**
     * 令牌生成请求类
     */
    public static class TokenRequest {
        private String serviceName;
        private String operation;
        
        public TokenRequest() {}
        
        public TokenRequest(String serviceName, String operation) {
            this.serviceName = serviceName;
            this.operation = operation;
        }

        // Getters and Setters
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public String getOperation() { return operation; }
        public void setOperation(String operation) { this.operation = operation; }
    }

    /**
     * 更新请求类
     */
    public static class UpdateRequest {
        @NotNull(message = "更新条件不能为空")
        private Map<String, Object> condition;
        
        @NotNull(message = "更新数据不能为空")
        private Map<String, Object> data;

        // Getters and Setters
        public Map<String, Object> getCondition() { return condition; }
        public void setCondition(Map<String, Object> condition) { this.condition = condition; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }
}