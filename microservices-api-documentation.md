# 饱了么微服务后端API文档

## 概述

饱了么外卖平台微服务化后端API文档

**版本**: 1.0.0  
**联系方式**: Baoleme Team (support@baoleme.com)

## 微服务架构说明

本系统采用微服务架构，包含以下核心服务：

- **admin-service**: 管理员服务 (端口: 8084)
- **user-service**: 用户服务 (端口: 8081)
- **merchant-service**: 商家服务 (端口: 8082)
- **rider-service**: 骑手服务 (端口: 8083)
- **common-service**: 公共服务 (端口: 8085)
- **gateway-forward-service**: 网关转发服务 (端口: 8090)

## 服务器地址

| 服务 | 地址 | 描述 |
|------|------|------|
| 网关服务器 | http://localhost:8090 | 开发环境网关服务器 |
| 管理员服务 | http://localhost:8084 | 管理员服务 |
| 用户服务 | http://localhost:8081 | 用户服务 |
| 商家服务 | http://localhost:8082 | 商家服务 |
| 骑手服务 | http://localhost:8083 | 骑手服务 |
| 公共服务 | http://localhost:8085 | 公共服务 |

## 认证说明

所有需要认证的API都使用JWT Bearer Token认证方式。

**请求头格式**:
```
Authorization: Bearer <JWT_TOKEN>
```

## 统一响应格式

所有API响应都遵循统一的格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": {}
}
```

## API分类

- [管理员认证](#管理员认证) - 管理员登录、登出相关接口
- [管理员管理](#管理员管理) - 管理员对用户、商家、骑手、店铺、商品的管理接口
- [用户认证](#用户认证) - 用户注册、登录、信息管理接口
- [用户功能](#用户功能) - 用户搜索、收藏、购物车、优惠券等功能接口
- [商家认证](#商家认证) - 商家注册、登录、信息管理接口
- [商家管理](#商家管理) - 商家店铺、商品管理接口
- [骑手认证](#骑手认证) - 骑手注册、登录、信息管理接口
- [骑手功能](#骑手功能) - 骑手接单、配送相关接口
- [订单管理](#订单管理) - 订单创建、查询、状态更新等接口
- [评论管理](#评论管理) - 评论提交、查看、管理接口
- [优惠券管理](#优惠券管理) - 优惠券创建、领取、使用接口
- [消息通信](#消息通信) - 用户间聊天消息接口

---

# API接口详情

## 管理员认证

### 管理员登录

**接口地址**: `POST /admin/login`

**接口描述**: 管理员使用ID和密码进行登录认证

**是否需要认证**: 否

**请求参数**:

```json
{
  "adminId": 1,
  "password": "string"
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| adminId | integer | 是 | 管理员ID |
| password | string | 是 | 密码 |

**响应示例**:

```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "id": 1,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 管理员登出

**接口地址**: `POST /admin/logout`

**接口描述**: 管理员登出系统，清除登录状态

**是否需要认证**: 是

**请求参数**: 无

**响应示例**:

```json
{
  "success": true,
  "message": "登出成功",
  "data": {}
}
```

## 管理员管理

### 分页查看用户列表

**接口地址**: `POST /admin/userlist`

**接口描述**: 管理员分页查询用户列表，支持关键词搜索和ID范围筛选

**是否需要认证**: 是

**请求参数**:

```json
{
  "keyword": "string",
  "gender": "male",
  "startId": 1,
  "endId": 100,
  "page": 1,
  "pageSize": 10
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| gender | string | 否 | 性别筛选 |
| startId | integer | 否 | 起始ID |
| endId | integer | 否 | 结束ID |
| page | integer | 否 | 页码，默认1 |
| pageSize | integer | 否 | 每页大小，默认10 |

**响应示例**:

```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "username": "用户名",
      "description": "用户描述",
      "phone": "13800138000",
      "avatar": "头像URL",
      "createdAt": "2024-01-01T00:00:00Z"
    }
  ]
}
```

### 分页查看商家列表

**接口地址**: `POST /admin/merchantlist`

**接口描述**: 管理员分页查询商家列表，支持关键词搜索和ID范围筛选

**是否需要认证**: 是

**请求参数**:

```json
{
  "keyword": "string",
  "startId": 1,
  "endId": 100,
  "page": 1,
  "pageSize": 10
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| startId | integer | 否 | 起始ID |
| endId | integer | 否 | 结束ID |
| page | integer | 否 | 页码，默认1 |
| pageSize | integer | 否 | 每页大小，默认10 |

### 分页查看店铺列表

**接口地址**: `POST /admin/storelist`

**接口描述**: 管理员分页查询店铺列表，支持关键词搜索、商家ID和状态筛选

**是否需要认证**: 是

**请求参数**:

```json
{
  "keyword": "string",
  "type": "string",
  "startRating": 4.0,
  "endRating": 5.0,
  "status": 1,
  "merchantId": 1,
  "page": 1,
  "pageSize": 10
}
```

### 分页查看商品列表

**接口地址**: `POST /admin/productlist`

**接口描述**: 管理员分页查询指定店铺的商品列表

**是否需要认证**: 是

**请求参数**:

```json
{
  "storeId": 1,
  "page": 1,
  "pageSize": 10
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| storeId | integer | 是 | 店铺ID |
| page | integer | 否 | 页码，默认1 |
| pageSize | integer | 否 | 每页大小，默认10 |

### 搜索店铺和商品

**接口地址**: `POST /admin/search`

**接口描述**: 管理员搜索店铺和商品，支持关键词、距离、价格、评分等多条件筛选

**是否需要认证**: 是

**请求参数**:

```json
{
  "keyword": "快餐",
  "distance": 5.0,
  "wishPrice": 30.0,
  "startRating": 4.0,
  "endRating": 5.0,
  "page": 1,
  "pageSize": 10
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| distance | number | 否 | 距离范围 |
| wishPrice | number | 否 | 期望价格 |
| startRating | number | 否 | 最低评分 |
| endRating | number | 否 | 最高评分 |
| page | integer | 否 | 页码，默认1 |
| pageSize | integer | 否 | 每页大小，默认10 |

## 用户认证

### 用户注册

**接口地址**: `POST /user/register`

**接口描述**: 新用户注册账号

**是否需要认证**: 否

**请求参数**:

```json
{
  "username": "用户名",
  "phone": "13800138000",
  "password": "password123",
  "gender": "male",
  "description": "个人描述"
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| username | string | 是 | 用户名 |
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |
| gender | string | 否 | 性别 (male/female/other) |
| description | string | 否 | 个人描述 |

**响应示例**:

```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "username": "用户名",
    "phone": "13800138000"
  }
}
```

### 用户登录

**接口地址**: `POST /user/login`

**接口描述**: 用户使用手机号和密码登录

**是否需要认证**: 否

**请求参数**:

```json
{
  "phone": "13800138000",
  "password": "password123"
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |

**响应示例**:

```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "用户名",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 获取用户信息

**接口地址**: `GET /user/info`

**接口描述**: 获取当前登录用户的详细信息

**是否需要认证**: 是

**请求参数**: 无

**响应示例**:

```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "用户名",
    "phone": "13800138000",
    "gender": "male",
    "description": "个人描述",
    "avatar": "头像URL",
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

## 用户功能

### 搜索店铺和商品

**接口地址**: `POST /user/search`

**接口描述**: 用户搜索店铺和商品，支持关键词、距离、价格、评分等多条件筛选

**是否需要认证**: 是

**请求参数**:

```json
{
  "keyword": "快餐",
  "distance": 5.0,
  "wishPrice": 30.0,
  "startRating": 4.0,
  "endRating": 5.0,
  "page": 1,
  "pageSize": 10
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| distance | number | 否 | 距离范围 |
| wishPrice | number | 否 | 期望价格 |
| startRating | number | 否 | 最低评分 |
| endRating | number | 否 | 最高评分 |
| page | integer | 否 | 页码，默认1 |
| pageSize | integer | 否 | 每页大小，默认10 |

**响应示例**:

```json
{
  "success": true,
  "message": "搜索成功",
  "data": {
    "results": [
      {
        "storeId": 1,
        "name": "店铺名称",
        "type": "快餐",
        "description": "店铺描述",
        "location": "店铺位置",
        "rating": 4.5,
        "status": 1,
        "image": "店铺图片URL",
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ]
  }
}
```

## 商家认证

### 商家注册

**接口地址**: `POST /merchant/register`

**接口描述**: 新商家注册账号

**是否需要认证**: 否

**请求参数**:

```json
{
  "username": "商家名称",
  "phone": "13800138000",
  "password": "password123",
  "businessLicense": "营业执照号",
  "description": "商家描述"
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| username | string | 是 | 商家用户名 |
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |
| businessLicense | string | 否 | 营业执照号 |
| description | string | 否 | 商家描述 |

### 商家登录

**接口地址**: `POST /merchant/login`

**接口描述**: 商家使用手机号和密码登录

**是否需要认证**: 否

**请求参数**:

```json
{
  "phone": "13800138000",
  "password": "password123"
}
```

## 商家管理

### 创建店铺

**接口地址**: `POST /store/create`

**接口描述**: 商家创建新店铺

**是否需要认证**: 是

**请求参数**:

```json
{
  "name": "店铺名称",
  "type": "快餐",
  "description": "店铺描述",
  "location": "店铺位置",
  "image": "店铺图片URL"
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | string | 是 | 店铺名称 |
| type | string | 是 | 店铺类型 |
| description | string | 否 | 店铺描述 |
| location | string | 是 | 店铺位置 |
| image | string | 否 | 店铺图片URL |

### 创建商品

**接口地址**: `POST /product/create`

**接口描述**: 商家在店铺中创建新商品

**是否需要认证**: 是

**请求参数**:

```json
{
  "storeId": 1,
  "name": "商品名称",
  "category": "主食",
  "price": 25.50,
  "description": "商品描述",
  "image": "商品图片URL",
  "stock": 100
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| storeId | integer | 是 | 店铺ID |
| name | string | 是 | 商品名称 |
| category | string | 否 | 商品分类 |
| price | number | 是 | 商品价格 |
| description | string | 否 | 商品描述 |
| image | string | 否 | 商品图片URL |
| stock | integer | 否 | 商品库存 |

### 获取商品详情

**接口地址**: `POST /product/info`

**接口描述**: 获取商品的详细信息，包括评价列表

**是否需要认证**: 是

**请求参数**:

```json
{
  "id": 1
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | integer | 是 | 商品ID |

## 骑手认证

### 骑手注册

**接口地址**: `POST /rider/register`

**接口描述**: 新骑手注册账号

**是否需要认证**: 否

**请求参数**:

```json
{
  "username": "骑手姓名",
  "phone": "13800138000",
  "password": "password123",
  "idCard": "身份证号",
  "vehicleType": "电动车",
  "vehiclePlate": "车牌号"
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| username | string | 是 | 骑手用户名 |
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |
| idCard | string | 否 | 身份证号 |
| vehicleType | string | 否 | 车辆类型 |
| vehiclePlate | string | 否 | 车牌号 |

### 骑手登录

**接口地址**: `POST /rider/login`

**接口描述**: 骑手使用手机号和密码登录

**是否需要认证**: 否

**请求参数**:

```json
{
  "phone": "13800138000",
  "password": "password123"
}
```

## 订单管理

### 创建订单

**接口地址**: `POST /orders/create`

**接口描述**: 用户创建新订单

**是否需要认证**: 是

**请求参数**:

```json
{
  "storeId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 25.50,
      "productName": "商品名称"
    }
  ],
  "userLocation": "用户地址",
  "deliveryPrice": 5.00,
  "couponId": 1,
  "remark": "订单备注",
  "deadline": "2024-01-01T12:00:00Z"
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| storeId | integer | 是 | 店铺ID |
| items | array | 是 | 订单商品列表 |
| userLocation | string | 是 | 用户位置 |
| deliveryPrice | number | 否 | 配送费用 |
| couponId | integer | 否 | 优惠券ID |
| remark | string | 否 | 订单备注 |
| deadline | string | 否 | 订单截止时间 |

### 获取可抢订单列表

**接口地址**: `GET /orders/available`

**接口描述**: 骑手获取可抢的订单列表

**是否需要认证**: 是

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| page | integer | 是 | 页码 |
| page_size | integer | 是 | 每页大小 |

### 抢单

**接口地址**: `PUT /orders/grab`

**接口描述**: 骑手抢取订单

**是否需要认证**: 是

**请求参数**:

```json
{
  "orderId": 1
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| orderId | integer | 是 | 订单ID |

### 取消订单

**接口地址**: `PUT /orders/cancel`

**接口描述**: 骑手取消订单

**是否需要认证**: 是

**请求参数**:

```json
{
  "id": 1,
  "new_status": 4,
  "cancelReason": "取消原因"
}
```

### 骑手更新订单状态

**接口地址**: `POST /orders/rider-update-status`

**接口描述**: 骑手更新订单配送状态

**是否需要认证**: 是

**请求参数**:

```json
{
  "id": 1,
  "new_status": 2
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | integer | 是 | 订单ID |
| new_status | integer | 是 | 目标状态（0:待接单 1:准备中 2:配送中 3:完成 4:取消） |

### 商家更新订单状态

**接口地址**: `PUT /orders/merchant-update`

**接口描述**: 商家更新订单状态（接单、准备完成等）

**是否需要认证**: 是

**请求参数**:

```json
{
  "id": 1,
  "new_status": 1,
  "cancelReason": "取消原因"
}
```

### 商家查看订单列表

**接口地址**: `GET /orders/merchant-list` 或 `POST /orders/merchant-list`

**接口描述**: 商家分页查看店铺订单列表

**是否需要认证**: 是

**GET请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| store_id | integer | 是 | 店铺ID |
| status | integer | 否 | 订单状态筛选 |
| page | integer | 是 | 页码 |
| page_size | integer | 是 | 每页大小 |

**POST请求参数**:

```json
{
  "store_id": 1,
  "status": 1,
  "page": 1,
  "page_size": 10
}
```

### 用户查看订单历史

**接口地址**: `POST /orders/user-history`

**接口描述**: 用户查看历史订单，支持状态和时间范围筛选

**是否需要认证**: 是

**请求参数**:

```json
{
  "status": 3,
  "startTime": "2024-01-01T00:00:00Z",
  "endTime": "2024-01-31T23:59:59Z",
  "page": 1,
  "pageSize": 10
}
```

### 用户查看当前订单

**接口地址**: `POST /orders/user-current`

**接口描述**: 用户查看当前进行中的订单

**是否需要认证**: 是

**请求参数**:

```json
{
  "page": 1,
  "page_size": 10
}
```

### 获取订单详情

**接口地址**: `GET /orders/{orderId}`

**接口描述**: 根据订单ID获取订单基本信息

**是否需要认证**: 是

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| orderId | integer | 是 | 订单ID |

### 获取订单商品明细

**接口地址**: `GET /orders/{orderId}/items`

**接口描述**: 获取订单的商品明细和价格信息

**是否需要认证**: 是

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| orderId | integer | 是 | 订单ID |

### 获取订单完整详情

**接口地址**: `POST /orders/detail`

**接口描述**: 获取订单的完整详情信息，包括用户、商家、店铺、商品等相关信息

**是否需要认证**: 是

**请求参数**:

```json
{
  "orderId": 1
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| orderId | integer | 是 | 订单ID |

## 评论管理

### 提交评论

**接口地址**: `POST /reviews/submit`

**接口描述**: 用户提交商品或店铺评论

**是否需要认证**: 是

**请求参数**:

```json
{
  "storeId": 1,
  "productId": 1,
  "rating": 5,
  "comment": "很好吃，推荐！",
  "images": ["图片URL1", "图片URL2"]
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| storeId | integer | 是 | 店铺ID |
| productId | integer | 否 | 商品ID |
| rating | integer | 是 | 评分（1-5星） |
| comment | string | 是 | 评价内容 |
| images | array | 否 | 评价图片列表 |

### 查看评论列表

**接口地址**: `POST /reviews/read`

**接口描述**: 商家查看店铺评论列表，支持筛选和分页

**是否需要认证**: 是

**请求参数**:

```json
{
  "store_id": 1,
  "page": 1,
  "page_size": 10,
  "type": "POSITIVE",
  "has_image": true
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| store_id | integer | 否 | 店铺ID |
| page | integer | 否 | 页码，默认1 |
| page_size | integer | 否 | 每页数量，默认10 |
| type | string | 否 | 筛选类型（POSITIVE:好评 NEGATIVE:差评） |
| has_image | boolean | 否 | 是否有图片筛选 |

## 优惠券管理

### 创建优惠券

**接口地址**: `POST /coupons/create`

**接口描述**: 商家创建优惠券

**是否需要认证**: 是

**请求参数**:

```json
{
  "name": "满50减10优惠券",
  "type": "FIXED",
  "value": 10.00,
  "minAmount": 50.00,
  "storeId": 1,
  "validFrom": "2024-01-01T00:00:00Z",
  "validTo": "2024-01-31T23:59:59Z",
  "totalCount": 100
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | string | 是 | 优惠券名称 |
| type | string | 是 | 优惠券类型（DISCOUNT:折扣 FIXED:满减） |
| value | number | 是 | 优惠券面值 |
| minAmount | number | 否 | 最低消费金额 |
| storeId | integer | 否 | 店铺ID |
| validFrom | string | 是 | 有效期开始时间 |
| validTo | string | 是 | 有效期结束时间 |
| totalCount | integer | 是 | 发放总数 |

### 查看可用优惠券

**接口地址**: `POST /coupons/available`

**接口描述**: 用户查看可用的优惠券列表

**是否需要认证**: 是

**请求参数**:

```json
{
  "store_id": 1
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| store_id | integer | 否 | 店铺ID |

### 领取优惠券

**接口地址**: `POST /coupons/claim`

**接口描述**: 用户领取优惠券

**是否需要认证**: 是

**请求参数**:

```json
{
  "couponId": 1
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| couponId | integer | 是 | 优惠券ID |

### 查看用户优惠券

**接口地址**: `POST /coupons/user`

**接口描述**: 用户查看自己的优惠券列表

**是否需要认证**: 是

**请求参数**:

```json
{
  "status": "AVAILABLE",
  "page": 1,
  "pageSize": 10
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| status | string | 否 | 优惠券状态筛选（AVAILABLE/USED/EXPIRED） |
| page | integer | 否 | 页码，默认1 |
| pageSize | integer | 否 | 每页大小，默认10 |

## 消息通信

### 发送消息

**接口地址**: `POST /messages/send`

**接口描述**: 用户间发送聊天消息

**是否需要认证**: 是

**请求参数**:

```json
{
  "receiverId": 2,
  "receiverRole": "merchant",
  "content": "您好，请问商品还有库存吗？"
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| receiverId | integer | 是 | 接收方用户ID |
| receiverRole | string | 是 | 接收方角色（user/merchant/rider） |
| content | string | 是 | 消息内容（最大1000字符） |

### 获取聊天记录

**接口地址**: `POST /messages/history`

**接口描述**: 获取与指定用户的聊天记录

**是否需要认证**: 是

**请求参数**:

```json
{
  "receiverId": 2,
  "receiverRole": "merchant",
  "page": 1,
  "pageSize": 20
}
```

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| receiverId | integer | 是 | 接收方用户ID |
| receiverRole | string | 是 | 接收方角色（user/merchant/rider） |
| page | integer | 是 | 页码 |
| pageSize | integer | 是 | 每页大小 |

---

# 数据模型

## 订单状态说明

| 状态码 | 状态名称 | 描述 |
|--------|----------|------|
| 0 | 待接单 | 订单已创建，等待骑手接单 |
| 1 | 准备中 | 商家正在准备订单 |
| 2 | 配送中 | 骑手正在配送订单 |
| 3 | 完成 | 订单已完成 |
| 4 | 取消 | 订单已取消 |

## 用户角色说明

| 角色 | 描述 |
|------|------|
| user | 普通用户 |
| merchant | 商家 |
| rider | 骑手 |
| admin | 管理员 |

## 优惠券类型说明

| 类型 | 描述 |
|------|------|
| DISCOUNT | 折扣券（如8折券） |
| FIXED | 满减券（如满50减10） |

## 优惠券状态说明

| 状态 | 描述 |
|------|------|
| AVAILABLE | 可用 |
| USED | 已使用 |
| EXPIRED | 已过期 |

---

# 错误码说明

## HTTP状态码

| 状态码 | 描述 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问，权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 业务错误码

业务错误信息通过响应体中的 `message` 字段返回，常见错误信息包括：

- "用户未登录"
- "权限不足"
- "参数不能为空"
- "用户名或密码错误"
- "订单不存在"
- "库存不足"
- "优惠券已过期"
- "订单状态不允许此操作"

---

# 开发说明

## 环境要求

- Java 17+
- Spring Boot 3.x
- MySQL 8.0+
- Redis 6.0+

## 本地开发

1. 启动各个微服务
2. 确保数据库和Redis连接正常
3. 使用网关地址 `http://localhost:8090` 进行API调用

## 测试建议

1. 使用Postman或类似工具导入此API文档
2. 先进行用户注册和登录获取Token
3. 在后续请求中携带Authorization头部
4. 按照业务流程进行测试：注册→登录→搜索→下单→支付→配送→评价

---

*文档版本: 1.0.0*  
*最后更新: 2024-01-01*  
*维护团队: Baoleme Team*