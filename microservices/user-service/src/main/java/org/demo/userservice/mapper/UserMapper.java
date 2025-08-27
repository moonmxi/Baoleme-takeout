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
     * 根据用户名删除用户
     * 
     * @param username 用户名
     * @return 影响行数
     */
    @Delete("DELETE FROM user WHERE username = #{username}")
    int deleteByUsername(@Param("username") String username);

    /**
     * 分页查询用户列表
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @return 用户列表
     */
    @Select("SELECT * FROM user ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<User> selectUsersPaged(@Param("page") int page, @Param("pageSize") int pageSize, @Param("offset") int offset);

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} LIMIT 1")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    @Update("UPDATE user SET username = #{username}, phone = #{phone}, email = #{email}, avatar = #{avatar}, updated_at = NOW() WHERE id = #{id}")
    int updateUser(User user);

    /**
     * 获取店铺列表
     * 
     * @param type 店铺类型
     * @param distance 距离
     * @param avgPrice 平均价格
     * @param startRating 最低评分
     * @param endRating 最高评分
     * @param page 页码
     * @param pageSize 每页数量
     * @return 店铺列表
     */
    @Select("""
    SELECT 
        s.id,
        s.name,
        s.description,
        s.location,
        s.type,
        s.rating,
        s.avg_price,
        s.status,
        s.created_at AS createdAt,
        s.image
    FROM store s
    WHERE s.status = 'ACTIVE'
        AND (#{type} IS NULL OR s.type = #{type})
        AND (#{distance} IS NULL OR s.distance <= #{distance})
        AND (#{avgPrice} IS NULL OR s.avg_price <= #{avgPrice})
        AND (#{startRating} IS NULL OR s.rating >= #{startRating})
        AND (#{endRating} IS NULL OR s.rating <= #{endRating})
    ORDER BY s.rating DESC, s.id DESC
    LIMIT #{offset}, #{pageSize}
""")
    List<UserSearchResponse> getStores(
            @Param("type") String type,
            @Param("distance") BigDecimal distance,
            @Param("avgPrice") BigDecimal avgPrice,
            @Param("startRating") BigDecimal startRating,
            @Param("endRating") BigDecimal endRating,
            @Param("page") int page,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset);

    /**
     * 根据用户ID更新头像
     * 
     * @param userId 用户ID
     * @param avatar 头像URL
     * @return 影响行数
     */
    @Update("UPDATE user SET avatar = #{avatar}, updated_at = NOW() WHERE id = #{userId}")
    int updateAvatarById(@Param("userId") Long userId, @Param("avatar") String avatar);

    /**
     * 添加浏览历史
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 影响行数
     */
    @Insert("INSERT INTO browse_history(user_id, store_id, created_at) VALUES(#{userId}, #{storeId}, NOW()) ON DUPLICATE KEY UPDATE created_at = NOW()")
    int addViewHistory(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 查询浏览历史
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 浏览历史列表
     */
    @Select("""
    SELECT 
        bh.store_id,
        s.name,
        s.description,
        s.location,
        s.type,
        s.rating,
        s.avg_price,
        s.status,
        s.image,
        bh.created_at AS viewTime
    FROM browse_history bh
    INNER JOIN store s ON bh.store_id = s.id
    WHERE bh.user_id = #{userId}
    ORDER BY bh.created_at DESC
    LIMIT #{offset}, #{pageSize}
""")
    List<UserViewHistoryResponse> selectViewHistory(
            @Param("userId") Long userId,
            @Param("page") int page,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset);

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
     * 检查收藏是否存在
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM favorite WHERE user_id = #{userId} AND store_id = #{storeId}")
    boolean existsFavorite(@Param("userId") Long userId, @Param("storeId") Long storeId);


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
     * @param avgPrice 平均价格
     * @param startRating 最低评分
     * @param endRating 最高评分
     * @param page 页码
     * @param pageSize 每页数量
     * @return 收藏店铺列表
     */
    @Select("""
    SELECT 
        f.store_id, 
        s.name, 
        s.description, 
        s.location, 
        s.type, 
        s.rating, 
        s.status, 
        s.created_at AS createdAt,
        s.image
    FROM favorite f 
    INNER JOIN store s ON f.store_id = s.id
    WHERE f.user_id = #{userId}
        AND (#{type} IS NULL OR s.type = #{type})
        AND (#{distance} IS NULL OR s.distance <= #{distance})
        AND (#{startRating} IS NULL OR s.rating >= #{startRating})
        AND (#{endRating} IS NULL OR s.rating <= #{endRating})
        AND (#{avg_price} IS NULL OR s.avg_price <= #{avg_price})
    ORDER BY s.id DESC
    LIMIT #{offset}, #{pageSize}
""")
    List<UserFavoriteResponse> getFavoriteStores(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("distance") BigDecimal distance,
            @Param("avg_price") BigDecimal avgPrice,
            @Param("startRating") BigDecimal startRating,
            @Param("endRating") BigDecimal endRating,
            @Param("page") int page,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset);

    /**
     * 获取用户收藏的店铺详细信息
     * 
     * @param userId 用户ID
     * @param type 店铺类型
     * @param distance 距离
     * @param avgPrice 平均价格
     * @param startRating 最低评分
     * @param endRating 最高评分
     * @param page 页码
     * @param pageSize 每页数量
     * @return 收藏店铺详细信息列表
     */
    @Select("""
    SELECT 
        f.store_id, 
        s.name, 
        s.description, 
        s.location, 
        s.type, 
        s.rating, 
        s.status, 
        s.created_at AS createdAt,
        s.image,
        s.avg_price
    FROM favorite f 
    INNER JOIN store s ON f.store_id = s.id
    WHERE f.user_id = #{userId}
        AND (#{type} IS NULL OR s.type = #{type})
        AND (#{distance} IS NULL OR s.distance <= #{distance})
        AND (#{startRating} IS NULL OR s.rating >= #{startRating})
        AND (#{endRating} IS NULL OR s.rating <= #{endRating})
        AND (#{avgPrice} IS NULL OR s.avg_price <= #{avgPrice})
    ORDER BY s.id DESC
    LIMIT #{offset}, #{pageSize}
""")
    List<UserFavoriteResponse> selectFavoriteStoresWithDetails(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("distance") BigDecimal distance,
            @Param("avgPrice") BigDecimal avgPrice,
            @Param("startRating") BigDecimal startRating,
            @Param("endRating") BigDecimal endRating,
            @Param("page") int page,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset);

    /**
     * 获取用户优惠券列表
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 优惠券列表
     */
    @Select("""
    SELECT 
        c.id,
        c.name,
        c.description,
        c.discount_amount,
        c.min_order_amount,
        c.start_date,
        c.end_date,
        c.store_id,
        uc.is_used,
        uc.used_at
    FROM user_coupon uc
    INNER JOIN coupon c ON uc.coupon_id = c.id
    WHERE uc.user_id = #{userId}
        AND (#{storeId} IS NULL OR c.store_id = #{storeId})
    ORDER BY uc.created_at DESC
""")
    List<UserCouponResponse> getUserCoupons(@Param("userId") Long userId, @Param("storeId") Long storeId);

    /**
     * 领取优惠券
     * 
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 影响行数
     */
    @Insert("INSERT INTO user_coupon(user_id, coupon_id, created_at, is_used) VALUES(#{userId}, #{couponId}, NOW(), false)")
    int claimCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 搜索店铺
     * 
     * @param keyword 关键词
     * @param distance 距离
     * @param avgPrice 平均价格
     * @param startRating 最低评分
     * @param endRating 最高评分
     * @param page 页码
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    @Select("""
    SELECT 
        s.id,
        s.name,
        s.description,
        s.location,
        s.type,
        s.rating,
        s.avg_price,
        s.status,
        s.created_at AS createdAt,
        s.image
    FROM store s
    WHERE s.status = 'ACTIVE'
        AND (#{keyword} IS NULL OR s.name LIKE CONCAT('%', #{keyword}, '%') OR s.description LIKE CONCAT('%', #{keyword}, '%'))
        AND (#{distance} IS NULL OR s.distance <= #{distance})
        AND (#{avgPrice} IS NULL OR s.avg_price <= #{avgPrice})
        AND (#{startRating} IS NULL OR s.rating >= #{startRating})
        AND (#{endRating} IS NULL OR s.rating <= #{endRating})
    ORDER BY s.rating DESC, s.id DESC
    LIMIT #{offset}, #{pageSize}
""")
    List<UserSearchResponse> searchStores(
            @Param("keyword") String keyword,
            @Param("distance") BigDecimal distance,
            @Param("avgPrice") BigDecimal avgPrice,
            @Param("startRating") BigDecimal startRating,
            @Param("endRating") BigDecimal endRating,
            @Param("page") int page,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset);

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
    @Select("""
    SELECT 
        bh.store_id,
        s.name,
        s.description,
        s.location,
        s.type,
        s.rating,
        s.avg_price,
        s.status,
        s.image,
        bh.created_at AS viewTime
    FROM browse_history bh
    INNER JOIN store s ON bh.store_id = s.id
    WHERE bh.user_id = #{userId}
    ORDER BY bh.created_at DESC
    LIMIT #{offset}, #{pageSize}
""")
    List<UserViewHistoryResponse> getViewHistory(
            @Param("userId") Long userId,
            @Param("page") int page,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset);
}