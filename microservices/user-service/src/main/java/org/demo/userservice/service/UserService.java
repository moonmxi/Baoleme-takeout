/**
 * 用户业务服务接口
 * 定义用户相关的业务操作方法
 * 重构后仅包含用户自身数据的操作，跨数据库操作移至Controller层
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.userservice.service;

import org.demo.userservice.pojo.Store;
import org.demo.userservice.pojo.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户业务接口
 * 说明：此接口仅处理用户自身数据，不涉及跨数据库操作
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
     * 收藏店铺（仅操作用户数据库中的收藏表）
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 是否成功
     */
    boolean favoriteStore(Long userId, Long storeId);

    /**
     * 删除收藏（仅操作用户数据库中的收藏表）
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 是否删除成功
     */
    boolean deleteFavorite(Long userId, Long storeId);

    boolean updateViewHistory(Long userId, Long storeId, LocalDateTime viewTime);

}