/**
 * 优惠券数据访问层接口
 * 提供优惠券相关的数据库操作方法
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.gateway.dto.response.UserCouponResponse;
import org.demo.gateway.pojo.Coupon;

import java.util.List;

/**
 * 优惠券数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 * 同时定义优惠券相关的自定义查询方法
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 检查用户是否已领取过同类型的优惠券
     * 
     * @param userId 用户ID
     * @param templateCouponId 模板优惠券ID
     * @return boolean 是否已领取
     */
    @Select("SELECT COUNT(*) > 0 FROM coupon WHERE user_id = #{userId} AND type = " +
            "(SELECT type FROM coupon WHERE id = #{templateCouponId})")
    boolean hasUserClaimedCouponType(@Param("userId") Long userId,
                                     @Param("templateCouponId") Long templateCouponId);

    /**
     * 根据ID查询优惠券
     * 
     * @param couponId 优惠券ID
     * @return Coupon 优惠券信息
     */
    @Select("SELECT * FROM coupon WHERE id = #{couponId}")
    Coupon selectById(Long couponId);

    /**
     * 根据用户ID和店铺ID查询用户优惠券
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return List<UserCouponResponse> 用户优惠券列表
     */
    @Select("SELECT c.id as coupon_id, c.type, c.discount, c.expiration_date, " +
            "c.full_amount, c.reduce_amount " +
            "FROM coupon c WHERE (c.user_id IS NULL OR c.user_id = #{userId}) " +
            "AND (c.store_id IS NULL OR c.store_id = #{storeId}) " +
            "AND (c.is_used = 0)")
    List<UserCouponResponse> selectUserCouponsByUserId(@Param("userId") Long userId, 
                                                       @Param("storeId") Long storeId);

    /**
     * 检查用户优惠券是否存在
     * 
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return boolean 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM coupon " +
            "WHERE id = #{couponId} AND user_id = #{userId}")
    boolean existsUserCoupon(@Param("userId") Long userId,
                             @Param("couponId") Long couponId);

    /**
     * 更新优惠券的用户ID（领取优惠券）
     * 
     * @param couponId 优惠券ID
     * @param userId 用户ID
     * @return int 影响行数
     */
    @Update("UPDATE coupon SET user_id = #{userId} WHERE id = #{couponId} AND user_id IS NULL")
    int updateUserCoupon(@Param("couponId") Long couponId, @Param("userId") Long userId);

    /**
     * 标记优惠券为已使用
     * 
     * @param couponId 优惠券ID
     * @return int 影响行数
     */
    @Update("UPDATE coupon SET is_used = 1 WHERE id = #{couponId}")
    int markAsUsed(@Param("couponId") Long couponId);
}