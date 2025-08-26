/**
 * 用户业务服务接口
 * 定义用户相关的业务操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.service;

import org.demo.userservice.dto.response.user.*;
import org.demo.userservice.pojo.User;
import org.demo.userservice.dto.request.user.UserReviewRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户业务接口
 * 说明：此接口不涉及任何认证上下文，完全以 userId 和 user 对象为参数处理业务。
 */
public interface UserService {

    /**
     * 用户注册（不传入 ID，由框架生成）
     * 
     * @param user 新注册信息
     * @return User 成功注册的 User 对象，失败返回 null
     */
    User register(User user);

    /**
     * 用户登录验证
     * 
     * @param phone 手机号
     * @param password 密码明文
     * @return 验证成功返回 User，失败返回 null
     */
    User login(String phone, String password);

    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean delete(Long userId);

    /**
     * 获取用户信息
     * 
     * @param userId 用户主键 ID
     * @return User 或 null
     */
    User getInfo(Long userId);

    /**
     * 更新用户资料（需包含 ID）
     * 
     * @param user 要更新的 User 对象
     * @return true 表示成功，false 表示失败
     */
    boolean updateInfo(User user);

    /**
     * 更新用户头像
     * 
     * @param userId 用户ID
     * @param avatarUrl 头像URL，null表示删除头像
     * @return true 表示成功，false 表示失败
     */
    boolean updateUserAvatar(Long userId, String avatarUrl);

    /**
     * 用户注销（逻辑删除）
     * 
     * @param userId 主键 ID
     * @return true 表示成功，false 表示失败
     */
    boolean cancelAccount(Long userId);

    /**
     * 收藏店铺
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 是否成功
     */
    boolean favoriteStore(Long userId, Long storeId);

    /**
     * 获取用户收藏的店铺
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
    List<UserFavoriteResponse> getFavoriteStores(Long userId, String type, BigDecimal distance, BigDecimal wishPrice, BigDecimal startRating, BigDecimal endRating, Integer page, Integer pageSize);

    /**
     * 删除收藏
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 是否删除成功
     */
    boolean deleteFavorite(Long userId, Long storeId);

    /**
     * 获取用户优惠券
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 优惠券列表
     */
    List<UserCouponResponse> getUserCoupons(Long userId, Long storeId);

    /**
     * 领取优惠券
     * 
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 是否领取成功
     */
    boolean claimCoupon(Long userId, Long couponId);

    /**
     * 全局搜索店铺
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
    List<UserSearchResponse> searchStores(String keyword, BigDecimal distance, BigDecimal wishPrice, BigDecimal startRating, BigDecimal endRating, Integer page, Integer pageSize);

    /**
     * 提交评价
     * 
     * @param userId 用户ID
     * @param request 评价请求
     * @return 评价响应
     */
    UserReviewResponse submitReview(Long userId, UserReviewRequest request);

    /**
     * 更新浏览历史
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @param viewTime 浏览时间
     * @return 是否更新成功
     */
    boolean updateViewHistory(Long userId, Long storeId, LocalDateTime viewTime);

    /**
     * 获取浏览历史
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 浏览历史列表
     */
    List<UserViewHistoryResponse> getViewHistory(Long userId, Integer page, Integer pageSize);
}