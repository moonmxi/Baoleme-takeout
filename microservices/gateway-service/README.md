# 饱了么微服务网关服务 (Gateway Service)

## 概述

网关服务是饱了么微服务架构的核心组件，专注于提供**跨数据库操作**功能。该服务采用简化设计，通过统一的HTTP接口支持对不同微服务数据库的直接操作，无需复杂的消息传递机制。

## 核心特性

### 🎯 简化架构设计
- **专注核心功能**: 移除复杂的消息传递机制，专注于跨数据库操作
- **直接数据库访问**: 通过HTTP请求直接操作不同数据库的表
- **自动路由**: 根据表名自动选择正确的数据库连接
- **统一接口**: 提供标准化的REST API进行CRUD操作

### 🗄️ 多数据源支持
支持连接5个独立的微服务数据库：
- **用户数据库** (`baoleme_user`) - 用户相关表
- **商家数据库** (`baoleme_merchant`) - 商家、店铺、商品相关表
- **骑手数据库** (`baoleme_rider`) - 骑手相关表
- **管理员数据库** (`baoleme_admin`) - 管理员相关表
- **网关数据库** (`baoleme_gateway`) - 订单、购物车、评价等跨服务表

### 📋 表映射关系

| 表名 | 数据库 | 说明 |
|------|--------|------|
| `user`, `user_favorite`, `user_coupon`, `user_view_history` | user | 用户相关数据 |
| `merchant`, `store`, `product`, `sales` | merchant | 商家相关数据 |
| `rider` | rider | 骑手数据 |
| `admin` | admin | 管理员数据 |
| `order`, `order_item`, `review`, `cart`, `coupon`, `message` | gateway | 跨服务数据 |

## 技术架构

### 技术栈
- **Spring Boot 3.1.5** - 应用框架
- **MyBatis-Plus** - 数据库ORM
- **HikariCP** - 数据库连接池
- **MySQL 8.0+** - 数据存储
- **Maven** - 项目管理

### 架构图
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   管理员服务    │    │   用户服务      │    │   其他微服务    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │ HTTP请求
                    ┌─────────────────┐
                    │   网关服务      │
                    │  (Gateway)      │
                    │  数据库路由器   │
                    └─────────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                       │                       │
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  用户数据库 │    │  商家数据库 │    │  骑手数据库 │    │ 管理员数据库│
│(baoleme_    │    │(baoleme_    │    │(baoleme_    │    │(baoleme_    │
│ user)       │    │ merchant)   │    │ rider)      │    │ admin)      │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                                 │
                    ┌─────────────────┐
                    │  网关数据库     │
                    │(baoleme_gateway)│
                    │  (订单等)       │
                    └─────────────────┘
```

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 配置数据库

1. **创建数据库**
```sql
-- 执行数据库初始化脚本
source src/main/resources/common-service.sql
```

2. **配置连接信息**
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    # 网关数据库（主数据源）
    url: jdbc:mysql://localhost:3306/baoleme_common
    username: root
    password: your_password
    
    # 多数据源配置
    user:
      jdbc-url: jdbc:mysql://localhost:3306/baoleme_user
      username: root
      password: your_password
    
    merchant:
      jdbc-url: jdbc:mysql://localhost:3306/baoleme_merchant
      username: root
      password: your_password
    
    rider:
      jdbc-url: jdbc:mysql://localhost:3306/baoleme_rider
      username: root
      password: your_password
    
    admin:
      jdbc-url: jdbc:mysql://localhost:3306/baoleme_admin
      username: root
      password: your_password
```

### 启动服务

```bash
# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

服务启动后，访问 `http://localhost:8080`

## API接口文档

### 基础CRUD操作

#### 1. 查询单条记录
```http
GET /api/database/{tableName}/{id}
```

**示例：查询用户信息**
```bash
curl -X GET "http://localhost:8080/api/database/user/10000001"
```

**响应：**
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "id": 10000001,
    "username": "testuser",
    "phone": "13800138000",
    "avatar": "avatar.jpg",
    "created_at": "2024-01-25T10:00:00"
  },
  "timestamp": "2025-01-25T15:30:00"
}
```

#### 2. 插入记录
```http
POST /api/database/{tableName}
Content-Type: application/json
```

**示例：创建新用户**
```bash
curl -X POST "http://localhost:8080/api/database/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "encrypted_password",
    "phone": "13900139000",
    "avatar": "default.jpg"
  }'
```

#### 3. 更新记录
```http
PUT /api/database/{tableName}/{id}
Content-Type: application/json
```

**示例：更新用户信息**
```bash
curl -X PUT "http://localhost:8080/api/database/user/10000001" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "avatar": "new_avatar.jpg"
  }'
```

#### 4. 删除记录
```http
DELETE /api/database/{tableName}/{id}
```

**示例：删除用户**
```bash
curl -X DELETE "http://localhost:8080/api/database/user/10000001"
```

### 高级操作

#### 1. 条件查询
```http
POST /api/database/{tableName}/select
Content-Type: application/json
```

**示例：查询特定条件的商品**
```bash
curl -X POST "http://localhost:8080/api/database/product/select" \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 50000001,
    "category": "饮品",
    "minPrice": 10.0,
    "maxPrice": 50.0,
    "inStock": true,
    "limit": 20
  }'
```

#### 2. 分页查询
```http
GET /api/database/{tableName}?page=1&pageSize=10
```

**示例：分页查询订单**
```bash
curl -X GET "http://localhost:8080/api/database/order?page=1&pageSize=20"
```

#### 3. 批量操作
```http
POST /api/database/{tableName}/batch
Content-Type: application/json
```

**示例：批量插入商品**
```bash
curl -X POST "http://localhost:8080/api/database/product/batch" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "storeId": 50000001,
      "name": "珍珠奶茶",
      "price": 15.0,
      "category": "饮品",
      "stock": 100
    },
    {
      "storeId": 50000001,
      "name": "红烧肉",
      "price": 28.0,
      "category": "主食",
      "stock": 50
    }
  ]'
```

#### 4. 统计查询
```http
GET /api/database/{tableName}/count
```

**示例：统计用户总数**
```bash
curl -X GET "http://localhost:8080/api/database/user/count"
```

### 系统管理接口

#### 1. 健康检查
```http
GET /api/database/health
```

**响应：**
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "healthStatus": {
      "user": true,
      "merchant": true,
      "rider": true,
      "admin": true,
      "gateway": true
    },
    "timestamp": "2025-01-25T15:30:00"
  }
}
```

#### 2. 表结构查询
```http
GET /api/database/{tableName}/structure
```

#### 3. 表存在性检查
```http
GET /api/database/{tableName}/exists
```

## 使用场景

### 场景1：管理员操作用户数据

管理员服务需要查询和管理用户信息：

```java
// 管理员服务中的代码示例
@RestController
public class AdminUserController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String GATEWAY_URL = "http://localhost:8080/api/database";
    
    // 查询用户信息
    @GetMapping("/admin/users/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        String url = GATEWAY_URL + "/user/" + userId;
        return restTemplate.getForEntity(url, Map.class);
    }
    
    // 更新用户状态
    @PutMapping("/admin/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, 
                                             @RequestBody Map<String, Object> statusData) {
        String url = GATEWAY_URL + "/user/" + userId;
        return restTemplate.exchange(url, HttpMethod.PUT, 
                                   new HttpEntity<>(statusData), Map.class);
    }
    
    // 批量查询用户
    @PostMapping("/admin/users/search")
    public ResponseEntity<?> searchUsers(@RequestBody Map<String, Object> conditions) {
        String url = GATEWAY_URL + "/user/select";
        return restTemplate.postForEntity(url, conditions, Map.class);
    }
}
```

### 场景2：订单服务操作多个数据库

创建订单时需要操作商品库存和订单数据：

```java
// 订单创建逻辑
public void createOrder(OrderRequest request) {
    // 1. 检查商品库存（商家数据库）
    String productUrl = GATEWAY_URL + "/product/" + request.getProductId();
    Map<String, Object> product = restTemplate.getForObject(productUrl, Map.class);
    
    // 2. 减少库存（商家数据库）
    Map<String, Object> stockUpdate = Map.of("stock", 
        (Integer)product.get("stock") - request.getQuantity());
    restTemplate.exchange(productUrl, HttpMethod.PUT, 
                         new HttpEntity<>(stockUpdate), Map.class);
    
    // 3. 创建订单（网关数据库）
    String orderUrl = GATEWAY_URL + "/order";
    Map<String, Object> orderData = Map.of(
        "userId", request.getUserId(),
        "storeId", request.getStoreId(),
        "totalPrice", request.getTotalPrice(),
        "status", 0
    );
    restTemplate.postForEntity(orderUrl, orderData, Map.class);
}
```

### 场景3：数据统计和报表

生成跨数据库的统计报表：

```java
// 生成业务统计报表
public Map<String, Object> generateBusinessReport() {
    Map<String, Object> report = new HashMap<>();
    
    // 用户统计
    String userCountUrl = GATEWAY_URL + "/user/count";
    Long userCount = restTemplate.getForObject(userCountUrl, Map.class)
                                .get("data").get("count");
    
    // 商家统计
    String merchantCountUrl = GATEWAY_URL + "/merchant/count";
    Long merchantCount = restTemplate.getForObject(merchantCountUrl, Map.class)
                                    .get("data").get("count");
    
    // 订单统计
    String orderCountUrl = GATEWAY_URL + "/order/count";
    Long orderCount = restTemplate.getForObject(orderCountUrl, Map.class)
                                 .get("data").get("count");
    
    report.put("totalUsers", userCount);
    report.put("totalMerchants", merchantCount);
    report.put("totalOrders", orderCount);
    
    return report;
}
```

## 项目结构

```
src/
├── main/
│   ├── java/org/demo/gateway/
│   │   ├── GatewayServiceApplication.java     # 启动类
│   │   ├── config/                           # 配置类
│   │   │   ├── DataSourceConfig.java         # 多数据源配置
│   │   │   └── DynamicDataSource.java        # 动态数据源
│   │   ├── controller/                       # 控制器
│   │   │   └── DatabaseController.java       # 数据库操作API
│   │   ├── mapper/                           # 数据访问层
│   │   │   ├── UserMapper.java               # 用户表操作
│   │   │   ├── MerchantMapper.java           # 商家表操作
│   │   │   ├── StoreMapper.java              # 店铺表操作
│   │   │   ├── ProductMapper.java            # 商品表操作
│   │   │   ├── RiderMapper.java              # 骑手表操作
│   │   │   ├── AdminMapper.java              # 管理员表操作
│   │   │   └── OrderMapper.java              # 订单表操作
│   │   └── service/                          # 业务逻辑层
│   │       ├── DatabaseService.java          # 数据库服务接口
│   │       └── impl/
│   │           └── DatabaseServiceImpl.java  # 数据库服务实现
│   └── resources/
│       ├── application.yml                   # 配置文件
│       └── gateway-service.sql               # 数据库初始化脚本
└── test/
    └── java/                                 # 测试代码
```

## 配置说明

### 数据库配置

```yaml
# application.yml
spring:
  application:
    name: gateway-service
  
  # 主数据源（网关数据库）
  datasource:
    url: jdbc:mysql://localhost:3306/baoleme_common
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  # 多数据源配置
    user:
      jdbc-url: jdbc:mysql://localhost:3306/baoleme_user
      username: root
      password: root
      driver-class-name: com.mysql.cj.jdbc.Driver
      hikari:
        pool-name: UserHikariPool
        maximum-pool-size: 10
        minimum-idle: 5
    
    merchant:
      jdbc-url: jdbc:mysql://localhost:3306/baoleme_merchant
      username: root
      password: root
      driver-class-name: com.mysql.cj.jdbc.Driver
      hikari:
        pool-name: MerchantHikariPool
        maximum-pool-size: 10
        minimum-idle: 5

# 自定义配置
gateway:
  database:
    timeout: 30000
    batch-size: 1000
    enable-transaction: true
    pool-monitoring: true
```

### 日志配置

```yaml
logging:
  level:
    org.demo.gateway: debug
    org.springframework.jdbc: debug
    com.zaxxer.hikari: debug
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 性能优化

### 1. 连接池优化

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20        # 最大连接数
      minimum-idle: 5              # 最小空闲连接
      connection-timeout: 30000    # 连接超时
      idle-timeout: 600000         # 空闲超时
      max-lifetime: 1800000        # 连接最大生命周期
```

### 2. JVM参数优化

```bash
java -Xms512m -Xmx1024m -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar gateway-service.jar
```

### 3. 批量操作优化

- 使用批量插入/更新接口处理大量数据
- 合理设置批量大小（默认1000条）
- 启用事务管理确保数据一致性

## 监控和运维

### 健康检查

```bash
# 检查服务状态
curl http://localhost:8080/actuator/health

# 检查数据源健康状态
curl http://localhost:8080/api/database/health
```

### 日志监控

```bash
# 查看应用日志
tail -f logs/gateway-service.log

# 查看错误日志
grep "ERROR" logs/gateway-service.log
```

### 性能监控

- 监控数据库连接池使用情况
- 跟踪API响应时间
- 监控内存和CPU使用率

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 验证连接配置是否正确
   - 确认网络连通性

2. **表不存在错误**
   - 确认数据库初始化脚本已执行
   - 检查表名拼写是否正确
   - 验证数据源路由是否正确

3. **权限不足**
   - 检查数据库用户权限
   - 确认用户有相应表的操作权限

### 调试命令

```bash
# 检查表是否存在
curl http://localhost:8080/api/database/user/exists

# 查看表结构
curl http://localhost:8080/api/database/user/structure

# 测试数据库连接
curl http://localhost:8080/api/database/health
```

## 开发指南

### 添加新表支持

1. **创建Mapper接口**
```java
@Mapper
@Repository
public interface NewTableMapper {
    @Select("SELECT * FROM new_table WHERE id = #{id}")
    Map<String, Object> selectById(@Param("id") Long id);
    
    // 其他CRUD方法...
}
```

2. **更新数据源路由**
```java
// 在DynamicDataSource.getDataSourceKeyByTable()中添加
case "new_table":
    return "appropriate_datasource";
```

3. **更新服务实现**
```java
// 在DatabaseServiceImpl中添加对应的处理逻辑
case "new_table":
    return newTableMapper.selectById(id);
```

### 扩展API接口

可以根据业务需要在DatabaseController中添加新的接口方法，或创建专门的业务控制器。

## 版本历史

### v2.0.0 (2025-01-25)
- 🎯 **架构重构**: 简化为专注跨数据库操作的架构
- 🗄️ **多数据源**: 支持5个独立数据库的动态路由
- 📋 **统一接口**: 提供标准化的REST API
- 🚀 **性能优化**: 移除复杂消息传递，提升响应速度
- 📚 **完善文档**: 详细的使用指南和示例代码

### v1.0.0 (2025-01-24)
- 初始版本（已废弃）
- 复杂的消息传递和数据同步机制

## 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目维护者: Baoleme Team
- 邮箱: dev@baoleme.com
- 项目地址: https://github.com/baoleme/microservices

---

**注意**: 这是一个教学项目，用于演示微服务架构中跨数据库操作的设计和实现。在生产环境中使用前，请确保进行充分的测试和安全评估。