# 饱了么微服务API接口文档

## 概述

本文档描述了饱了么微服务系统的所有API接口，包括用户服务、商家服务、骑手服务、管理员服务和网关服务。

### 服务端口分配

| 服务名称 | 端口 | 上下文路径 | 描述 |
|---------|------|----------|---------|
| gateway-service | 8080 | / | API网关服务 |
| user-service | 8081 | /user-service | 用户服务 |
| merchant-service | 8082 | /merchant-service | 商家服务 |
| rider-service | 8083 | /rider-service | 骑手服务 |
| admin-service | 8084 | /admin-service | 管理员服务 |

### 网关路由规则

- `/api/user/**` → user-service (8081)
- `/api/merchant/**`, `/api/store/**`, `/api/product/**` → merchant-service (8082)
- `/api/rider/**` → rider-service (8083)
- `/api/admin/**`, `/api/stats/**` → admin-service (8084)
- `/api/order/**`, `/api/cart/**`, `/api/coupon/**`, `/api/review/**`, `/api/messages/**`, `/api/products/**` → gateway-service (8080)

### 通用响应格式

```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 认证方式

大部分接口需要在请求头中携带JWT Token：
```
Authorization: Bearer <token>
```

---

## 1. 用户服务 (User Service)

### 1.1 用户管理

#### 1.1.1 用户注册

**接口路径**: `POST /api/user/register`

**请求参数**:
```json
{
  "username": "string",     // 用户名，2-20个字符
  "password": "string",     // 密码，6-20个字符
  "phone": "string"         // 手机号，11位数字
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "testuser",
    "phone": "13800138000"
  }
}
```

#### 1.1.2 用户登录

**接口路径**: `POST /api/user/login`

**请求参数**:
```json
{
  "phone": "string",        // 手机号
  "password": "string"      // 密码
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "phone": "13800138000"
    }
  }
}
```

#### 1.1.3 用户登出

**接口路径**: `POST /api/user/logout`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "登出成功"
}
```

#### 1.1.4 获取用户信息

**接口路径**: `GET /api/user/info`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "testuser",
    "phone": "13800138000",
    "avatar": "http://example.com/avatar.jpg",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

#### 1.1.5 更新用户信息

**接口路径**: `PUT /api/user/update`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "username": "string",     // 可选，用户名
  "password": "string",     // 可选，新密码
  "phone": "string"         // 可选，手机号
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "用户信息更新成功"
}
```

#### 1.1.6 删除用户账户

**接口路径**: `DELETE /api/user/delete`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "账户删除成功"
}
```

#### 1.1.7 注销用户账户

**接口路径**: `DELETE /api/user/cancel`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "账户注销成功"
}
```

### 1.2 店铺收藏

#### 1.2.1 收藏店铺

**接口路径**: `POST /api/user/favorite`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1              // 店铺ID
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "收藏成功"
}
```

#### 1.2.2 获取收藏店铺列表

**接口路径**: `POST /api/user/favorite/watch`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "type": "string",         // 可选，店铺类型
  "distance": 5.0,          // 可选，距离范围(km)
  "wishPrice": 50.0,        // 可选，期望价格
  "startRating": 4.0,       // 可选，最低评分
  "endRating": 5.0,         // 可选，最高评分
  "page": 1,                // 页码
  "pageSize": 10            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "storeId": 1,
      "storeName": "美味餐厅",
      "rating": 4.5,
      "distance": 2.3,
      "averagePrice": 35.0
    }
  ]
}
```

#### 1.2.3 取消收藏店铺

**接口路径**: `POST /api/user/deleteFavorite`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1              // 店铺ID
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "取消收藏成功"
}
```

### 1.3 优惠券管理

#### 1.3.1 获取用户优惠券

**接口路径**: `POST /api/user/coupon`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1              // 可选，店铺ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "couponId": 1,
      "name": "满50减10",
      "discount": 10.0,
      "minAmount": 50.0,
      "expiryDate": "2024-12-31T23:59:59",
      "isUsed": false
    }
  ]
}
```

#### 1.3.2 查看可用优惠券

**接口路径**: `POST /api/user/coupon/view`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1              // 店铺ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "couponId": 1,
      "name": "新用户专享",
      "discount": 15.0,
      "minAmount": 30.0,
      "available": true
    }
  ]
}
```

#### 1.3.3 领取优惠券

**接口路径**: `POST /api/user/coupon/claim`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "couponId": 1             // 优惠券ID
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "优惠券领取成功"
}
```

### 1.4 搜索功能

#### 1.4.1 搜索店铺和商品

**接口路径**: `POST /api/user/search`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "keyword": "string",      // 搜索关键词
  "distance": 5.0,          // 可选，距离范围
  "wishPrice": 50.0,        // 可选，期望价格
  "startRating": 4.0,       // 可选，最低评分
  "endRating": 5.0,         // 可选，最高评分
  "page": 1,                // 页码
  "pageSize": 10            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "stores": [
      {
        "storeId": 1,
        "storeName": "美味餐厅",
        "rating": 4.5,
        "distance": 2.3
      }
    ],
    "products": [
      {
        "productId": 1,
        "productName": "宫保鸡丁",
        "price": 28.0,
        "storeName": "美味餐厅"
      }
    ]
  }
}
```

### 1.5 评价管理

#### 1.5.1 提交评价

**接口路径**: `POST /api/user/review`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1,             // 店铺ID
  "productId": 1,           // 商品ID
  "rating": 5,              // 评分(1-5)
  "comment": "string",      // 评价内容
  "image": "string"         // 可选，评价图片URL
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "reviewId": 1,
    "rating": 5,
    "comment": "非常好吃！",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

### 1.6 浏览历史

#### 1.6.1 更新浏览历史

**接口路径**: `POST /api/user/updateViewHistory`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1,             // 店铺ID
  "viewTime": "2024-01-01T12:00:00"  // 浏览时间
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "浏览历史更新成功"
}
```

#### 1.6.2 获取浏览历史

**接口路径**: `POST /api/user/viewHistory`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "page": 1,                // 页码
  "pageSize": 10            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "storeId": 1,
      "storeName": "美味餐厅",
      "viewTime": "2024-01-01T12:00:00"
    }
  ]
}
```

### 1.7 图片上传

#### 1.7.1 上传用户头像

**接口路径**: `POST /api/user/image/upload-user-avatar`

**请求头**: `Authorization: Bearer <token>`

**请求参数**: `multipart/form-data`
- `file`: 图片文件 (最大5MB，支持jpg/png/gif)

**响应数据**:
```json
{
  "success": true,
  "data": {
    "message": "头像上传成功",
    "avatar_url": "/uploads/user/avatar/2024-01-01/uuid.jpg",
    "file_size": 1024000,
    "upload_time": "2024-01-01T12:00:00"
  }
}
```

#### 1.7.2 删除用户头像

**接口路径**: `DELETE /api/user/image/delete-user-avatar`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "头像删除成功"
}
```

---

## 2. 商家服务 (Merchant Service)

### 2.1 商家管理

#### 2.1.1 商家注册

**接口路径**: `POST /api/merchant/register`

**请求参数**:
```json
{
  "username": "string",     // 商家用户名，2-20个字符
  "password": "string",     // 密码，6-20个字符
  "phone": "string"         // 手机号，11位数字
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "merchant01",
    "phone": "13800138001"
  }
}
```

#### 2.1.2 商家登录

**接口路径**: `POST /api/merchant/login`

**请求参数**:
```json
{
  "phone": "string",        // 手机号
  "password": "string"      // 密码
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "merchantInfo": {
      "id": 1,
      "username": "merchant01",
      "phone": "13800138001"
    }
  }
}
```

#### 2.1.3 获取商家信息

**接口路径**: `GET /api/merchant/info`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "merchant01",
    "phone": "13800138001",
    "avatar": "http://example.com/avatar.jpg",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

#### 2.1.4 更新商家信息

**接口路径**: `PUT /api/merchant/update`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "username": "string",     // 可选，商家用户名
  "password": "string",     // 可选，新密码
  "phone": "string"         // 可选，手机号
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "商家信息更新成功"
}
```

#### 2.1.5 商家登出

**接口路径**: `POST /api/merchant/logout`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "登出成功"
}
```

#### 2.1.6 删除商家账户

**接口路径**: `DELETE /api/merchant/delete`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "账户删除成功"
}
```

### 2.2 店铺管理

#### 2.2.1 创建店铺

**接口路径**: `POST /api/store/create`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "name": "string",         // 店铺名称
  "location": "string",     // 店铺地址
  "description": "string",  // 店铺描述
  "image": "string"         // 可选，店铺图片URL
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1
  }
}
```

#### 2.2.2 获取店铺列表

**接口路径**: `POST /api/store/list`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "page": 1,                // 页码
  "pageSize": 10            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "美味餐厅",
      "location": "北京市朝阳区",
      "status": 1,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

#### 2.2.3 查看店铺详情

**接口路径**: `POST /api/store/view`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1              // 店铺ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "美味餐厅",
    "location": "北京市朝阳区",
    "description": "正宗川菜",
    "image": "http://example.com/store.jpg",
    "status": 1,
    "rating": 4.5,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

#### 2.2.4 更新店铺信息

**接口路径**: `PUT /api/store/update`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1,             // 店铺ID
  "name": "string",         // 可选，店铺名称
  "location": "string",     // 可选，店铺地址
  "description": "string",  // 可选，店铺描述
  "image": "string"         // 可选，店铺图片URL
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "店铺信息更新成功"
}
```

#### 2.2.5 切换店铺状态

**接口路径**: `PUT /api/store/status`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1,             // 店铺ID
  "status": 1               // 状态：0-关闭，1-营业
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "店铺状态更新成功"
}
```

#### 2.2.6 删除店铺

**接口路径**: `DELETE /api/store/delete`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1              // 店铺ID
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "店铺删除成功"
}
```

#### 2.2.7 获取店铺信息（公开接口）

**接口路径**: `POST /api/store/storeInfo`

**请求参数**:
```json
{
  "storeId": 1              // 店铺ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "美味餐厅",
    "location": "北京市朝阳区",
    "description": "正宗川菜",
    "rating": 4.5,
    "status": 1
  }
}
```

### 2.3 商品管理

#### 2.3.1 创建商品

**接口路径**: `POST /api/product/create`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1,             // 店铺ID
  "name": "string",         // 商品名称
  "description": "string",  // 商品描述
  "price": 28.50,           // 商品价格
  "category": "string",     // 商品分类
  "stock": 100,             // 库存数量
  "image": "string"         // 可选，商品图片URL
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "productId": 1
  }
}
```

#### 2.3.2 查看商品详情

**接口路径**: `POST /api/product/view`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "productId": 1,           // 商品ID
  "storeId": 1              // 店铺ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "宫保鸡丁",
    "description": "经典川菜",
    "price": 28.50,
    "category": "川菜",
    "stock": 100,
    "image": "http://example.com/product.jpg",
    "status": 1,
    "rating": 4.8
  }
}
```

#### 2.3.3 获取店铺商品列表

**接口路径**: `POST /api/product/store-products`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1,             // 店铺ID
  "page": 1,                // 页码
  "pageSize": 10            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "宫保鸡丁",
      "price": 28.50,
      "category": "川菜",
      "stock": 100,
      "status": 1
    }
  ]
}
```

#### 2.3.4 更新商品信息

**接口路径**: `PUT /api/product/update`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "productId": 1,           // 商品ID
  "name": "string",         // 可选，商品名称
  "description": "string",  // 可选，商品描述
  "price": 28.50,           // 可选，商品价格
  "category": "string",     // 可选，商品分类
  "stock": 100,             // 可选，库存数量
  "image": "string"         // 可选，商品图片URL
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "商品信息更新成功"
}
```

#### 2.3.5 更新商品状态

**接口路径**: `PUT /api/product/status`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "productId": 1,           // 商品ID
  "status": 1               // 状态：0-下架，1-上架
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "商品状态更新成功"
}
```

#### 2.3.6 删除商品

**接口路径**: `POST /api/product/delete`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "productId": 1            // 商品ID
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "商品删除成功"
}
```

### 2.4 图片上传

#### 2.4.1 上传商家头像

**接口路径**: `POST /api/merchant/image/upload-merchant-avatar`

**请求头**: `Authorization: Bearer <token>`

**请求参数**: `multipart/form-data`
- `file`: 图片文件 (最大5MB)

**响应数据**:
```json
{
  "success": true,
  "data": {
    "message": "头像上传成功",
    "avatar_url": "/uploads/merchant/avatar/2024-01-01/uuid.jpg"
  }
}
```

#### 2.4.2 上传店铺图片

**接口路径**: `POST /api/merchant/image/upload-store-image`

**请求头**: `Authorization: Bearer <token>`

**请求参数**: `multipart/form-data`
- `file`: 图片文件
- `storeId`: 店铺ID

**响应数据**:
```json
{
  "success": true,
  "data": {
    "message": "店铺图片上传成功",
    "image_url": "/uploads/store/image/2024-01-01/uuid.jpg"
  }
}
```

#### 2.4.3 上传商品图片

**接口路径**: `POST /api/merchant/image/upload-product-image`

**请求头**: `Authorization: Bearer <token>`

**请求参数**: `multipart/form-data`
- `file`: 图片文件
- `productId`: 商品ID

**响应数据**:
```json
{
  "success": true,
  "data": {
    "message": "商品图片上传成功",
    "image_url": "/uploads/product/image/2024-01-01/uuid.jpg"
  }
}
```

#### 2.4.4 删除商家头像

**接口路径**: `DELETE /api/merchant/image/delete-merchant-avatar`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "头像删除成功"
}
```

---

## 3. 骑手服务 (Rider Service)

### 3.1 骑手管理

#### 3.1.1 骑手注册

**接口路径**: `POST /api/rider/register`

**请求参数**:
```json
{
  "username": "string",     // 骑手用户名，2-20个字符
  "password": "string",     // 密码，6-20个字符
  "phone": "string"         // 手机号，11位数字
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "rider01",
    "phone": "13800138002"
  }
}
```

#### 3.1.2 骑手登录

**接口路径**: `POST /api/rider/login`

**请求参数**:
```json
{
  "phone": "string",        // 手机号
  "password": "string"      // 密码
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "riderInfo": {
      "id": 1,
      "username": "rider01",
      "phone": "13800138002",
      "dispatchMode": 1
    }
  }
}
```

#### 3.1.3 获取骑手信息

**接口路径**: `GET /api/rider/info`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "rider01",
    "phone": "13800138002",
    "avatar": "http://example.com/avatar.jpg",
    "balance": 1250.50,
    "dispatchMode": 1,
    "status": 1,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

#### 3.1.4 更新骑手信息

**接口路径**: `PUT /api/rider/update`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "username": "string",     // 可选，骑手用户名
  "password": "string",     // 可选，新密码
  "phone": "string"         // 可选，手机号
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "骑手信息更新成功"
}
```

#### 3.1.5 切换接单模式

**接口路径**: `PATCH /api/rider/dispatch-mode`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "dispatchMode": 1          // 接单模式：0-手动，1-自动
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "接单模式切换成功",
  "data": {
    "dispatchMode": 1
  }
}
```

#### 3.1.6 骑手登出

**接口路径**: `POST /api/rider/logout`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "登出成功"
}
```

#### 3.1.7 删除骑手账户

**接口路径**: `DELETE /api/rider/delete`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "账户删除成功"
}
```

#### 3.1.8 自动接单

**接口路径**: `POST /api/rider/auto-order-taking`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "自动接单功能已启用"
}
```

### 3.2 图片上传

#### 3.2.1 上传骑手头像

**接口路径**: `POST /api/rider/image/upload-rider-avatar`

**请求头**: `Authorization: Bearer <token>`

**请求参数**: `multipart/form-data`
- `file`: 图片文件 (最大5MB)

**响应数据**:
```json
{
  "success": true,
  "data": {
    "message": "头像上传成功",
    "avatar_url": "/uploads/rider/avatar/2024-01-01/uuid.jpg"
  }
}
```

#### 3.2.2 删除骑手头像

**接口路径**: `DELETE /api/rider/image/delete-rider-avatar`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "头像删除成功"
}
```

---

## 4. 管理员服务 (Admin Service)

### 4.1 管理员认证

#### 4.1.1 管理员登录

**接口路径**: `POST /api/admin/login`

**请求参数**:
```json
{
  "adminId": 1,             // 管理员ID
  "password": "string"      // 密码
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### 4.1.2 管理员登出

**接口路径**: `POST /api/admin/logout`

**请求头**: `Authorization: Bearer <token>`

**响应数据**:
```json
{
  "success": true,
  "message": "登出成功"
}
```

### 4.2 用户管理

#### 4.2.1 获取用户列表

**接口路径**: `POST /api/admin/userlist`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "page": 1,                // 页码
  "pageSize": 10,           // 每页大小
  "keyword": "string",      // 可选，搜索关键词
  "gender": "string",       // 可选，性别筛选
  "startId": 1,             // 可选，起始ID
  "endId": 100              // 可选，结束ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "testuser",
      "phone": "13800138000",
      "gender": "male",
      "createdAt": "2024-01-01T00:00:00",
      "status": 1
    }
  ]
}
```

### 4.3 骑手管理

#### 4.3.1 获取骑手列表

**接口路径**: `POST /api/admin/riderlist`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "page": 1,                // 页码
  "pageSize": 10,           // 每页大小
  "keyword": "string",      // 可选，搜索关键词
  "startId": 1,             // 可选，起始ID
  "endId": 100,             // 可选，结束ID
  "status": 1,              // 可选，状态筛选
  "dispatchMode": 1,        // 可选，接单模式筛选
  "startBalance": 0,        // 可选，最低余额
  "endBalance": 10000       // 可选，最高余额
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "rider01",
      "phone": "13800138002",
      "balance": 1250.50,
      "dispatchMode": 1,
      "status": 1,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 4.4 商家管理

#### 4.4.1 获取商家列表

**接口路径**: `POST /api/admin/merchantlist`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "page": 1,                // 页码
  "pageSize": 10,           // 每页大小
  "keyword": "string",      // 可选，搜索关键词
  "startId": 1,             // 可选，起始ID
  "endId": 100              // 可选，结束ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "merchant01",
      "phone": "13800138001",
      "createdAt": "2024-01-01T00:00:00",
      "status": 1
    }
  ]
}
```

### 4.5 店铺管理

#### 4.5.1 获取店铺列表

**接口路径**: `POST /api/admin/storelist`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "page": 1,                // 页码
  "pageSize": 10,           // 每页大小
  "keyword": "string",      // 可选，搜索关键词
  "merchantId": 1,          // 可选，商家ID筛选
  "status": 1               // 可选，状态筛选
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "美味餐厅",
      "merchantId": 1,
      "location": "北京市朝阳区",
      "status": 1,
      "rating": 4.5,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 4.6 商品管理

#### 4.6.1 获取商品列表

**接口路径**: `POST /api/admin/productlist`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1,             // 店铺ID
  "page": 1,                // 页码
  "pageSize": 10            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "宫保鸡丁",
      "storeId": 1,
      "price": 28.50,
      "category": "川菜",
      "stock": 100,
      "status": 1,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

### 4.7 订单管理

#### 4.7.1 获取订单列表

**接口路径**: `POST /api/admin/orderlist`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "userId": 1,              // 可选，用户ID筛选
  "storeId": 1,             // 可选，店铺ID筛选
  "riderId": 1,             // 可选，骑手ID筛选
  "status": 1,              // 可选，状态筛选
  "createdAt": "2024-01-01T00:00:00",  // 可选，创建时间起始
  "endedAt": "2024-01-31T23:59:59",    // 可选，创建时间结束
  "page": 1,                // 页码
  "pageSize": 10            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "storeId": 1,
      "riderId": 1,
      "totalPrice": 58.50,
      "status": 3,
      "createdAt": "2024-01-01T12:00:00",
      "deliveredAt": "2024-01-01T12:30:00"
    }
  ]
}
```

### 4.8 评价管理

#### 4.8.1 获取评价列表

**接口路径**: `POST /api/admin/reviewlist`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "userId": 1,              // 可选，用户ID筛选
  "storeId": 1,             // 可选，店铺ID筛选
  "productId": 1,           // 可选，商品ID筛选
  "startTime": "2024-01-01T00:00:00",  // 可选，时间起始
  "endTime": "2024-01-31T23:59:59",    // 可选，时间结束
  "startRating": 1,         // 可选，最低评分
  "endRating": 5,           // 可选，最高评分
  "page": 1,                // 页码
  "pageSize": 10            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "storeId": 1,
      "productId": 1,
      "rating": 5,
      "comment": "非常好吃！",
      "image": "http://example.com/review.jpg",
      "createdAt": "2024-01-01T12:00:00"
    }
  ]
}
```

### 4.9 搜索功能

#### 4.9.1 搜索店铺和商品

**接口路径**: `POST /api/admin/search`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "keyword": "string"       // 搜索关键词
}
```

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "store_id": 1,
      "store_name": "美味餐厅",
      "products": "宫保鸡丁:1,麻婆豆腐:2"
    }
  ]
}
```

#### 4.9.2 根据ID搜索订单

**接口路径**: `POST /api/admin/search-order-by-id`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "orderId": 1              // 订单ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 1,
    "storeId": 1,
    "riderId": 1,
    "totalPrice": 58.50,
    "status": 3,
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

#### 4.9.3 根据ID搜索评价

**接口路径**: `POST /api/admin/search-review-by-id`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "reviewId": 1             // 评价ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 1,
    "storeId": 1,
    "productId": 1,
    "rating": 5,
    "comment": "非常好吃！",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

### 4.10 删除操作

#### 4.10.1 删除数据

**接口路径**: `DELETE /api/admin/delete`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "type": "string",         // 删除类型：user/rider/merchant/store/product
  "username": "string",     // 用户名（用于user/rider/merchant）
  "storeName": "string",    // 店铺名称（用于store）
  "productName": "string",  // 商品名称（用于product）
  "storeNameForProduct": "string"  // 商品所属店铺名称（用于product）
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "删除成功"
}
```

---

## 5. 网关服务 (Gateway Service)

### 5.1 订单管理

#### 5.1.1 创建订单

**接口路径**: `POST /api/order/create`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "storeId": 1,             // 店铺ID
  "items": [                // 订单商品列表
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "deliveryAddress": "string",  // 配送地址
  "couponId": 1             // 可选，优惠券ID
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "orderId": 1,
    "totalPrice": 58.50,
    "discountAmount": 5.00,
    "finalPrice": 53.50,
    "estimatedDeliveryTime": "2024-01-01T13:00:00"
  }
}
```

#### 5.1.2 获取可抢订单列表（骑手）

**接口路径**: `GET /api/order/available?page=1&page_size=10`

**请求头**: `Authorization: Bearer <token>` (骑手角色)

**响应数据**:
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "id": 1,
        "storeId": 1,
        "storeName": "美味餐厅",
        "totalPrice": 58.50,
        "deliveryAddress": "北京市朝阳区",
        "createdAt": "2024-01-01T12:00:00"
      }
    ]
  }
}
```

#### 5.1.3 抢单（骑手）

**接口路径**: `PUT /api/order/grab`

**请求头**: `Authorization: Bearer <token>` (骑手角色)

**请求参数**:
```json
{
  "orderId": 1              // 订单ID
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "抢单成功"
}
```

#### 5.1.4 取消订单（用户）

**接口路径**: `PUT /api/order/cancel`

**请求头**: `Authorization: Bearer <token>` (用户角色)

**请求参数**:
```json
{
  "orderId": 1              // 订单ID
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "订单取消成功"
}
```

#### 5.1.5 更新订单状态（骑手）

**接口路径**: `POST /api/order/rider-update-status`

**请求头**: `Authorization: Bearer <token>` (骑手角色)

**请求参数**:
```json
{
  "orderId": 1,             // 订单ID
  "status": 2               // 新状态：2-配送中，3-已送达
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "订单状态更新成功"
}
```

#### 5.1.6 获取骑手订单历史

**接口路径**: `GET /api/order/rider-history?status=3&page=1&page_size=10`

**请求头**: `Authorization: Bearer <token>` (骑手角色)

**查询参数**:
- `status`: 可选，订单状态筛选
- `page`: 页码
- `page_size`: 每页大小

**响应数据**:
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "id": 1,
        "storeId": 1,
        "storeName": "美味餐厅",
        "totalPrice": 58.50,
        "status": 3,
        "createdAt": "2024-01-01T12:00:00",
        "deliveredAt": "2024-01-01T12:30:00"
      }
    ]
  }
}
```

#### 5.1.7 获取骑手收入统计

**接口路径**: `GET /api/order/rider-earnings`

**请求头**: `Authorization: Bearer <token>` (骑手角色)

**响应数据**:
```json
{
  "success": true,
  "data": {
    "todayEarnings": 125.50,
    "weekEarnings": 850.00,
    "monthEarnings": 3200.00,
    "totalOrders": 45,
    "completedOrders": 42
  }
}
```

#### 5.1.8 商家更新订单状态

**接口路径**: `PUT /api/order/merchant-update`

**请求头**: `Authorization: Bearer <token>` (商家角色)

**请求参数**:
```json
{
  "orderId": 1,             // 订单ID
  "status": 1               // 新状态：1-已接单，4-已拒绝
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "订单状态更新成功"
}
```

#### 5.1.9 获取商家订单列表

**接口路径**: `GET /api/order/merchant-list?store_id=1&status=1&page=1&page_size=10`

**请求头**: `Authorization: Bearer <token>` (商家角色)

**查询参数**:
- `store_id`: 店铺ID
- `status`: 可选，订单状态筛选
- `page`: 页码
- `page_size`: 每页大小

**响应数据**:
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "id": 1,
        "userId": 1,
        "userName": "testuser",
        "totalPrice": 58.50,
        "status": 1,
        "deliveryAddress": "北京市朝阳区",
        "createdAt": "2024-01-01T12:00:00"
      }
    ]
  }
}
```

#### 5.1.10 获取用户订单历史

**接口路径**: `GET /api/order/user-history?status=3&start_time=2024-01-01&end_time=2024-01-31&page=1&page_size=10`

**请求头**: `Authorization: Bearer <token>` (用户角色)

**查询参数**:
- `status`: 可选，订单状态筛选
- `start_time`: 可选，开始时间
- `end_time`: 可选，结束时间
- `page`: 页码
- `page_size`: 每页大小

**响应数据**:
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "id": 1,
        "storeId": 1,
        "storeName": "美味餐厅",
        "totalPrice": 58.50,
        "status": 3,
        "createdAt": "2024-01-01T12:00:00"
      }
    ]
  }
}
```

#### 5.1.11 获取用户当前订单

**接口路径**: `GET /api/order/user-current?page=1&page_size=10`

**请求头**: `Authorization: Bearer <token>` (用户角色)

**查询参数**:
- `page`: 页码
- `page_size`: 每页大小

**响应数据**:
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "id": 1,
        "storeId": 1,
        "storeName": "美味餐厅",
        "totalPrice": 58.50,
        "status": 2,
        "estimatedDeliveryTime": "2024-01-01T13:00:00"
      }
    ]
  }
}
```

#### 5.1.12 获取订单商品详情

**接口路径**: `GET /api/order/{orderId}/items`

**请求头**: `Authorization: Bearer <token>`

**路径参数**:
- `orderId`: 订单ID

**响应数据**:
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "productId": 1,
        "productName": "宫保鸡丁",
        "quantity": 2,
        "price": 28.50,
        "subtotal": 57.00
      }
    ]
  }
}
```

#### 5.1.13 获取订单详情

**接口路径**: `GET /api/order/{orderId}`

**请求头**: `Authorization: Bearer <token>`

**路径参数**:
- `orderId`: 订单ID

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 1,
    "storeId": 1,
    "riderId": 1,
    "totalPrice": 58.50,
    "discountAmount": 5.00,
    "finalPrice": 53.50,
    "status": 3,
    "deliveryAddress": "北京市朝阳区",
    "createdAt": "2024-01-01T12:00:00",
    "deliveredAt": "2024-01-01T12:30:00"
  }
}
```

### 5.2 购物车管理

#### 5.2.1 添加商品到购物车

**接口路径**: `POST /api/cart/add`

**请求头**: `Authorization: Bearer <token>` (用户角色)

**请求参数**:
```json
{
  "productId": 1,           // 商品ID
  "quantity": 2             // 数量
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "商品已添加到购物车"
}
```

#### 5.2.2 查看购物车

**接口路径**: `GET /api/cart/view`

**请求头**: `Authorization: Bearer <token>` (用户角色)

**响应数据**:
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "productId": 1,
        "productName": "宫保鸡丁",
        "quantity": 2,
        "price": 28.50,
        "imageUrl": "http://example.com/product.jpg",
        "storeId": 1,
        "storeName": "美味餐厅"
      }
    ],
    "totalAmount": 57.00,
    "totalItems": 2
  }
}
```

#### 5.2.3 更新购物车商品数量

**接口路径**: `PUT /api/cart/update`

**请求头**: `Authorization: Bearer <token>` (用户角色)

**请求参数**:
```json
{
  "productId": 1,           // 商品ID
  "quantity": 3             // 新数量
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "购物车已更新"
}
```

#### 5.2.4 删除购物车商品

**接口路径**: `PUT /api/cart/delete`

**请求头**: `Authorization: Bearer <token>` (用户角色)

**请求参数**:
```json
{
  "productId": 1            // 商品ID
}
```

**响应数据**:
```json
{
  "success": true,
  "message": "商品已从购物车移除"
}
```

#### 5.2.5 清空购物车

**接口路径**: `DELETE /api/cart/remove`

**请求头**: `Authorization: Bearer <token>` (用户角色)

**响应数据**:
```json
{
  "success": true,
  "message": "购物车已清空"
}
```

### 5.3 商品详情

#### 5.3.1 获取商品详情

**接口路径**: `GET /api/products/{productId}`

**路径参数**:
- `productId`: 商品ID

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "宫保鸡丁",
    "description": "经典川菜",
    "price": 28.50,
    "category": "川菜",
    "stock": 100,
    "image": "http://example.com/product.jpg",
    "rating": 4.8,
    "storeId": 1,
    "storeName": "美味餐厅",
    "storeLocation": "北京市朝阳区"
  }
}
```

#### 5.3.2 根据请求体获取商品详情

**接口路径**: `POST /api/products/info`

**请求参数**:
```json
{
  "id": 1                   // 商品ID
}
```

**响应数据**: 同上

#### 5.3.3 获取商品评价列表

**接口路径**: `GET /api/products/{productId}/reviews?page=1&page_size=10`

**路径参数**:
- `productId`: 商品ID

**查询参数**:
- `page`: 页码，默认1
- `page_size`: 每页大小，默认10

**响应数据**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "username": "testuser",
      "userAvatar": "http://example.com/avatar.jpg",
      "rating": 5,
      "comment": "非常好吃！",
      "images": "http://example.com/review.jpg",
      "createdAt": "2024-01-01T12:00:00"
    }
  ]
}
```

#### 5.3.4 获取店铺商品列表

**接口路径**: `GET /api/products/store/{storeId}?category=川菜&page=1&page_size=10`

**路径参数**:
- `storeId`: 店铺ID

**查询参数**:
- `category`: 可选，商品分类筛选
- `page`: 页码，默认1
- `page_size`: 每页大小，默认10

**响应数据**:
```json
{
  "success": true,
  "data": {
    "products": [
      {
        "id": 1,
        "name": "宫保鸡丁",
        "price": 28.50,
        "category": "川菜",
        "image": "http://example.com/product.jpg",
        "rating": 4.8,
        "stock": 100
      }
    ],
    "total": 25
  }
}
```

### 5.4 消息聊天

#### 5.4.1 获取聊天记录

**接口路径**: `POST /api/messages/history`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "receiverId": 1,          // 接收方ID
  "receiverRole": "merchant", // 接收方角色：user/merchant/rider/admin
  "page": 1,                // 页码
  "pageSize": 20            // 每页大小
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "messages": [
      {
        "id": 1,
        "senderId": 1,
        "senderRole": "user",
        "receiverId": 2,
        "receiverRole": "merchant",
        "content": "您好，请问这个商品还有库存吗？",
        "messageType": "text",
        "isRead": true,
        "createdAt": "2024-01-01T12:00:00"
      }
    ],
    "currentPage": 1,
    "pageSize": 20,
    "totalCount": 1
  }
}
```

#### 5.4.2 发送消息

**接口路径**: `POST /api/messages/send`

**请求头**: `Authorization: Bearer <token>`

**请求参数**:
```json
{
  "receiverId": 2,          // 接收方ID
  "receiverRole": "merchant", // 接收方角色
  "content": "您好，请问这个商品还有库存吗？"  // 消息内容
}
```

**响应数据**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "senderId": 1,
    "senderRole": "user",
    "receiverId": 2,
    "receiverRole": "merchant",
    "content": "您好，请问这个商品还有库存吗？",
    "messageType": "text",
    "isRead": false,
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

#### 5.4.3 获取会话列表

**接口路径**: `GET /api/messages/conversations?page=1&page_size=10`

**请求头**: `Authorization: Bearer <token>`

**查询参数**:
- `page`: 页码，默认1
- `page_size`: 每页大小，默认10

**响应数据**:
```json
{
  "success": true,
  "data": {
    "conversations": [
      {
        "contactId": 2,
        "contactRole": "merchant",
        "lastMessageTime": "2024-01-01T12:00:00",
        "unreadCount": 3
      }
    ]
  }
}
```

#### 5.4.4 标记消息为已读

**接口路径**: `PUT /api/messages/conversations/{conversationId}/read`

**请求头**: `Authorization: Bearer <token>`

**路径参数**:
- `conversationId`: 会话ID（对方用户ID）

**响应数据**:
```json
{
  "success": true,
  "message": "消息已标记为已读"
}
```

---

## 6. 状态码说明

### 6.1 订单状态

| 状态码 | 状态名称 | 描述 |
|-------|---------|------|
| 0 | 待接单 | 订单已创建，等待商家接单 |
| 1 | 已接单 | 商家已接单，准备中 |
| 2 | 配送中 | 骑手已取餐，正在配送 |
| 3 | 已送达 | 订单已完成 |
| 4 | 已取消 | 订单已取消 |

### 6.2 用户角色

| 角色 | 描述 |
|------|------|
| user | 普通用户 |
| merchant | 商家 |
| rider | 骑手 |
| admin | 管理员 |

### 6.3 店铺/商品状态

| 状态码 | 描述 |
|-------|------|
| 0 | 关闭/下架 |
| 1 | 营业/上架 |

### 6.4 骑手接单模式

| 模式码 | 描述 |
|-------|------|
| 0 | 手动接单 |
| 1 | 自动接单 |

---

## 7. 错误码说明

### 7.1 通用错误码

| 错误码 | 错误信息 | 描述 |
|-------|---------|------|
| 400 | 请求参数错误 | 请求参数格式不正确或缺少必要参数 |
| 401 | 未授权 | 用户未登录或token无效 |
| 403 | 权限不足 | 用户无权限访问该资源 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器内部错误 | 服务器处理请求时发生错误 |

### 7.2 业务错误码

| 错误码 | 错误信息 | 描述 |
|-------|---------|------|
| 1001 | 用户名或手机号已存在 | 注册时用户名或手机号重复 |
| 1002 | 手机号或密码错误 | 登录时凭据不正确 |
| 1003 | 该用户已登录 | 重复登录 |
| 2001 | 商品不存在或已下架 | 商品ID无效或商品已下架 |
| 2002 | 库存不足 | 商品库存不够 |
| 3001 | 订单不存在 | 订单ID无效 |
| 3002 | 订单状态不允许此操作 | 订单当前状态不支持该操作 |
| 4001 | 文件格式不支持 | 上传的文件格式不正确 |
| 4002 | 文件大小超限 | 上传的文件过大 |

---

## 8. 开发注意事项

### 8.1 认证机制

1. 所有需要认证的接口都需要在请求头中携带JWT Token
2. Token格式：`Authorization: Bearer <token>`
3. Token有效期为24小时
4. 同一用户同时只能有一个有效登录会话

### 8.2 分页参数

1. 分页参数统一使用`page`（页码，从1开始）和`pageSize`（每页大小）
2. 默认页码为1，默认每页大小为10
3. 最大每页大小限制为100

### 8.3 时间格式

1. 所有时间字段统一使用ISO 8601格式：`YYYY-MM-DDTHH:mm:ss`
2. 时区统一使用东八区（Asia/Shanghai）

### 8.4 文件上传

1. 支持的图片格式：jpg、png、gif
2. 单个文件最大大小：5MB
3. 上传成功后返回相对路径，需要拼接域名使用

### 8.5 数据校验

1. 所有输入参数都会进行服务端校验
2. 手机号格式：11位数字
3. 密码长度：6-20个字符
4. 用户名长度：2-20个字符

---

## 9. 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 完成用户服务、商家服务、骑手服务、管理员服务和网关服务的所有API接口
- 支持用户注册登录、店铺管理、商品管理、订单管理、购物车、消息聊天等功能

---

**文档最后更新时间**: 2024-01-01

**技术支持**: Baoleme开发团队