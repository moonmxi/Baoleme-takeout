# 网关转发服务 (Gateway Forward Service)

## 项目简介

网关转发服务是一个高性能的HTTP请求转发服务，负责将客户端请求转发到对应的微服务端点。该服务支持所有HTTP方法，保持请求参数、headers和body内容不变，确保准确转发到目标微服务。

## 功能特性

### 🚀 核心功能
- **全HTTP方法支持**: 支持GET、POST、PUT、DELETE、PATCH、OPTIONS、HEAD等所有HTTP方法
- **智能路由匹配**: 基于路径模式的智能路由匹配机制
- **请求完整性保持**: 完整保持请求参数、headers和body内容
- **高性能转发**: 基于连接池的高性能HTTP客户端

### 📊 监控与日志
- **详细请求日志**: 记录每个请求的完整信息和性能指标
- **健康状态检查**: 提供服务自身和下游服务的健康状态监控
- **性能监控**: 集成Actuator监控端点
- **错误处理**: 完善的异常处理和错误响应机制

### 🔧 高级特性
- **连接池管理**: 可配置的HTTP连接池，支持连接复用
- **超时控制**: 灵活的超时配置，防止请求阻塞
- **CORS支持**: 完整的跨域资源共享支持
- **请求拦截**: 可扩展的请求拦截器机制

## 技术架构

### 技术栈
- **Spring Boot 3.2.0**: 应用框架
- **Spring WebMVC**: Web层框架
- **Apache HttpClient 5**: HTTP客户端
- **Micrometer**: 监控指标
- **Lombok**: 代码简化
- **JUnit 5**: 单元测试

### 架构设计
```
客户端请求 → ForwardController → ForwardService → 目标微服务
     ↓              ↓              ↓
  日志拦截器    路由匹配器    HTTP客户端池
     ↓              ↓              ↓
  异常处理器    配置管理器    健康检查器
```

## 微服务路由映射

### 当前支持的微服务

| 路径模式 | 目标服务 | 服务端口 | 说明 |
|---------|---------|---------|------|
| `/admin/**` | admin-service | 8084 | 管理员服务 |
| `/user/**` | user-service | 8081 | 用户服务 |
| `/merchant/**` | merchant-service | 8082 | 商家服务 |
| `/store/**` | merchant-service | 8082 | 店铺服务 |
| `/product/**` | merchant-service | 8082 | 商品服务 |
| `/rider/**` | rider-service | 8083 | 骑手服务 |
| `/image/**` | rider-service | 8083 | 图片上传服务 |
| `/orders/**` | common-service | 8085 | 订单服务 |
| `/payment/**` | common-service | 8085 | 支付服务 |
| `/notification/**` | common-service | 8085 | 通知服务 |
| `/api/database/**` | gateway-service | 8080 | 数据库API服务 |

### 接口示例

#### 用户相关接口
```bash
# 用户注册
POST http://localhost:8090/user/register

# 用户登录
POST http://localhost:8090/user/login

# 获取用户信息
GET http://localhost:8090/user/info
```

#### 管理员相关接口
```bash
# 管理员登录
POST http://localhost:8090/admin/login

# 查看用户列表
POST http://localhost:8090/admin/userlist
```

#### 商家相关接口
```bash
# 商家注册
POST http://localhost:8090/merchant/register

# 创建店铺
POST http://localhost:8090/store/create

# 创建商品
POST http://localhost:8090/product/create
```

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- 各微服务已启动并运行在对应端口

### 构建和运行

1. **克隆项目**
```bash
cd /Users/mawenban/Documents/Yuan/SE-Project/microservices/gateway-forward-service
```

2. **编译项目**
```bash
mvn clean compile
```

3. **运行测试**
```bash
mvn test
```

4. **启动服务**
```bash
mvn spring-boot:run
```

5. **验证服务**
```bash
# 检查服务健康状态
curl http://localhost:8090/health

# 检查详细健康状态
curl http://localhost:8090/health/detailed
```

### Docker部署

1. **构建Docker镜像**
```bash
mvn clean package
docker build -t gateway-forward-service:1.0.0 .
```

2. **运行容器**
```bash
docker run -d \
  --name gateway-forward-service \
  -p 8090:8090 \
  gateway-forward-service:1.0.0
```

## 配置说明

### 核心配置 (application.yml)

```yaml
# 服务端口配置
server:
  port: 8090

# 微服务路由配置
gateway:
  forward:
    services:
      user-service:
        url: http://localhost:8082
        timeout: 30000
    routes:
      - path: "/user/**"
        service: user-service
        strip-prefix: false
```

### 连接池配置

```yaml
gateway:
  forward:
    connection-pool:
      max-total: 200          # 最大连接数
      max-per-route: 50       # 每路由最大连接数
      connection-timeout: 5000 # 连接超时(ms)
      socket-timeout: 30000   # Socket超时(ms)
```

### 日志配置

```yaml
logging:
  level:
    org.demo.gateway.forward: INFO
  file:
    name: logs/gateway-forward-service.log
```

## 监控和运维

### 健康检查端点

- **基础健康检查**: `GET /health`
- **详细健康检查**: `GET /health/detailed`
- **Actuator端点**: `GET /actuator/health`

### 监控指标

- **请求计数**: 按路径和方法统计请求数量
- **响应时间**: 请求处理时间分布
- **错误率**: 4xx和5xx错误统计
- **连接池状态**: 连接池使用情况

### 日志格式

```
2025-01-25 10:30:15.123 [http-nio-8090-exec-1] INFO  o.d.g.f.i.RequestLoggingInterceptor - 
=== 请求开始 [ID: abc12345] ===
时间: 2025-01-25T10:30:15.123
方法: POST
路径: /user/login
客户端IP: 192.168.1.100
```

## 性能优化

### 连接池优化
- 根据并发量调整最大连接数
- 设置合适的连接超时时间
- 启用连接复用和Keep-Alive

### JVM优化
```bash
-Xms512m -Xmx1024m
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
```

### 系统优化
- 调整系统文件描述符限制
- 优化网络参数
- 使用SSD存储日志文件

## 故障排查

### 常见问题

1. **连接超时**
   - 检查目标服务是否正常运行
   - 调整连接超时配置
   - 检查网络连通性

2. **路由不匹配**
   - 检查路由配置是否正确
   - 验证路径模式匹配规则
   - 查看详细日志

3. **内存溢出**
   - 调整JVM堆内存大小
   - 检查连接池配置
   - 分析内存使用情况

### 调试模式

```yaml
logging:
  level:
    org.demo.gateway.forward: DEBUG
    org.springframework.web: DEBUG
    org.apache.http: DEBUG
```

## 扩展开发

### 添加新的路由规则

1. 在`application.yml`中添加服务配置
2. 添加对应的路由规则
3. 重启服务使配置生效

### 自定义拦截器

```java
@Component
public class CustomInterceptor implements HandlerInterceptor {
    // 实现自定义逻辑
}
```

### 集成监控系统

```java
@Component
public class MetricsCollector {
    // 集成Prometheus、Grafana等监控系统
}
```

## 版本历史

- **v1.0.0** (2025-01-25)
  - 初始版本发布
  - 支持基础HTTP请求转发
  - 实现路由匹配和健康检查
  - 添加完整的日志和监控功能

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码变更
4. 创建Pull Request

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

- 项目团队: Baoleme Team
- 技术支持: 请提交Issue或Pull Request
- 文档更新: 2025-01-25