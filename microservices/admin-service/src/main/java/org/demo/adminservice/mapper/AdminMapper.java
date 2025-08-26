/**
 * 管理员数据访问接口
 * 提供管理员相关的数据库操作方法，包括管理员认证、用户管理、商家管理、骑手管理等数据访问功能
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.adminservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.adminservice.pojo.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理员数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 * 同时定义管理员特有的数据访问方法
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

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
    @Select("<script>" +
            "SELECT * FROM user WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='gender != null and gender != \"\">" +
            " AND gender = #{gender}" +
            "</if>" +
            "<if test='startId != null'>" +
            " AND id >= #{startId}" +
            "</if>" +
            "<if test='endId != null'>" +
            " AND id <= #{endId}" +
            "</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<User> getAllUsersPaged(@Param("page") int page, @Param("pageSize") int pageSize, 
                                @Param("keyword") String keyword, @Param("gender") String gender, 
                                @Param("startId") Long startId, @Param("endId") Long endId, 
                                @Param("offset") int offset);

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
    @Select("<script>" +
            "SELECT * FROM rider WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='startId != null'>" +
            " AND id >= #{startId}" +
            "</if>" +
            "<if test='endId != null'>" +
            " AND id <= #{endId}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            "<if test='dispatchMode != null'>" +
            " AND dispatch_mode = #{dispatchMode}" +
            "</if>" +
            "<if test='startBalance != null'>" +
            " AND balance >= #{startBalance}" +
            "</if>" +
            "<if test='endBalance != null'>" +
            " AND balance <= #{endBalance}" +
            "</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Rider> getAllRidersPaged(@Param("page") int page, @Param("pageSize") int pageSize, 
                                  @Param("keyword") String keyword, @Param("startId") Long startId, 
                                  @Param("endId") Long endId, @Param("status") Integer status, 
                                  @Param("dispatchMode") Integer dispatchMode, @Param("startBalance") Long startBalance, 
                                  @Param("endBalance") Long endBalance, @Param("offset") int offset);

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
    @Select("<script>" +
            "SELECT * FROM merchant WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='startId != null'>" +
            " AND id >= #{startId}" +
            "</if>" +
            "<if test='endId != null'>" +
            " AND id <= #{endId}" +
            "</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Merchant> getAllMerchantsPaged(@Param("page") int page, @Param("pageSize") int pageSize, 
                                        @Param("keyword") String keyword, @Param("startId") Long startId, 
                                        @Param("endId") Long endId, @Param("offset") int offset);

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
    @Select("<script>" +
            "SELECT * FROM store WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\">" +
            " AND name LIKE CONCAT('%', #{keyword}, '%')" +
            "</if>" +
            "<if test='merchantId != null'>" +
            " AND merchant_id = #{merchantId}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Store> getAllStoresPaged(@Param("page") int page, @Param("pageSize") int pageSize, 
                                  @Param("keyword") String keyword, @Param("merchantId") Long merchantId, 
                                  @Param("status") Integer status, @Param("offset") int offset);

    /**
     * 根据店铺ID获取商品列表
     * 
     * @param storeId 店铺ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return List<Product> 商品列表
     */
    @Select("SELECT * FROM product WHERE store_id = #{storeId} ORDER BY created_at DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<Product> getProductsByStore(@Param("storeId") Long storeId, @Param("page") int page, 
                                     @Param("pageSize") int pageSize, @Param("offset") int offset);

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
    @Select("<script>" +
            "SELECT * FROM `order` WHERE 1=1" +
            "<if test='userId != null'>" +
            " AND user_id = #{userId}" +
            "</if>" +
            "<if test='storeId != null'>" +
            " AND store_id = #{storeId}" +
            "</if>" +
            "<if test='riderId != null'>" +
            " AND rider_id = #{riderId}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            "<if test='createdAt != null'>" +
            " AND created_at >= #{createdAt}" +
            "</if>" +
            "<if test='endedAt != null'>" +
            " AND ended_at <= #{endedAt}" +
            "</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Order> getAllOrdersPaged(@Param("userId") Long userId, @Param("storeId") Long storeId, 
                                  @Param("riderId") Long riderId, @Param("status") Integer status, 
                                  @Param("createdAt") LocalDateTime createdAt, @Param("endedAt") LocalDateTime endedAt, 
                                  @Param("page") int page, @Param("pageSize") int pageSize, @Param("offset") int offset);

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
    @Select("<script>" +
            "SELECT * FROM review WHERE 1=1" +
            "<if test='userId != null'>" +
            " AND user_id = #{userId}" +
            "</if>" +
            "<if test='storeId != null'>" +
            " AND store_id = #{storeId}" +
            "</if>" +
            "<if test='productId != null'>" +
            " AND product_id = #{productId}" +
            "</if>" +
            "<if test='startTime != null'>" +
            " AND created_at >= #{startTime}" +
            "</if>" +
            "<if test='endTime != null'>" +
            " AND created_at <= #{endTime}" +
            "</if>" +
            "<if test='startRating != null'>" +
            " AND rating >= #{startRating}" +
            "</if>" +
            "<if test='endRating != null'>" +
            " AND rating <= #{endRating}" +
            "</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Review> getReviewsByCondition(@Param("userId") Long userId, @Param("storeId") Long storeId, 
                                       @Param("productId") Long productId, @Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime, @Param("page") int page, 
                                       @Param("pageSize") int pageSize, @Param("startRating") Integer startRating, 
                                       @Param("endRating") Integer endRating, @Param("offset") int offset);

    /**
     * 根据关键词搜索店铺和商品
     * 
     * @param keyword 搜索关键词
     * @return List<Map<String, Object>> 搜索结果
     */
    @Select("SELECT s.id as store_id, s.name as store_name, " +
            "GROUP_CONCAT(CONCAT(p.name, ':', p.id) SEPARATOR ',') as products " +
            "FROM store s LEFT JOIN product p ON s.id = p.store_id " +
            "WHERE s.name LIKE CONCAT('%', #{keyword}, '%') OR p.name LIKE CONCAT('%', #{keyword}, '%') " +
            "GROUP BY s.id, s.name")
    List<Map<String, Object>> searchStoreAndProductByKeyword(@Param("keyword") String keyword);

    /**
     * 根据订单ID获取订单
     * 
     * @param orderId 订单ID
     * @return Order 订单信息
     */
    @Select("SELECT * FROM `order` WHERE id = #{orderId}")
    Order getOrderById(@Param("orderId") Long orderId);

    /**
     * 根据评论ID获取评论
     * 
     * @param reviewId 评论ID
     * @return Review 评论信息
     */
    @Select("SELECT * FROM review WHERE id = #{reviewId}")
    Review getReviewById(@Param("reviewId") Long reviewId);

    /**
     * 根据用户名删除用户
     * 
     * @param username 用户名
     * @return int 删除结果
     */
    @Delete("DELETE FROM user WHERE username = #{username}")
    int deleteUserByUsername(@Param("username") String username);

    /**
     * 根据用户名删除骑手
     * 
     * @param username 用户名
     * @return int 删除结果
     */
    @Delete("DELETE FROM rider WHERE username = #{username}")
    int deleteRiderByUsername(@Param("username") String username);

    /**
     * 根据用户名删除商家
     * 
     * @param username 用户名
     * @return int 删除结果
     */
    @Delete("DELETE FROM merchant WHERE username = #{username}")
    int deleteMerchantByUsername(@Param("username") String username);

    /**
     * 根据店铺名删除店铺
     * 
     * @param storeName 店铺名
     * @return int 删除结果
     */
    @Delete("DELETE FROM store WHERE name = #{storeName}")
    int deleteStoreByName(@Param("storeName") String storeName);

    /**
     * 根据商品名和店铺名删除商品
     * 
     * @param productName 商品名
     * @param storeName 店铺名
     * @return int 删除结果
     */
    @Delete("DELETE p FROM product p INNER JOIN store s ON p.store_id = s.id " +
            "WHERE p.name = #{productName} AND s.name = #{storeName}")
    int deleteProductByNameAndStore(@Param("productName") String productName, @Param("storeName") String storeName);

    // 默认实现方法，用于计算偏移量
    default List<User> getAllUsersPaged(int page, int pageSize, String keyword, String gender, Long startId, Long endId) {
        int offset = (page - 1) * pageSize;
        return getAllUsersPaged(page, pageSize, keyword, gender, startId, endId, offset);
    }

    default List<Rider> getAllRidersPaged(int page, int pageSize, String keyword, Long startId, Long endId, 
                                          Integer status, Integer dispatchMode, Long startBalance, Long endBalance) {
        int offset = (page - 1) * pageSize;
        return getAllRidersPaged(page, pageSize, keyword, startId, endId, status, dispatchMode, startBalance, endBalance, offset);
    }

    default List<Merchant> getAllMerchantsPaged(int page, int pageSize, String keyword, Long startId, Long endId) {
        int offset = (page - 1) * pageSize;
        return getAllMerchantsPaged(page, pageSize, keyword, startId, endId, offset);
    }

    default List<Store> getAllStoresPaged(int page, int pageSize, String keyword, Long merchantId, Integer status) {
        int offset = (page - 1) * pageSize;
        return getAllStoresPaged(page, pageSize, keyword, merchantId, status, offset);
    }

    default List<Product> getProductsByStore(Long storeId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return getProductsByStore(storeId, page, pageSize, offset);
    }

    default List<Order> getAllOrdersPaged(Long userId, Long storeId, Long riderId, Integer status, 
                                          LocalDateTime createdAt, LocalDateTime endedAt, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return getAllOrdersPaged(userId, storeId, riderId, status, createdAt, endedAt, page, pageSize, offset);
    }

    default List<Review> getReviewsByCondition(Long userId, Long storeId, Long productId, LocalDateTime startTime, 
                                               LocalDateTime endTime, int page, int pageSize, Integer startRating, Integer endRating) {
        int offset = (page - 1) * pageSize;
        return getReviewsByCondition(userId, storeId, productId, startTime, endTime, page, pageSize, startRating, endRating, offset);
    }
}