/**
 * 订单服务接口
 * 定义订单相关的业务逻辑方法
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.service;

import org.demo.gateway.dto.request.OrderCreateRequest;
import org.demo.gateway.dto.response.OrderResponse;
import org.demo.gateway.pojo.Order;
import org.demo.gateway.pojo.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 * 定义订单管理的核心业务方法
 */
public interface OrderService {

    /**
     * 创建订单
     * 
     * @param userId 用户ID
     * @param request 订单创建请求
     * @return OrderResponse 创建的订单信息
     * @throws Exception 创建失败时抛出异常
     */
    OrderResponse createOrder(Long userId, OrderCreateRequest request) throws Exception;

    /**
     * 获取可抢订单列表
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 可抢订单列表
     */
    List<Order> getAvailableOrders(int page, int pageSize);

    /**
     * 抢单
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     * @return boolean 抢单是否成功
     */
    boolean grabOrder(Long orderId, Long riderId);

    /**
     * 骑手取消订单
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     * @return boolean 取消是否成功
     */
    boolean riderCancelOrder(Long orderId, Long riderId);

    /**
     * 骑手更新订单状态
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     * @param targetStatus 目标状态
     * @return boolean 更新是否成功
     */
    boolean riderUpdateOrderStatus(Long orderId, Long riderId, Integer targetStatus);

    /**
     * 获取骑手订单列表
     * 
     * @param riderId 骑手ID
     * @param status 订单状态（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 骑手订单列表
     */
    List<Order> getRiderOrders(Long riderId, Integer status, int page, int pageSize);

    /**
     * 获取骑手收入统计
     * 
     * @param riderId 骑手ID
     * @return Map<String, Object> 收入统计信息
     */
    Map<String, Object> getRiderEarnings(Long riderId);

    /**
     * 商家更新订单状态
     * 
     * @param merchantId 商家ID
     * @param orderId 订单ID
     * @param newStatus 新状态
     * @return boolean 更新是否成功
     */
    boolean updateOrderByMerchant(Long merchantId, Long orderId, Integer newStatus);

    /**
     * 获取店铺订单列表
     * 
     * @param storeId 店铺ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 店铺订单列表
     */
    List<Order> getOrdersByMerchant(Long storeId, int page, int pageSize);

    /**
     * 根据状态获取店铺订单列表
     * 
     * @param storeId 店铺ID
     * @param status 订单状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 店铺订单列表
     */
    List<Order> getOrdersByMerchantAndStatus(Long storeId, Integer status, int page, int pageSize);

    /**
     * 获取用户订单列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 用户订单列表
     */
    List<Order> getUserOrders(Long userId, int page, int pageSize);

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
    List<Order> getUserOrdersWithFilter(Long userId, Integer status, String startTime, String endTime, int page, int pageSize);

    /**
     * 获取用户当前订单（进行中的订单）
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 用户当前订单列表
     */
    List<Order> getUserCurrentOrders(Long userId, int page, int pageSize);

    /**
     * 根据订单ID获取订单详情
     * 
     * @param orderId 订单ID
     * @return Order 订单详情
     */
    Order getOrderById(Long orderId);

    /**
     * 获取订单项列表
     * 
     * @param orderId 订单ID
     * @return List<OrderItem> 订单项列表
     */
    List<OrderItem> getOrderItems(Long orderId);

    /**
     * 获取订单价格信息
     * 
     * @param orderId 订单ID
     * @return Map<String, Object> 订单价格信息
     */
    Map<String, Object> getOrderPriceInfo(Long orderId);

    /**
     * 计算订单总价
     * 
     * @param items 订单项列表
     * @return BigDecimal 订单总价
     */
    BigDecimal calculateTotalPrice(List<OrderCreateRequest.CartItemDTO> items);

    /**
     * 验证店铺是否存在且营业
     * 
     * @param storeId 店铺ID
     * @return boolean 店铺是否可用
     */
    boolean validateStore(Long storeId);

    /**
     * 获取店铺位置信息
     * 
     * @param storeId 店铺ID
     * @return String 店铺位置
     */
    String getStoreLocation(Long storeId);
}