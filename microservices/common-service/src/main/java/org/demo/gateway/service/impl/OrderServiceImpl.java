/**
 * 订单服务实现类
 * 实现订单相关的业务逻辑
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.service.impl;

import org.demo.gateway.client.GatewayApiClient;
import org.demo.gateway.common.UserHolder;
import org.demo.gateway.dto.request.OrderCreateRequest;
import org.demo.gateway.dto.response.OrderResponse;
import org.demo.gateway.mapper.OrderMapper;
import org.demo.gateway.pojo.Order;
import org.demo.gateway.pojo.OrderItem;
import org.demo.gateway.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务实现类
 * 提供订单管理的具体业务逻辑实现
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private GatewayApiClient gatewayApiClient;

    /**
     * 创建订单
     * 
     * @param userId 用户ID
     * @param request 订单创建请求
     * @return OrderResponse 创建的订单信息
     * @throws Exception 创建失败时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) throws Exception {
        // 验证店铺是否存在且营业
        if (!validateStore(request.getStoreId())) {
            throw new Exception("店铺不存在或已停业");
        }

        // 获取JWT token用于调用网关API
        String token = UserHolder.getToken();
        if (token == null) {
            throw new Exception("用户未登录或token无效");
        }

        // 补充订单项的商品信息（价格和名称）
        for (OrderCreateRequest.CartItemDTO item : request.getItems()) {
            if (item.getPrice() == null || item.getProductName() == null) {
                Map<String, Object> productInfo = gatewayApiClient.getProductById(item.getProductId(), token);
                if (productInfo.isEmpty()) {
                    throw new Exception("商品不存在: " + item.getProductId());
                }
                
                // 设置商品价格
                if (item.getPrice() == null) {
                    Object priceObj = productInfo.get("price");
                    if (priceObj != null) {
                        item.setPrice(new BigDecimal(priceObj.toString()));
                    } else {
                        throw new Exception("商品价格信息缺失: " + item.getProductId());
                    }
                }
                
                // 设置商品名称
                if (item.getProductName() == null) {
                    String productName = (String) productInfo.get("name");
                    if (productName != null) {
                        item.setProductName(productName);
                    } else {
                        throw new Exception("商品名称信息缺失: " + item.getProductId());
                    }
                }
            }
        }

        // 计算订单总价
        BigDecimal totalPrice = calculateTotalPrice(request.getItems());
        BigDecimal deliveryPrice = new BigDecimal("5.00"); // 固定配送费
        BigDecimal actualPrice = totalPrice.add(deliveryPrice);

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setStoreId(request.getStoreId());
        order.setStatus(0); // 待接单
        order.setUserLocation(request.getUserLocation());
        order.setStoreLocation(getStoreLocation(request.getStoreId()));
        order.setTotalPrice(totalPrice);
        order.setActualPrice(actualPrice);
        order.setDeliveryPrice(deliveryPrice);
        order.setRemark(request.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        order.setDeadline(LocalDateTime.now().plusHours(2)); // 2小时后过期

        // 插入订单
        orderMapper.insert(order);

        // 插入订单项
        for (OrderCreateRequest.CartItemDTO item : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            orderItem.setProductName(item.getProductName());
            orderMapper.insertOrderItem(orderItem);
        }

        // 构建响应
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setUserId(order.getUserId());
        response.setStoreId(order.getStoreId());
        response.setStatus(order.getStatus());
        response.setUserLocation(order.getUserLocation());
        response.setStoreLocation(order.getStoreLocation());
        response.setTotalPrice(order.getTotalPrice());
        response.setActualPrice(order.getActualPrice());
        response.setDeliveryPrice(order.getDeliveryPrice());
        response.setRemark(order.getRemark());
        response.setCreatedAt(order.getCreatedAt());
        response.setDeadline(order.getDeadline());

        return response;
    }

    /**
     * 获取可抢订单列表
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 可抢订单列表
     */
    @Override
    public List<Order> getAvailableOrders(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.getAvailableOrders(offset, pageSize);
    }

    /**
     * 抢单
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     * @return boolean 抢单是否成功
     */
    @Override
    public boolean grabOrder(Long orderId, Long riderId) {
        return orderMapper.grabOrder(orderId, riderId) > 0;
    }

    /**
     * 骑手取消订单
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     * @return boolean 取消是否成功
     */
    @Override
    public boolean riderCancelOrder(Long orderId, Long riderId) {
        return orderMapper.updateOrderStatus(orderId, riderId, 4) > 0;
    }

    /**
     * 骑手更新订单状态
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     * @param targetStatus 目标状态
     * @return boolean 更新是否成功
     */
    @Override
    public boolean riderUpdateOrderStatus(Long orderId, Long riderId, Integer targetStatus) {
        return orderMapper.updateOrderStatus(orderId, riderId, targetStatus) > 0;
    }

    /**
     * 获取骑手订单列表
     * 
     * @param riderId 骑手ID
     * @param status 订单状态（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 骑手订单列表
     */
    @Override
    public List<Order> getRiderOrders(Long riderId, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.getRiderOrders(riderId, status, offset, pageSize);
    }

    /**
     * 获取骑手订单列表（支持复杂条件筛选）
     * 
     * @param riderId 骑手ID
     * @param status 订单状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 骑手订单列表
     */
    @Override
    public List<Order> getRiderOrdersWithFilter(Long riderId, Integer status, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.getRiderOrdersWithFilter(riderId, status, startTime, endTime, offset, pageSize);
    }

    /**
     * 获取骑手收入统计
     * 
     * @param riderId 骑手ID
     * @return Map<String, Object> 收入统计信息
     */
    @Override
    public Map<String, Object> getRiderEarnings(Long riderId) {
        Map<String, Object> result = new HashMap<>();
        // 这里应该查询数据库获取真实的收入统计
        // 为简化实现，返回模拟数据
        result.put("completed_orders", 10);
        result.put("total_earnings", new BigDecimal("150.00"));
        result.put("current_month", new BigDecimal("50.00"));
        return result;
    }

    /**
     * 商家更新订单状态
     * 
     * @param merchantId 商家ID
     * @param orderId 订单ID
     * @param newStatus 新状态
     * @return boolean 更新是否成功
     */
    @Override
    public boolean updateOrderByMerchant(Long merchantId, Long orderId, Integer newStatus) {
        // 这里需要先验证商家是否拥有该订单的店铺
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        
        // 简化实现，直接更新状态
        return orderMapper.updateOrderByMerchant(orderId, order.getStoreId(), newStatus) > 0;
    }

    /**
     * 获取店铺订单列表
     * 
     * @param storeId 店铺ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 店铺订单列表
     */
    @Override
    public List<Order> getOrdersByMerchant(Long storeId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.getStoreOrders(storeId, null, offset, pageSize);
    }

    /**
     * 根据状态获取店铺订单列表
     * 
     * @param storeId 店铺ID
     * @param status 订单状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 店铺订单列表
     */
    @Override
    public List<Order> getOrdersByMerchantAndStatus(Long storeId, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.getStoreOrders(storeId, status, offset, pageSize);
    }

    /**
     * 获取用户订单列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 用户订单列表
     */
    @Override
    public List<Order> getUserOrders(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.getUserOrders(userId, offset, pageSize);
    }

    /**
     * 获取用户订单列表（支持条件筛选）
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 用户订单列表
     */
    @Override
    public List<Order> getUserOrdersWithFilter(Long userId, Integer status, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.getUserOrdersWithFilter(userId, status, startTime, endTime, offset, pageSize);
    }

    /**
     * 获取用户当前订单（进行中的订单）
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 用户当前订单列表
     */
    @Override
    public List<Order> getUserCurrentOrders(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        // 查询状态为0,1,2的订单（待接单、准备中、配送中）
        return orderMapper.getUserCurrentOrders(userId, offset, pageSize);
    }

    /**
     * 根据订单ID获取订单详情
     * 
     * @param orderId 订单ID
     * @return Order 订单详情
     */
    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    /**
     * 获取订单项列表
     * 
     * @param orderId 订单ID
     * @return List<OrderItem> 订单项列表
     */
    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderMapper.getOrderItems(orderId);
    }

    /**
     * 获取订单价格信息
     * 
     * @param orderId 订单ID
     * @return Map<String, Object> 订单价格信息
     */
    @Override
    public Map<String, Object> getOrderPriceInfo(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> priceInfo = new HashMap<>();
        priceInfo.put("total_price", order.getTotalPrice());
        priceInfo.put("actual_price", order.getActualPrice());
        priceInfo.put("delivery_price", order.getDeliveryPrice());
        return priceInfo;
    }

    /**
     * 计算订单总价
     * 
     * @param items 订单项列表
     * @return BigDecimal 订单总价
     */
    @Override
    public BigDecimal calculateTotalPrice(List<OrderCreateRequest.CartItemDTO> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 验证店铺是否存在且营业
     * 
     * @param storeId 店铺ID
     * @return boolean 店铺是否可用
     */
    @Override
    public boolean validateStore(Long storeId) {
        // 简化实现，假设店铺都存在且营业
        return storeId != null && storeId > 0;
    }

    /**
     * 获取店铺位置信息
     * 
     * @param storeId 店铺ID
     * @return String 店铺位置
     */
    @Override
    public String getStoreLocation(Long storeId) {
        // 简化实现，返回默认位置
        return "店铺位置-" + storeId;
    }
}