/**
 * 订单数据访问层接口
 * 提供订单相关的数据库操作方法
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.demo.gateway.pojo.Order;
import org.demo.gateway.pojo.OrderItem;

import java.util.List;

/**
 * 订单数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 * 同时定义订单相关的自定义查询方法
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 获取可抢订单列表（状态为待接单）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Order> 可抢订单列表
     */
    @Select("SELECT * FROM `order` WHERE status = 0 ORDER BY created_at ASC LIMIT #{offset}, #{limit}")
    List<Order> getAvailableOrders(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 抢单操作（更新订单状态和骑手ID）
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     * @return int 影响行数
     */
    @Update("UPDATE `order` SET rider_id = #{riderId}, status = 1 WHERE id = #{orderId} AND status = 0")
    int grabOrder(@Param("orderId") Long orderId, @Param("riderId") Long riderId);

    /**
     * 更新订单状态
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     * @param status 新状态
     * @return int 影响行数
     */
    @Update("UPDATE `order` SET status = #{status} WHERE id = #{orderId} AND rider_id = #{riderId}")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("riderId") Long riderId, @Param("status") Integer status);

    /**
     * 根据骑手ID和条件查询订单
     * 
     * @param riderId 骑手ID
     * @param status 订单状态（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Order> 订单列表
     */
    @Select("SELECT * FROM `order` WHERE rider_id = #{riderId} " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getRiderOrders(@Param("riderId") Long riderId, 
                              @Param("status") Integer status, 
                              @Param("offset") int offset, 
                              @Param("limit") int limit);

    /**
     * 根据店铺ID查询订单
     * 
     * @param storeId 店铺ID
     * @param status 订单状态（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Order> 订单列表
     */
    @Select("SELECT * FROM `order` WHERE store_id = #{storeId} " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getStoreOrders(@Param("storeId") Long storeId, 
                              @Param("status") Integer status, 
                              @Param("offset") int offset, 
                              @Param("limit") int limit);

    /**
     * 根据用户ID查询订单
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Order> 订单列表
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getUserOrders(@Param("userId") Long userId, 
                             @Param("offset") int offset, 
                             @Param("limit") int limit);

    /**
     * 根据用户ID和条件查询订单
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Order> 订单列表
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "AND (#{startTime} IS NULL OR #{startTime} = '' OR created_at >= #{startTime}) " +
            "AND (#{endTime} IS NULL OR #{endTime} = '' OR created_at <= #{endTime}) " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getUserOrdersWithFilter(@Param("userId") Long userId,
                                       @Param("status") Integer status,
                                       @Param("startTime") String startTime,
                                       @Param("endTime") String endTime,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    /**
     * 根据用户ID查询当前订单（进行中的订单）
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return List<Order> 当前订单列表
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND status IN (0, 1, 2) ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getUserCurrentOrders(@Param("userId") Long userId,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    /**
     * 根据订单ID查询订单项
     * 
     * @param orderId 订单ID
     * @return List<OrderItem> 订单项列表
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> getOrderItems(@Param("orderId") Long orderId);

    /**
     * 插入订单项
     * 
     * @param orderItem 订单项
     * @return int 影响行数
     */
    int insertOrderItem(OrderItem orderItem);

    /**
     * 商家更新订单状态
     * 
     * @param orderId 订单ID
     * @param storeId 店铺ID
     * @param status 新状态
     * @return int 影响行数
     */
    @Update("UPDATE `order` SET status = #{status} WHERE id = #{orderId} AND store_id = #{storeId}")
    int updateOrderByMerchant(@Param("orderId") Long orderId, 
                             @Param("storeId") Long storeId, 
                             @Param("status") Integer status);
}