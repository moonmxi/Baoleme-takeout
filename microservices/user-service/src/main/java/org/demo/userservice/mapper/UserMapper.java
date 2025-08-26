/**
 * 用户数据访问接口
 * 定义用户相关的数据库操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.userservice.dto.response.user.*;
import org.demo.userservice.pojo.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM user WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM user WHERE phone = #{phone}")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} LIMIT 1")
    User findByPhone(@Param("phone") String phone);

    /**
     * 插入收藏记录
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 影响行数
     */
    @Insert("INSERT INTO favorite(user_id, store_id, created_at) VALUES(#{userId}, #{storeId}, NOW())")
    int insertFavorite(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 删除收藏记录
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 影响行数
     */
    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND store_id = #{storeId}")
    int deleteFavorite(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 获取用户收藏的店铺列表
     * 
     * @param userId 用户ID
     * @param type 店铺类型
     * @param distance 距离
     * @param wishPrice 期望价格
     * @param startRating 最低评分
     * @param endRating 最高评分
     * @param page 页码
     * @param pageSize 每页数量
     * @return 收藏店铺列表
     */
    default List<UserFavoriteResponse> getFavoriteStores(Long userId, String type, BigDecimal distance, 
            BigDecimal wishPrice, BigDecimal startRating, BigDecimal endRating, Integer page, Integer pageSize) {
        // 简化实现，实际应该调用具体的SQL查询
        return List.of();
    }

    /**
     * 获取用户优惠券列表
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 优惠券列表
     */
    default List<UserCouponResponse> getUserCoupons(Long userId, Long storeId) {
        // 简化实现
        return List.of();
    }

    /**
     * 领取优惠券
     * 
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 影响行数
     */
    @Insert("INSERT INTO coupon(user_id, id, created_at, is_used) VALUES(#{userId}, #{couponId}, NOW(), false)")
    int claimCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 搜索店铺
     * 
     * @param keyword 关键词
     * @param distance 距离
     * @param wishPrice 期望价格
     * @param startRating 最低评分
     * @param endRating 最高评分
     * @param page 页码
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    default List<UserSearchResponse> searchStores(String keyword, BigDecimal distance, 
            BigDecimal wishPrice, BigDecimal startRating, BigDecimal endRating, Integer page, Integer pageSize) {
        // 简化实现
        return List.of();
    }

    /**
     * 插入评价记录
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @param productId 商品ID
     * @param rating 评分
     * @param comment 评论
     * @param image 图片
     * @return 影响行数
     */
    @Insert("INSERT INTO review(user_id, store_id, product_id, rating, comment, image, created_at) " +
            "VALUES(#{userId}, #{storeId}, #{productId}, #{rating}, #{comment}, #{image}, NOW())")
    int insertReview(@Param("userId") Long userId, @Param("storeId") Long storeId, 
                    @Param("productId") Long productId, @Param("rating") Integer rating, 
                    @Param("comment") String comment, @Param("image") String image);

    /**
     * 更新浏览历史
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @param viewTime 浏览时间
     * @return 影响行数
     */
    @Insert("INSERT INTO browse_history(user_id, store_id, created_at) " +
            "VALUES(#{userId}, #{storeId}, #{viewTime}) " +
            "ON DUPLICATE KEY UPDATE created_at = #{viewTime}")
    int updateViewHistory(@Param("userId") Long userId, @Param("storeId") Long storeId, 
                         @Param("viewTime") LocalDateTime viewTime);

    /**
     * 获取浏览历史
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 浏览历史列表
     */
    default List<UserViewHistoryResponse> getViewHistory(Long userId, Integer page, Integer pageSize) {
        // 简化实现
        return List.of();
    }
}