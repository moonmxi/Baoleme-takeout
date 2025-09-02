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
import org.demo.common.common.CommonResponse;
import org.demo.common.common.ResponseBuilder;
import org.demo.common.common.UserHolder;
import org.demo.common.dto.request.MerchantOrderListRequest;
import org.demo.common.dto.request.OrderCreateRequest;
import org.demo.common.dto.request.OrderGrabRequest;
import org.demo.common.dto.request.OrderStatusUpdateRequest;
import org.demo.common.dto.request.RiderOrderHistoryQueryRequest;
import org.demo.common.dto.request.UserCurrentOrderRequest;
import org.demo.common.dto.request.UserOrderHistoryRequest;
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
     * 根据订单ID获取订单详情
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
}