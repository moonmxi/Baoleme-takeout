/**
 * 订单控制器
 * 处理订单相关的HTTP请求，包括创建订单、抢单、状态更新等功能
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.common.controller;

import jakarta.validation.Valid;
import org.demo.common.client.GatewayApiClient;
import org.demo.common.common.CommonResponse;
import org.demo.common.common.ResponseBuilder;
import org.demo.common.common.UserHolder;
import org.demo.common.dto.request.MerchantOrderListRequest;
import org.demo.common.dto.request.OrderCreateRequest;
import org.demo.common.dto.request.OrderDetailRequest;
import org.demo.common.dto.request.OrderGrabRequest;
import org.demo.common.dto.request.OrderStatusUpdateRequest;
import org.demo.common.dto.request.RiderOrderHistoryQueryRequest;
import org.demo.common.dto.request.UserCurrentOrderRequest;
import org.demo.common.dto.request.UserOrderHistoryRequest;
import org.demo.common.dto.response.OrderDetailResponse;
import org.demo.common.dto.response.OrderResponse;
import org.demo.common.pojo.Order;
import org.demo.common.pojo.OrderItem;
import org.demo.common.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器类
 * 提供订单管理的REST API接口
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private GatewayApiClient gatewayApiClient;

    /**
     * 创建订单
     * 
     * @param request 订单创建请求
     * @return CommonResponse<OrderResponse> 创建结果
     */
    @PostMapping("/create")
    public CommonResponse createOrder(@Valid @RequestBody OrderCreateRequest request) {
        try {
            Long userId = UserHolder.getId();
            if (userId == null) {
                return ResponseBuilder.fail("用户未登录");
            }

            OrderResponse response = orderService.createOrder(userId, request);
            return ResponseBuilder.ok("订单创建成功", response);
        } catch (Exception e) {
            return ResponseBuilder.fail("订单创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取可抢订单列表（骑手使用）
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @return CommonResponse<List<Order>> 可抢订单列表
     */
    @GetMapping("/available")
    public CommonResponse getAvailableOrders(@RequestParam("page") int page,
                                            @RequestParam("page_size") int pageSize) {
        String role = UserHolder.getRole();
        if (!"rider".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }

        List<Order> orders = orderService.getAvailableOrders(page, pageSize);
        return ResponseBuilder.ok(Map.of("orders", orders));
    }

    /**
     * 抢单（骑手使用）
     * 
     * @param request 抢单请求
     * @return CommonResponse 抢单结果
     */
    @PutMapping("/grab")
    public CommonResponse grabOrder(@Valid @RequestBody OrderGrabRequest request) {
        String role = UserHolder.getRole();
        if (!"rider".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }

        Long riderId = UserHolder.getId();
        boolean success = orderService.grabOrder(request.getOrderId(), riderId);
        
        if (!success) {
            return ResponseBuilder.fail("订单已被抢或不存在");
        }

        return ResponseBuilder.ok(Map.of(
                "order_id", request.getOrderId(),
                "pickup_deadline", LocalDateTime.now().plusMinutes(30)
        ));
    }

    /**
     * 取消订单（骑手使用）
     * 
     * @param request 状态更新请求
     * @return CommonResponse 取消结果
     */
    @PutMapping("/cancel")
    public CommonResponse cancelOrder(@Valid @RequestBody OrderStatusUpdateRequest request) {
        String role = UserHolder.getRole();
        if (!"rider".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }

        Long riderId = UserHolder.getId();
        boolean success = orderService.riderCancelOrder(request.getOrderId(), riderId);
        
        if (!success) {
            return ResponseBuilder.fail("当前状态不可取消或订单不存在");
        }

        return ResponseBuilder.ok(Map.of(
                "order_id", request.getOrderId(),
                "status", "CANCELLED"
        ));
    }

    /**
     * 更新订单状态（骑手使用）
     * 
     * @param request 状态更新请求
     * @return CommonResponse 更新结果
     */
    @PostMapping("/rider-update-status")
    public CommonResponse updateOrderStatus(@Valid @RequestBody OrderStatusUpdateRequest request) {
        String role = UserHolder.getRole();
        if (!"rider".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }

        Long riderId = UserHolder.getId();
        boolean success = orderService.riderUpdateOrderStatus(
                request.getOrderId(), riderId, request.getTargetStatus());
        
        if (!success) {
            return ResponseBuilder.fail("订单状态更新失败");
        }

        return ResponseBuilder.ok(Map.of(
                "order_id", request.getOrderId(),
                "status", request.getTargetStatus(),
                "updated_at", LocalDateTime.now()
        ));
    }

    /**
     * 查询骑手订单记录（GET方式）
     * 
     * @param status 订单状态（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return CommonResponse<List<Order>> 骑手订单列表
     */
    @GetMapping("/rider-history")
    public CommonResponse getRiderOrders(@RequestParam(value = "status", required = false) Integer status,
                                         @RequestParam("page") int page,
                                         @RequestParam("page_size") int pageSize) {
        String role = UserHolder.getRole();
        if (!"rider".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }

        Long riderId = UserHolder.getId();
        List<Order> orders = orderService.getRiderOrders(riderId, status, page, pageSize);
        return ResponseBuilder.ok(Map.of("orders", orders));
    }

    /**
     * 查询骑手订单记录（POST方式，支持复杂查询条件）
     * 
     * @param request 查询请求参数
     * @return CommonResponse<List<Order>> 骑手订单列表
     */
    @PostMapping("/rider-history-query")
    public CommonResponse getRiderOrdersWithQuery(@Valid @RequestBody RiderOrderHistoryQueryRequest request) {
        String role = UserHolder.getRole();
        if (!"rider".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }

        Long riderId = UserHolder.getId();
        List<Order> orders = orderService.getRiderOrdersWithFilter(
                riderId, 
                request.getStatus(), 
                request.getStartTime(), 
                request.getEndTime(), 
                request.getPage(), 
                request.getPageSize()
        );
        return ResponseBuilder.ok(Map.of("orders", orders));
    }

    /**
     * 查询骑手收入统计
     * 
     * @return CommonResponse 收入统计信息
     */
    @GetMapping("/rider-earnings")
    public CommonResponse getRiderEarnings() {
        String role = UserHolder.getRole();
        if (!"rider".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }

        Long riderId = UserHolder.getId();
        Map<String, Object> earnings = orderService.getRiderEarnings(riderId);
        return ResponseBuilder.ok(earnings);
    }

    /**
     * 商家更新订单状态
     * 
     * @param request 状态更新请求
     * @return CommonResponse 更新结果
     */
    @PutMapping("/merchant-update")
    public CommonResponse updateOrderByMerchant(@Valid @RequestBody OrderStatusUpdateRequest request) {
        String role = UserHolder.getRole();
        if (!"merchant".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }

        if (request.getTargetStatus() == null) {
            return ResponseBuilder.fail("订单更新失败：商家未设置新状态");
        }
        
        if (request.getTargetStatus() == 4 && request.getCancelReason() == null) {
            return ResponseBuilder.fail("订单更新失败：商家未设置取消原因");
        }

        Long merchantId = UserHolder.getId();
        boolean success = orderService.updateOrderByMerchant(
                merchantId, request.getOrderId(), request.getTargetStatus());
        
        if (!success) {
            return ResponseBuilder.fail("订单更新失败：权限不足或订单不存在");
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("order_id", request.getOrderId());
        responseData.put("new_status", request.getTargetStatus());
        responseData.put("update_at", LocalDateTime.now());
        if (request.getCancelReason() != null) {
            responseData.put("cancel_reason", request.getCancelReason());
        }
        
        return ResponseBuilder.ok(responseData);
    }

    /**
     * 商家分页查看订单
     * 
     * @param storeId 店铺ID
     * @param status 订单状态（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return CommonResponse<List<Order>> 店铺订单列表
     */
    @GetMapping("/merchant-list")
    public CommonResponse getOrdersByMerchant(@RequestParam("store_id") Long storeId,
                                             @RequestParam(value = "status", required = false) Integer status,
                                             @RequestParam("page") int page,
                                             @RequestParam("page_size") int pageSize) {
        String role = UserHolder.getRole();
        if (!"merchant".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }

        if (storeId == null) {
            return ResponseBuilder.fail("订单查看失败：未提供store_id字段");
        }

        List<Order> orders;
        if (status == null) {
            orders = orderService.getOrdersByMerchant(storeId, page, pageSize);
        } else {
            orders = orderService.getOrdersByMerchantAndStatus(storeId, status, page, pageSize);
        }

        return ResponseBuilder.ok(Map.of(
                "orders", orders,
                "current_page", page,
                "page_size", pageSize
        ));
    }

    /**
     * 商家查看店铺订单列表（POST方式，支持JSON请求体）
     * 
     * @param request 订单查询请求
     * @return CommonResponse<List<Order>> 店铺订单列表
     */
    @PostMapping("/merchant-list")
    public CommonResponse getOrdersByMerchantPost(@Valid @RequestBody MerchantOrderListRequest request) {
        String role = UserHolder.getRole();
        if (!"merchant".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }

        List<Order> orders;
        if (request.getStatus() == null) {
            orders = orderService.getOrdersByMerchant(request.getStoreId(), request.getPage(), request.getPageSize());
        } else {
            orders = orderService.getOrdersByMerchantAndStatus(request.getStoreId(), request.getStatus(), request.getPage(), request.getPageSize());
        }

        return ResponseBuilder.ok(Map.of(
                "orders", orders,
                "current_page", request.getPage(),
                "page_size", request.getPageSize()
        ));
    }

    /**
     * 用户查看订单历史（支持条件筛选）
     * 
     * @param request 用户订单历史查询请求
     * @return CommonResponse<List<Order>> 用户订单列表
     */
    @PostMapping("/user-history")
    public CommonResponse getUserOrders(@Valid @RequestBody UserOrderHistoryRequest request) {
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

        Long userId = UserHolder.getId();
        List<Order> orders = orderService.getUserOrdersWithFilter(userId, request.getStatus(), request.getStartTime(), request.getEndTime(), request.getPage(), request.getPageSize());
        return ResponseBuilder.ok(Map.of("orders", orders));
    }

    /**
     * 用户查看当前订单（进行中的订单）
     * 
     * @param request 用户当前订单查询请求
     * @return CommonResponse 当前订单列表
     */
    @PostMapping("/user-current")
    public CommonResponse getUserCurrentOrders(@Valid @RequestBody UserCurrentOrderRequest request) {
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

        Long userId = UserHolder.getId();
        // 查询状态为0,1,2的订单（待接单、准备中、配送中）
        List<Order> orders = orderService.getUserCurrentOrders(userId, request.getPage(), request.getPageSize());
        return ResponseBuilder.ok(Map.of("orders", orders));
    }

    /**
     * 获取订单商品明细
     * 
     * @param orderId 订单ID
     * @return CommonResponse 订单商品明细列表
     */
    @GetMapping("/{orderId}/items")
    public CommonResponse getOrderItems(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseBuilder.fail("订单不存在");
        }

        // 权限检查：只有订单相关的用户、商家、骑手或管理员可以查看
        Long userId = UserHolder.getId();
        String role = UserHolder.getRole();
        
        if (!"admin".equals(role)) {
            boolean hasPermission = false;
            if ("user".equals(role) && order.getUserId().equals(userId)) {
                hasPermission = true;
            } else if ("rider".equals(role) && order.getRiderId() != null && order.getRiderId().equals(userId)) {
                hasPermission = true;
            } else if ("merchant".equals(role)) {
                // 这里需要验证商家是否拥有该订单的店铺
                hasPermission = true; // 简化实现
            }
            
            if (!hasPermission) {
                return ResponseBuilder.fail("无权限查看该订单");
            }
        }

        List<OrderItem> orderItems = orderService.getOrderItems(orderId);
        Map<String, Object> priceInfo = orderService.getOrderPriceInfo(orderId);
        
        return ResponseBuilder.ok(Map.of(
                "order_items", orderItems,
                "price_info", priceInfo
        ));
    }

    /**
     * 根据订单ID获取订单详情（保留原有GET接口）
     * 
     * @param orderId 订单ID
     * @return CommonResponse 订单详情
     */
    @GetMapping("/{orderId}")
    public CommonResponse getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseBuilder.fail("订单不存在");
        }

        // 权限检查：只有订单相关的用户、商家、骑手或管理员可以查看
        Long userId = UserHolder.getId();
        String role = UserHolder.getRole();
        
        if (!"admin".equals(role)) {
            boolean hasPermission = false;
            if ("user".equals(role) && order.getUserId().equals(userId)) {
                hasPermission = true;
            } else if ("rider".equals(role) && order.getRiderId() != null && order.getRiderId().equals(userId)) {
                hasPermission = true;
            } else if ("merchant".equals(role)) {
                // 这里需要验证商家是否拥有该订单的店铺
                hasPermission = true; // 简化实现
            }
            
            if (!hasPermission) {
                return ResponseBuilder.fail("无权限查看该订单");
            }
        }

        return ResponseBuilder.ok(order);
    }
    
    /**
     * 获取订单详情（POST方式，支持请求体参数）
     * 通过网关服务获取完整的订单详情信息，包括用户、商家、店铺、商品等相关信息
     * 
     * @param request 订单详情查询请求
     * @return CommonResponse<OrderDetailResponse> 完整的订单详情
     */
    @PostMapping("/detail")
    public CommonResponse getOrderDetail(@Valid @RequestBody OrderDetailRequest request) {
        try {
            // 参数验证
            if (request == null || request.getOrderId() == null) {
                return ResponseBuilder.fail("请求参数不能为空");
            }
            
            // 获取基本订单信息
            Order order = orderService.getOrderById(request.getOrderId());
            if (order == null) {
                return ResponseBuilder.fail("订单不存在");
            }

            // 权限检查：只有订单相关的用户、商家、骑手或管理员可以查看
            Long currentUserId = UserHolder.getId();
            String role = UserHolder.getRole();
            String token = UserHolder.getToken();
            
            // 检查用户身份信息
            if (currentUserId == null || role == null) {
                return ResponseBuilder.fail("用户身份验证失败");
            }
            
            if (!"admin".equals(role)) {
                boolean hasPermission = false;
                if ("user".equals(role) && order.getUserId() != null && order.getUserId().equals(currentUserId)) {
                    hasPermission = true;
                } else if ("rider".equals(role) && order.getRiderId() != null && order.getRiderId().equals(currentUserId)) {
                    hasPermission = true;
                } else if ("merchant".equals(role)) {
                    // 通过网关API验证商家是否拥有该订单的店铺
                    try {
                        if (order.getStoreId() != null && token != null) {
                            Map<String, Object> storeInfo = gatewayApiClient.getStoreById(order.getStoreId(), token);
                            if (storeInfo != null && !storeInfo.isEmpty()) {
                                Long merchantId = getLongValue(storeInfo.get("merchant_id"));
                                hasPermission = merchantId != null && currentUserId.equals(merchantId);
                            }
                        }
                    } catch (Exception e) {
                        // 网关API调用失败时，记录日志但不影响权限验证
                        System.err.println("验证商家权限时网关API调用失败: " + e.getMessage());
                        // 对于商家角色，如果网关API调用失败，拒绝访问以确保安全
                        hasPermission = false;
                    }
                }
                
                if (!hasPermission) {
                    return ResponseBuilder.fail("无权限查看该订单");
                }
            }

            // 构建详细响应
            OrderDetailResponse response = buildOrderDetailResponse(order, request, token);
            
            return ResponseBuilder.ok("订单详情获取成功", response);
            
        } catch (Exception e) {
            System.err.println("获取订单详情时发生异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseBuilder.fail("获取订单详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建订单详情响应
     * 
     * @param order 订单基本信息
     * @param request 请求参数
     * @param token JWT令牌
     * @return OrderDetailResponse 完整的订单详情响应
     */
    private OrderDetailResponse buildOrderDetailResponse(Order order, OrderDetailRequest request, String token) {
        OrderDetailResponse response = new OrderDetailResponse();
        
        try {
            // 设置订单基本信息
            response.setOrderId(order.getId());
            response.setStatus(order.getStatus());
            response.setUserLocation(order.getUserLocation());
            response.setStoreLocation(order.getStoreLocation());
            response.setTotalPrice(order.getTotalPrice());
            response.setActualPrice(order.getActualPrice());
            response.setDeliveryPrice(order.getDeliveryPrice());
            response.setRemark(order.getRemark());
            response.setCreatedAt(order.getCreatedAt());
            response.setDeadline(order.getDeadline());
            response.setEndedAt(order.getEndedAt());
            

            if (token != null) {
                // 获取用户信息
                if (order.getUserId() != null) {
                    try {
                        Map<String, Object> userInfo = gatewayApiClient.getUserById(order.getUserId(), token);
                        if (userInfo != null && !userInfo.isEmpty()) {
                            response.setUserId(order.getUserId());
                            response.setUsername((String) userInfo.get("username"));
                            response.setUserPhone((String) userInfo.get("phone"));
                            response.setUserAvatar((String) userInfo.get("avatar"));
                        } else {
                            // 设置基本用户信息
                            response.setUserId(order.getUserId());
                        }
                    } catch (Exception e) {
                        System.err.println("获取用户信息失败: userId=" + order.getUserId() + ", error=" + e.getMessage());
                        // 设置基本用户信息
                        response.setUserId(order.getUserId());
                    }
                }
                
                // 获取店铺信息
                if (order.getStoreId() != null) {
                    try {
                        Map<String, Object> storeInfo = gatewayApiClient.getStoreById(order.getStoreId(), token);
                        if (storeInfo != null && !storeInfo.isEmpty()) {
                            response.setStoreId(order.getStoreId());
                            response.setStoreName((String) storeInfo.get("name"));
                            response.setStoreDescription((String) storeInfo.get("description"));
                            response.setStoreLocation((String) storeInfo.get("location"));
                            response.setStoreType((String) storeInfo.get("type"));
                            response.setStoreRating(getBigDecimalValue(storeInfo.get("rating")));
                            response.setStoreImage((String) storeInfo.get("image"));
                            
                            // 获取商家信息
                            Long merchantId = getLongValue(storeInfo.get("merchant_id"));
                            if (merchantId != null) {
                                try {
                                    Map<String, Object> merchantInfo = gatewayApiClient.getMerchantById(merchantId, token);
                                    if (merchantInfo != null && !merchantInfo.isEmpty()) {
                                        response.setMerchantId(merchantId);
                                        response.setMerchantName((String) merchantInfo.get("username"));
                                        response.setMerchantPhone((String) merchantInfo.get("phone"));
                                        response.setMerchantAvatar((String) merchantInfo.get("avatar"));
                                    } else {
                                        response.setMerchantId(merchantId);
                                    }
                                } catch (Exception e) {
                                    System.err.println("获取商家信息失败: merchantId=" + merchantId + ", error=" + e.getMessage());
                                    response.setMerchantId(merchantId);
                                }
                            }
                        } else {
                            // 设置基本店铺信息
                            response.setStoreId(order.getStoreId());
                        }
                    } catch (Exception e) {
                        System.err.println("获取店铺信息失败: storeId=" + order.getStoreId() + ", error=" + e.getMessage());
                        // 设置基本店铺信息
                        response.setStoreId(order.getStoreId());
                    }
                }
                
                // 获取骑手信息
                if (order.getRiderId() != null) {
                    try {
                        Map<String, Object> riderInfo = gatewayApiClient.getRiderById(order.getRiderId(), token);
                        if (riderInfo != null && !riderInfo.isEmpty()) {
                            response.setRiderId(order.getRiderId());
                            response.setRiderName((String) riderInfo.get("username"));
                            response.setRiderPhone((String) riderInfo.get("phone"));
                            response.setRiderAvatar((String) riderInfo.get("avatar"));
                        } else {
                            response.setRiderId(order.getRiderId());
                        }
                    } catch (Exception e) {
                        System.err.println("获取骑手信息失败: riderId=" + order.getRiderId() + ", error=" + e.getMessage());
                        response.setRiderId(order.getRiderId());
                    }
                }
            }
            
            // 获取订单项详情
            try {
                List<OrderItem> orderItems = orderService.getOrderItems(order.getId());
                if (orderItems != null && !orderItems.isEmpty()) {
                    List<OrderDetailResponse.OrderItemDetail> itemDetails = orderItems.stream()
                        .map(item -> {
                            OrderDetailResponse.OrderItemDetail detail = new OrderDetailResponse.OrderItemDetail();

                            try {
                                detail.setProductId(item.getProductId());
                                detail.setQuantity(item.getQuantity());
                                detail.setPrice(item.getPrice());

                                // 安全计算总价
                                if (item.getPrice() != null && item.getQuantity() != null) {
                                    detail.setTotalPrice(item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
                                }

                                // 获取商品详细信息
                                if (item.getProductId() != null && token != null) {
                                    try {
                                        Map<String, Object> productInfo = gatewayApiClient.getProductById(item.getProductId(), token);
                                        if (productInfo != null && !productInfo.isEmpty()) {
                                            detail.setProductName((String) productInfo.get("name"));
                                            detail.setProductDescription((String) productInfo.get("description"));
                                            detail.setProductImage((String) productInfo.get("image"));
                                            detail.setProductCategory((String) productInfo.get("category"));
                                        }
                                    } catch (Exception e) {
                                        System.err.println("获取商品信息失败: productId=" + item.getProductId() + ", error=" + e.getMessage());
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("处理订单项失败: " + e.getMessage());
                            }

                            return detail;
                        })
                        .collect(java.util.stream.Collectors.toList());

                    response.setOrderItems(itemDetails);
                }

                // 获取价格信息
                try {
                    Map<String, Object> priceInfo = orderService.getOrderPriceInfo(order.getId());
                    response.setPriceInfo(priceInfo);
                } catch (Exception e) {
                    System.err.println("获取价格信息失败: orderId=" + order.getId() + ", error=" + e.getMessage());
                }
            } catch (Exception e) {
                System.err.println("获取订单项详情失败: orderId=" + order.getId() + ", error=" + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("构建订单详情响应时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    /**
     * 安全地获取Long值
     * 
     * @param value 原始值
     * @return Long值或null
     */
    private Long getLongValue(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 安全地获取BigDecimal值
     * 
     * @param value 原始值
     * @return BigDecimal值或null
     */
    private java.math.BigDecimal getBigDecimalValue(Object value) {
        if (value == null) return null;
        if (value instanceof java.math.BigDecimal) return (java.math.BigDecimal) value;
        if (value instanceof Double) return java.math.BigDecimal.valueOf((Double) value);
        if (value instanceof Float) return java.math.BigDecimal.valueOf((Float) value);
        if (value instanceof String) {
            try {
                return new java.math.BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}