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

**