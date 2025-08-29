# Token传递问题修复验证指南

## 修复内容总结

### 1. 统一JWT工具类实现
- **admin-service**: 添加了缺失的`validateToken`、`extractUserId`、`extractRole`、`extractUsername`方法
- **gateway-service**: 保持原有完整的JWT工具类实现
- **结果**: 两个服务现在使用相同的JWT验证逻辑和密钥

### 2. 统一Redis存储逻辑
- **问题**: admin-service的token存储方式与其他微服务不一致
- **修复**: 统一使用双key存储模式：`{role}:token:{token}`存储userId，`{role}:login:{userId}`存储token
- **影响**: 所有微服务现在使用相同的token存储和验证机制

### 3. 改进JWT认证过滤器
- **admin-service**: 添加了token有效性验证，修正了Redis key查找逻辑
- **gateway-service**: 添加了详细的调试日志，保持白名单配置正确

### 4. 增强日志记录
- 添加了详细的JWT验证日志，便于调试token传递问题
- 包含请求路径、用户信息和token前缀信息

## 验证步骤

### 步骤1: 启动服务
```bash
# 启动admin-service (端口8084)
cd microservices/admin-service
mvn spring-boot:run

# 启动gateway-service (端口8080)
cd microservices/gateway-service  
mvn spring-boot:run
```

### 步骤2: 管理员登录
```bash
curl -X POST http://localhost:8084/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "adminId": "admin",
    "password": "password"
  }'
```

预期响应:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 步骤3: 使用token调用gateway-service
```bash
# 替换{TOKEN}为步骤2获得的token
curl -X POST http://localhost:8080/api/database/user/page?page=1&pageSize=10 \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{}'
```

### 步骤4: 通过admin-service调用gateway API
```bash
# 替换{TOKEN}为步骤2获得的token
curl -X POST http://localhost:8084/admin/userlist \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "page": 1,
    "pageSize": 10
  }'
```

## 预期结果

### 成功情况
1. **admin-service日志**应显示:
   ```
   JWT认证成功，用户ID: 1, 角色: admin
   调用网关API获取用户列表: page=1, pageSize=10
   ```

2. **gateway-service日志**应显示:
   ```
   JWT验证成功: POST /api/database/user/page - 用户ID=1, 角色=admin, 用户名=admin
   ```

3. **API响应**应返回用户列表数据而不是认证错误

### 失败情况排查

#### 如果gateway-service返回401错误:
1. 检查token格式是否正确（Bearer + 空格 + token）
2. 检查两个服务的JWT密钥是否一致
3. 查看gateway-service日志中的具体错误信息

#### 如果admin-service内部调用失败:
1. 检查GatewayApiClient中的token传递逻辑
2. 确认gateway-service的URL配置正确
3. 检查网络连接和端口配置

## 关键修复点

### 1. JWT密钥统一
两个服务都使用相同的密钥: `baoleme_secret_key_1234567890123456`

### 2. Token格式标准化
- 请求头格式: `Authorization: Bearer {token}`
- Token提取: `authHeader.substring(7)` 移除"Bearer "前缀

### 3. Redis存储规范
- Token映射Key: `{role}:token:{token}` → 存储userId
- 登录状态Key: `{role}:login:{userId}` → 存储token
- 过期时间: 24小时
- 支持的角色: user, merchant, rider, admin

### 4. 错误处理改进
- 统一的错误响应格式
- 详细的日志记录
- 清晰的错误消息

## 注意事项

1. **安全性**: JWT密钥应该从环境变量或配置文件中读取，不应硬编码
2. **性能**: Redis连接应该使用连接池
3. **监控**: 建议添加JWT验证失败的监控指标
4. **测试**: 应该添加单元测试和集成测试来验证JWT功能

## 后续优化建议

1. **配置外部化**: 将JWT密钥和过期时间配置到application.yml中
2. **异常处理**: 添加更细粒度的异常处理和错误码
3. **性能优化**: 考虑使用JWT的无状态特性，减少Redis查询
4. **安全增强**: 添加token刷新机制和黑名单功能