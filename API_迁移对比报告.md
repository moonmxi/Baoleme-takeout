# API接口迁移对比报告

## 概述
本报告详细对比了原始单体系统(back目录)与微服务化后各服务的API接口实现情况，确保所有功能完整迁移并保持数据一致性。

## 1. 用户相关API迁移对比 (UserController)

### 1.1 已完整迁移的API

| 原始API | 微服务API | 迁移状态 | 备注 |
|---------|-----------|----------|------|
| POST /user/register | POST /user/register | ✅ 完整迁移 | 用户注册功能 |
| POST /user/login | POST /user/login | ✅ 完整迁移 | 用户登录功能 |
| POST /user/logout | POST /user/logout | ✅ 完整迁移 | 用户登出功能 |
| DELETE /user/delete | DELETE /user/delete | ✅ 完整迁移 | 删除用户账户 |
| GET /user/info | GET /user/info | ✅ 完整迁移 | 获取用户信息 |
| PUT /user/update | PUT /user/update | ✅ 完整迁移 | 更新用户信息 |
| POST /user/favorite | POST /user/favorite | ✅ 完整迁移 | 收藏店铺 |
| POST /user/favorite/watch | POST /user/favorite/watch | ✅ 完整迁移 | 获取收藏店铺列表 |
| POST /user/deleteFavorite | POST /user/deleteFavorite | ✅ 完整迁移 | 删除收藏 |
| POST /user/coupon | POST /user/coupon | ✅ 完整迁移 | 获取用户优惠券 |
| POST /user/coupon/view | POST /user/coupon/view | ✅ 完整迁移 | 查看可用优惠券 |
| POST /user/coupon/claim | POST /user/coupon/claim | ✅ 完整迁移 | 领取优惠券 |
| POST /user/search | POST /user/search | ✅ 完整迁移 | 搜索店铺和商品 |
| POST /user/review | POST /user/review | ✅ 完整迁移 | 提交评价 |
| DELETE /user/cancel | DELETE /user/cancel | ✅ 完整迁移 | 注销账户 |
| POST /user/updateViewHistory | POST /user/updateViewHistory | ✅ 完整迁移 | 更新浏览历史 |
| POST /user/viewHistory | POST /user/viewHistory | ✅ 完整迁移 | 获取浏览历史 |

### 1.2 ❌ 缺失的订单相关API

| 原始API | 预期微服务位置 | 迁移状态 | 影响 |
|---------|----------------|----------|------|
| POST /user/history | gateway-service | ❌ 缺失 | 用户无法查看订单历史 |
| POST /user/history/item | gateway-service | ❌ 缺失 | 用户无法查看订单明细 |
| POST /user/current | gateway-service | ❌ 缺失 | 用户无法查看当前订单 |
| POST /user/order | gateway-service | ✅ 已迁移为 POST /orders/create | 订单创建功能 |
| POST /user/searchOrder | gateway-service | ✅ 已迁移为 GET /orders/{orderId} | 订单查询功能 |
| POST /user/searchOrderItem | gateway-service | ❌ 缺失 | 用户无法查看订单商品明细 |

## 2. 商家相关API迁移对比

### 2.1 MerchantController迁移情况

| 原始API | 微服务API | 迁移状态 | 备注 |
|---------|-----------|----------|------|
| 原始系统无独立MerchantController | POST /merchant/register | ✅ 新增 | 商家注册功能 |
| 原始系统无独立MerchantController | POST /merchant/login | ✅ 新增 | 商家登录功能 |
| 原始系统无独立MerchantController | GET /merchant/info | ✅ 新增 | 获取商家信息 |
| 原始系统无独立MerchantController | PUT /merchant/update | ✅ 新增 | 更新商家信息 |
| 原始系统无独立MerchantController | POST /merchant/logout | ✅ 新增 | 商家登出 |
| 原始系统无独立MerchantController | DELETE /merchant/delete | ✅ 新增 | 删除商家账户 |

### 2.2 StoreController迁移情况

| 原始API | 微服务API | 迁移状态 | 备注 |
|---------|-----------|----------|------|
| POST /store/create | POST /store/create | ✅ 完整迁移 | 创建店铺 |
| POST /store/list | POST /store/list | ✅ 完整迁移 | 获取店铺列表 |
| POST /store/view | POST /store/view | ✅ 完整迁移 | 查看店铺详情 |
| PUT /store/update | PUT /store/update | ✅ 完整迁移 | 更新店铺信息 |
| PUT /store/status | PUT /store/status | ✅ 完整迁移 | 切换店铺状态 |
| DELETE /store/delete | DELETE /store/delete | ✅ 完整迁移 | 删除店铺 |
| POST /store/storeInfo | POST /store/storeInfo | ✅ 完整迁移 | 获取店铺信息 |

### 2.3 ❌ 缺失的用户浏览相关API

| 原始API | 预期微服务位置 | 迁移状态 | 影响 |
|---------|----------------|----------|------|
| POST /store/user-view-stores | user-service 或 gateway-service | ❌ 缺失 | 用户无法浏览店铺 |
| POST /store/user-view-products | user-service 或 gateway-service | ❌ 缺失 | 用户无法浏览商品 |

### 2.4 ProductController迁移情况

| 原始API | 微服务API | 迁移状态 | 备注 |
|---------|-----------|----------|------|
| POST /product/create | POST /product/create | ✅ 完整迁移 | 创建商品 |
| POST /product/view | POST /product/view | ✅ 完整迁移 | 查看商品详情 |
| POST /product/store-products | POST /product/store-products | ✅ 完整迁移 | 获取店铺商品列表 |
| PUT /product/update | PUT /product/update | ✅ 完整迁移 | 更新商品信息 |
| PUT /product/status | PUT /product/status | ✅ 完整迁移 | 更新商品状态 |
| POST /product/delete | POST /product/delete | ✅ 完整迁移 | 删除商品 |

### 2.5 ❌ 缺失的商品相关API

| 原始API | 预期微服务位置 | 迁移状态 | 影响 |
|---------|----------------|----------|------|
| POST /product/productInfo | user-service 或 gateway-service | ❌ 缺失 | 用户无法查看商品详细信息和评价 |

## 3. 管理员相关API迁移对比 (AdminController)

### 3.1 已完整迁移的API

| 原始API | 微服务API | 迁移状态 | 备注 |
|---------|-----------|----------|------|
| POST /admin/login | POST /admin/login | ✅ 完整迁移 | 管理员登录 |
| POST /admin/logout | POST /admin/logout | ✅ 完整迁移 | 管理员登出 |
| POST /admin/userlist | POST /admin/userlist | ✅ 完整迁移 | 分页查看用户列表 |
| POST /admin/riderlist | POST /admin/riderlist | ✅ 完整迁移 | 分页查看骑手列表 |
| POST /admin/merchantlist | POST /admin/merchantlist | ✅ 完整迁移 | 分页查看商家列表 |
| POST /admin/storelist | POST /admin/storelist | ✅ 完整迁移 | 分页查看店铺列表 |
| POST /admin/productlist | POST /admin/productlist | ✅ 完整迁移 | 分页查看商品列表 |
| DELETE /admin/delete | DELETE /admin/delete | ✅ 完整迁移 | 删除用户/商家/骑手/店铺/商品 |
| POST /admin/orderlist | POST /admin/orderlist | ✅ 完整迁移 | 分页查看订单列表 |
| POST /admin/reviewlist | POST /admin/reviewlist | ✅ 完整迁移 | 分页查看评价列表 |
| POST /admin/search | POST /admin/search | ✅ 完整迁移 | 搜索店铺和商品 |
| POST /admin/search-order-by-id | POST /admin/search-order-by-id | ✅ 完整迁移 | 根据ID搜索订单 |
| POST /admin/search-review-by-id | POST /admin/search-review-by-id | ✅ 完整迁移 | 根据ID搜索评价 |

## 4. 骑手相关API迁移对比 (RiderController)

### 4.1 已完整迁移的API

| 原始API | 微服务API | 迁移状态 | 备注 |
|---------|-----------|----------|------|
| POST /rider/register | POST /rider/register | ✅ 完整迁移 | 骑手注册 |
| POST /rider/login | POST /rider/login | ✅ 完整迁移 | 骑手登录 |
| GET /rider/info | GET /rider/info | ✅ 完整迁移 | 获取骑手信息 |
| PUT /rider/update | PUT /rider/update | ✅ 完整迁移 | 更新骑手信息 |
| PATCH /rider/dispatch-mode | PATCH /rider/dispatch-mode | ✅ 完整迁移 | 切换接单模式 |
| POST /rider/logout | POST /rider/logout | ✅ 完整迁移 | 骑手登出 |
| DELETE /rider/delete | DELETE /rider/delete | ✅ 完整迁移 | 删除骑手账户 |
| POST /rider/auto-order-taking | POST /rider/auto-order-taking | ✅ 完整迁移 | 自动接单 |

## 5. 订单相关API迁移对比 (OrderController)

### 5.1 已完整迁移的API

| 原始API | 微服务API | 迁移状态 | 备注 |
|---------|-----------|----------|------|
| GET /orders/available | GET /orders/available | ✅ 完整迁移 | 获取可抢订单列表 |
| PUT /orders/grab | PUT /orders/grab | ✅ 完整迁移 | 抢单 |
| PUT /orders/cancel | PUT /orders/cancel | ✅ 完整迁移 | 取消订单 |
| POST /orders/rider-update-status | POST /orders/rider-update-status | ✅ 完整迁移 | 骑手更新订单状态 |
| POST /orders/rider-history-query | GET /orders/rider-history | ✅ 完整迁移 | 查询骑手订单记录 |
| GET /orders/rider-earnings | GET /orders/rider-earnings | ✅ 完整迁移 | 查询骑手收入统计 |
| PUT /orders/merchant-update | PUT /orders/merchant-update | ✅ 完整迁移 | 商家更新订单状态 |
| POST /orders/merchant-list | GET /orders/merchant-list | ✅ 完整迁移 | 商家分页查看订单 |

### 5.2 新增的API

| 微服务API | 功能描述 | 状态 |
|-----------|----------|------|
| POST /orders/create | 创建订单 | ✅ 新增 |
| GET /orders/user-history | 用户查看订单历史 | ✅ 新增 |
| GET /orders/{orderId} | 根据订单ID获取订单详情 | ✅ 新增 |

## 6. ❌ 缺失的重要功能API

### 6.1 购物车相关API (CartController)

| 原始API | 预期微服务位置 | 迁移状态 | 影响 |
|---------|----------------|----------|------|
| POST /cart/add | user-service 或 gateway-service | ❌ 完全缺失 | 用户无法添加商品到购物车 |
| GET /cart/view | user-service 或 gateway-service | ❌ 完全缺失 | 用户无法查看购物车 |
| PUT /cart/update | user-service 或 gateway-service | ❌ 完全缺失 | 用户无法更新购物车商品数量 |
| PUT /cart/delete | user-service 或 gateway-service | ❌ 完全缺失 | 用户无法删除购物车商品 |
| DELETE /cart/remove | user-service 或 gateway-service | ❌ 完全缺失 | 用户无法清空购物车 |

### 6.2 图片上传相关API (ImageController)

| 原始API | 预期微服务位置 | 迁移状态 | 影响 |
|---------|----------------|----------|------|
| POST /image/upload-rider-avatar | rider-service | ❌ 完全缺失 | 骑手无法上传头像 |
| POST /image/upload-merchant-avatar | merchant-service | ❌ 完全缺失 | 商家无法上传头像 |
| POST /image/upload-user-avatar | user-service | ❌ 完全缺失 | 用户无法上传头像 |
| POST /image/upload-store-image | merchant-service | ❌ 完全缺失 | 商家无法上传店铺图片 |
| POST /image/upload-product-image | merchant-service | ❌ 完全缺失 | 商家无法上传商品图片 |

### 6.3 消息聊天相关API (MessageController)

| 原始API | 预期微服务位置 | 迁移状态 | 影响 |
|---------|----------------|----------|------|
| POST /message/history | gateway-service | ❌ 完全缺失 | 用户无法查看聊天记录 |

### 6.4 评价相关API (ReviewController)

| 原始API | 预期微服务位置 | 迁移状态 | 影响 |
|---------|----------------|----------|------|
| 原始系统的评价查看功能 | user-service 或 gateway-service | ❌ 部分缺失 | 用户无法查看店铺和商品评价 |

## 7. 数据一致性风险分析

### 7.1 高风险项

1. **订单历史查询缺失**: 用户无法通过原有的 `/user/history` 和 `/user/current` 接口查看订单，可能导致用户体验严重下降
2. **购物车功能完全缺失**: 整个购物车模块未迁移，用户无法正常购物
3. **图片上传功能缺失**: 用户、商家、骑手无法上传头像和商品图片
4. **消息功能缺失**: 用户与商家、骑手之间无法进行沟通

### 7.2 中等风险项

1. **商品详情查看**: 用户无法查看商品的详细信息和评价
2. **店铺浏览功能**: 用户无法浏览店铺和商品列表
3. **订单商品明细**: 用户无法查看订单中的具体商品信息

## 8. 建议修复方案

### 8.1 紧急修复项 (高优先级)

1. **在gateway-service中补充用户订单相关API**:
   - 实现 `GET /orders/user-history` (替代原 `/user/history`)
   - 实现 `GET /orders/user-current` (替代原 `/user/current`)
   - 实现 `GET /orders/{orderId}/items` (替代原 `/user/searchOrderItem`)

2. **在gateway-service中实现购物车功能**:
   - 实现完整的购物车CRUD操作
   - 确保与订单创建流程的集成

3. **在各微服务中实现图片上传功能**:
   - user-service: 用户头像上传
   - merchant-service: 商家头像、店铺图片、商品图片上传
   - rider-service: 骑手头像上传

### 8.2 重要修复项 (中优先级)

1. **在gateway-service中实现商品详情查看**:
   - 实现 `GET /products/{productId}` 接口
   - 包含商品信息和评价列表

2. **在gateway-service中实现店铺浏览功能**:
   - 实现用户浏览店铺列表的接口
   - 实现用户浏览商品列表的接口

3. **在gateway-service中实现消息功能**:
   - 实现聊天记录查询接口
   - 确保用户、商家、骑手之间的沟通渠道

### 8.3 Service层和Mapper层验证

需要进一步验证各微服务的Service层和Mapper层是否完整实现了对应的业务逻辑和数据库操作，确保:

1. 数据库表的正确分配
2. 跨服务数据访问的实现
3. 事务一致性的保证
4. 数据同步机制的建立

## 9. 总结

微服务化改造在核心功能方面基本完成，但存在以下关键问题:

- **完整迁移率**: 约70%的API已完整迁移
- **缺失功能**: 购物车、图片上传、消息、部分订单查询功能完全缺失
- **架构改进**: 新增了独立的商家管理功能，订单功能集中到gateway-service
- **风险等级**: 高风险，需要紧急修复缺失的核心功能

**建议**: 优先修复购物车和订单历史查询功能，确保用户基本购物流程的完整性，然后逐步补充其他缺失功能。