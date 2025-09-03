package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.order.*;
import org.demo.baoleme.dto.request.rider.RiderOrderHistoryQueryRequest;
import org.demo.baoleme.dto.response.order.*;
import org.demo.baoleme.dto.response.rider.RiderEarningsResponse;
import org.demo.baoleme.dto.response.rider.RiderOrderHistoryResponse;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.service.OrderService;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.service.RiderService;
import org.demo.baoleme.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final StoreService storeService;

    @Autowired
    private RiderService riderService;

    public OrderController(OrderService orderService, StoreService storeService) {
        this.storeService = storeService;
        this.orderService = orderService;
    }

    /**
     * 获取可抢订单列表
     */
    @GetMapping("/available")
    public CommonResponse getAvailableOrders(@RequestParam("page") int page,
                                             @RequestParam("page_size") int pageSize) {
        // 权限验证：仅骑手可操作
        if (!"rider".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }
        
        try {
            List<Order> orders = orderService.getAvailableOrders(page, pageSize);
            
            // 转换为OrderListResponse格式
            List<OrderListResponse.OrderBrief> orderBriefs = orders.stream().map(order -> {
                OrderListResponse.OrderBrief brief = new OrderListResponse.OrderBrief();
                brief.setOrderId(order.getId());
                // 通过storeService获取店铺名称
                String shopName = "店铺" + order.getStoreId(); // 临时方案，实际应该查询店铺表
                brief.setShopName(shopName);
                brief.setShopLocation(order.getStoreLocation());
                brief.setDeliveryAddress(order.getUserLocation());
                brief.setTotalAmount(order.getTotalPrice());
                brief.setEstimatedTime(30); // 默认30分钟预估时间
                return brief;
            }).toList();
            
            OrderListResponse response = new OrderListResponse();
            response.setOrders(orderBriefs);
            
            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            return ResponseBuilder.fail("获取可用订单失败：" + e.getMessage());
        }
    }

    /**
     * 抢单
     */
    @PutMapping("/grab")
    public CommonResponse grabOrder(@Valid @RequestBody OrderGrabRequest request) {
        // 权限验证：仅骑手可操作
        if (!"rider".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }
        
        Long riderId = UserHolder.getId();
        boolean ok = orderService.grabOrder(request.getOrderId(), riderId);
        if (!ok) {
            return ResponseBuilder.fail("订单已被抢或不存在");
        }
        riderService.updateRiderOrderStatusAfterOrderGrab(riderId);
        OrderGrabResponse response = new OrderGrabResponse();
        response.setOrderId(request.getOrderId());
        response.setPickupDeadline(LocalDateTime.now().plusMinutes(30)); // 假设30分钟取货

        return ResponseBuilder.ok(response);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel")
    public CommonResponse cancelOrder(@Valid @RequestBody OrderCancelRequest request) {
        // 权限验证：仅骑手可操作
        if (!"rider".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }
        
        Long riderId = UserHolder.getId();
        boolean ok = orderService.riderCancelOrder(request.getOrderId(), riderId);
        if (!ok) {
            return ResponseBuilder.fail("当前状态不可取消或订单不存在");
        }
        return ResponseBuilder.ok(Map.of(
                "order_id", request.getOrderId(),
                "status", "CANCELLED"
        ));
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/rider-update-status")
    public CommonResponse updateOrderStatus(@Valid @RequestBody OrderStatusRiderUpdateRequest request) {
        // 权限验证：仅骑手可操作
        if (!"rider".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }
        
        Long riderId = UserHolder.getId();
        boolean ok = orderService.riderUpdateOrderStatus(request.getOrderId(), riderId, request.getTargetStatus());
        if (!ok) {
            return ResponseBuilder.fail("订单状态更新失败");
        }

        OrderStatusRiderUpdateResponse response = new OrderStatusRiderUpdateResponse();
        response.setOrderId(request.getOrderId());
        response.setStatus(request.getTargetStatus());
        response.setUpdatedAt(LocalDateTime.now());

        return ResponseBuilder.ok(response);
    }

    /**
     * 查询骑手订单记录
     */
    @PostMapping("/rider-history-query")
    public CommonResponse getRiderOrders(@Valid @RequestBody RiderOrderHistoryQueryRequest request) {
        // 权限验证：仅骑手可操作
        if (!"rider".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }
        
        try {
            Long riderId = UserHolder.getId();
            List<Order> orders = orderService.getRiderOrders(
                    riderId,
                    request.getStatus(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getPage(),
                    request.getPageSize()
            );

            List<RiderOrderHistoryResponse> responses = orders.stream().map(order -> {
                RiderOrderHistoryResponse resp = new RiderOrderHistoryResponse();
                resp.setOrderId(order.getId());
                resp.setUserId(order.getUserId());
                resp.setStatus(order.getStatus());
                resp.setTotalPrice(order.getTotalPrice());
                resp.setDeliveryPrice(order.getDeliveryPrice());
                resp.setCreatedAt(order.getCreatedAt());
                resp.setStoreLocation(order.getStoreLocation());
                resp.setUserLocation(order.getUserLocation());
                return resp;
            }).toList();

            return ResponseBuilder.ok(Map.of("orders", responses));
        } catch (Exception e) {
            return ResponseBuilder.fail("获取骑手订单记录失败：" + e.getMessage());
        }
    }

    /**
     * 查询收入统计
     */
    @GetMapping("/rider-earnings")
    public CommonResponse getRiderEarnings() {
        // 权限验证：仅骑手可操作
        if (!"rider".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅骑手可操作");
        }
        
        try {
            Long riderId = UserHolder.getId();
            Map<String, Object> result = orderService.getRiderEarnings(riderId);

            RiderEarningsResponse response = new RiderEarningsResponse();
            response.setCompletedOrders(((Number) result.getOrDefault("completed_orders", 0)).intValue());
            response.setTotalEarnings((BigDecimal) result.getOrDefault("total_earnings", BigDecimal.ZERO));
            response.setCurrentMonth((BigDecimal) result.getOrDefault("current_month", BigDecimal.ZERO));

            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            return ResponseBuilder.fail("获取收入统计失败：" + e.getMessage());
        }
    }
    /**
     * 商家更新订单状态
     */
    @PutMapping("/merchant-update")
    public CommonResponse updateOrderByMerchant(
            @RequestHeader("Authorization") String tokenHeader,
            @Valid @RequestBody OrderUpdateByMerchantRequest request
    ) {
        // 权限验证：仅商家可操作
        if (!"merchant".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }
        
        try {
            if (request.getNewStatus() == null){
                return ResponseBuilder.fail("订单更新失败：商家未设置新状态");
            }
            if (request.getNewStatus() == 4 && request.getCancelReason() == null){
                return ResponseBuilder.fail("订单更新失败：商家未设置取消原因");
            }

            // Step 1: 调用Service层执行更新
            boolean ok = orderService.updateOrderByMerchant(
                    UserHolder.getId(),
                    request.getId(),
                    request.getNewStatus()
            );

            // Step 2: 处理失败情况
            if (!ok) {
                return ResponseBuilder.fail("订单更新失败：权限不足或订单不存在");
            }

            // Step 3: 构造响应
            OrderUpdateByMerchantResponse response = new OrderUpdateByMerchantResponse();
            response.setId(request.getId());
            response.setNewStatus(request.getNewStatus());
            response.setUpdateAt(LocalDateTime.now());
            response.setCancelReason(request.getCancelReason());

            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            return ResponseBuilder.fail("订单更新失败：" + e.getMessage());
        }
    }

    /**
     * 商家分页查看订单
     */
    @PostMapping("/merchant-list")
    public CommonResponse ordersReadByMerchant(@Valid @RequestBody OrderReadByMerchantRequest request) {
        // 权限验证：仅商家可操作
        if (!"merchant".equals(UserHolder.getRole())) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }
        
        try {
            // Step 1: 参数校验
            if (request.getStoreId() == null) {
                return ResponseBuilder.fail("订单查看失败：未提供store_id字段");
            }

            Long storeId = request.getStoreId();
            Long merchantId = UserHolder.getId();
            if(!storeService.validateStoreOwnership(storeId, merchantId)){
                return ResponseBuilder.fail("订单查看失败：店铺不属于您");
            }

            // Step 2: 调用Service分页查询
            List<Order> orders;
            if (request.getStatus() == null) {
                orders = orderService.getOrdersByMerchant(
                        storeId,
                        request.getPage(),
                        request.getPageSize()
                );
            } else {
                orders = orderService.getOrdersByMerchantAndStatus(
                        storeId,
                        request.getStatus(),
                        request.getPage(),
                        request.getPageSize()
                );
            }

            // Step 3: 转换为响应对象
            List<OrderReadByMerchantResponse> responses = orders.stream().map(order -> {
                OrderReadByMerchantResponse resp = new OrderReadByMerchantResponse();
                resp.setOrderId(order.getId());
                resp.setUserName(order.getUserId()); // 假设用户ID字段为userId
                resp.setStatus(order.getStatus());
                resp.setTotalPrice(order.getTotalPrice());
                resp.setCreatedAt(order.getCreatedAt());
                return resp;
            }).toList();

            // Step 4: 包装分页响应
            OrderPageForMerchantResponse pageResponse = new OrderPageForMerchantResponse();
            pageResponse.setList(responses);
            pageResponse.setCurrentPage(request.getPage());
            pageResponse.setPageSize(request.getPageSize());
            // 总记录数需另查（此处省略具体实现）

            return ResponseBuilder.ok(pageResponse);
        } catch (Exception e) {
            return ResponseBuilder.fail("订单查看失败：" + e.getMessage());
        }
    }
}
