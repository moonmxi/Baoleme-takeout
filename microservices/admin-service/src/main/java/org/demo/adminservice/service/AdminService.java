/**
 * 管理员服务接口
 * 定义管理员相关的业务操作方法，包括登录认证、用户管理、商家管理、骑手管理等功能
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.adminservice.service;

import org.demo.adminservice.pojo.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务接口
 * 提供管理员相关的业务逻辑处理方法
 */
public interface AdminService {

    /**
     * 管理员登录
     * 
     * @param adminId 管理员ID
     * @param password 密码
     * @return Admin 管理员实体，登录成功返回管理员信息，失败返回null
     */
    Admin login(Long adminId, String password);

    /**
     * 分页获取所有用户
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 关键词搜索
     * @param gender 性别筛选
     * @param startId 起始ID
     * @param endId 结束ID
     * @return List<User> 用户列表
     */
    List<User> getAllUsersPaged(int page, int pageSize, String keyword, String gender, Long startId, Long endId);

    /**
     * 分页获取所有骑手
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 关键词搜索
     * @param startId 起始ID
     * @param endId 结束ID
     * @param status 状态筛选
     * @param dispatchMode 派单模式筛选
     * @param startBalance 起始余额
     * @param endBalance 结束余额
     * @return List<Rider> 骑手列表
     */
    List<Rider> getAllRidersPaged(int page, int pageSize, String keyword, Long startId, Long endId, 
                                  Integer status, Integer dispatchMode, Long startBalance, Long endBalance);

    /**
     * 分页获取所有商家
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 关键词搜索
     * @param startId 起始ID
     * @param endId 结束ID
     * @return List<Merchant> 商家列表
     */
    List<Merchant> getAllMerchantsPaged(int page, int pageSize, String keyword, Long startId, Long endId);

    /**
     * 分页获取所有店铺
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 关键词搜索
     * @param merchantId 商家ID筛选
     * @param status 状态筛选
     * @return List<Store> 店铺列表
     */
    List<Store> getAllStoresPaged(int page, int pageSize, String keyword, Long merchantId, Integer status);

    /**
     * 根据店铺ID获取商品列表
     * 
     * @param storeId 店铺ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Product> 商品列表
     */
    List<Product> getProductsByStore(Long storeId, int page, int pageSize);

    /**
     * 分页获取所有订单
     * 
     * @param userId 用户ID筛选
     * @param storeId 店铺ID筛选
     * @param riderId 骑手ID筛选
     * @param status 状态筛选
     * @param createdAt 创建时间筛选
     * @param endedAt 结束时间筛选
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Order> 订单列表
     */
    List<Order> getAllOrdersPaged(Long userId, Long storeId, Long riderId, Integer status, 
                                  LocalDateTime createdAt, LocalDateTime endedAt, int page, int pageSize);

    /**
     * 根据条件获取评论列表
     * 
     * @param userId 用户ID筛选
     * @param storeId 店铺ID筛选
     * @param productId 商品ID筛选
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param pageSize 每页大小
     * @param startRating 起始评分
     * @param endRating 结束评分
     * @return List<Review> 评论列表
     */
    List<Review> getReviewsByCondition(Long userId, Long storeId, Long productId, LocalDateTime startTime, 
                                       LocalDateTime endTime, int page, int pageSize, Integer startRating, Integer endRating);

    /**
     * 根据关键词搜索店铺和商品
     * 
     * @param keyword 搜索关键词
     * @return List<Map<String, Object>> 搜索结果
     */
    List<Map<String, Object>> searchStoreAndProductByKeyword(String keyword);

    /**
     * 根据订单ID获取订单
     * 
     * @param orderId 订单ID
     * @return Order 订单信息
     */
    Order getOrderById(Long orderId);

    /**
     * 根据评论ID获取评论
     * 
     * @param reviewId 评论ID
     * @return Review 评论信息
     */
    Review getReviewById(Long reviewId);

    /**
     * 根据用户名删除用户
     * 
     * @param username 用户名
     * @return boolean 删除是否成功
     */
    boolean deleteUserByUsername(String username);

    /**
     * 根据用户名删除骑手
     * 
     * @param username 用户名
     * @return boolean 删除是否成功
     */
    boolean deleteRiderByUsername(String username);

    /**
     * 根据用户名删除商家
     * 
     * @param username 用户名
     * @return boolean 删除是否成功
     */
    boolean deleteMerchantByUsername(String username);

    /**
     * 根据店铺名删除店铺
     * 
     * @param storeName 店铺名
     * @return boolean 删除是否成功
     */
    boolean deleteStoreByName(String storeName);

    /**
     * 根据商品名和店铺名删除商品
     * 
     * @param productName 商品名
     * @param storeName 店铺名
     * @return boolean 删除是否成功
     */
    boolean deleteProductByNameAndStore(String productName, String storeName);
}