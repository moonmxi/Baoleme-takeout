/**
 * 用户业务服务实现类
 * 实现用户相关的业务逻辑
 * 重构后仅包含用户自身数据的操作，跨数据库操作移至Controller层
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.userservice.service.impl;

import org.demo.userservice.mapper.UserMapper;
import org.demo.userservice.pojo.User;
import org.demo.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户业务服务实现类
 * 重构后仅处理用户自身数据，不涉及跨数据库操作
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 用户数据访问对象
     */
    @Autowired
    private UserMapper userMapper;



    /**
     * 用户注册
     * 
     * @param user 新注册信息
     * @return User 成功注册的 User 对象，失败返回 null
     */
    @Override
    public User register(User user) {
        try {
            // 检查用户名和手机号是否已存在
            if (userMapper.existsByUsername(user.getUsername()) || 
                userMapper.existsByPhone(user.getPhone())) {
                return null;
            }
            
            user.setCreatedAt(LocalDateTime.now());
            int result = userMapper.insert(user);
            return result > 0 ? user : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 用户登录验证
     * 
     * @param phone 手机号
     * @param password 密码明文
     * @return 验证成功返回 User，失败返回 null
     */
    @Override
    public User login(String phone, String password) {
        try {
            User user = userMapper.findByPhone(phone);
            if (user != null && password.equals(user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(Long userId) {
        try {
            return userMapper.deleteById(userId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取用户信息
     * 
     * @param userId 用户主键 ID
     * @return User 或 null
     */
    @Override
    public User getInfo(Long userId) {
        try {
            return userMapper.selectById(userId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新用户资料
     * 
     * @param user 要更新的 User 对象
     * @return true 表示成功，false 表示失败
     */
    @Override
    public boolean updateInfo(User user) {
        if (user == null || user.getId() == null) return false;

        User existing = userMapper.selectById(user.getId());
        if (existing == null) return false;

        // 只更新非空字段
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            existing.setUsername(user.getUsername());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(user.getPassword());
        }
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            existing.setPhone(user.getPhone());
        }
        if (user.getAvatar() != null) {
            existing.setAvatar(user.getAvatar());
        }
        if (user.getDescription() != null) {
            existing.setDescription(user.getDescription());
        }
        if (user.getLocation() != null) {
            existing.setLocation(user.getLocation());
        }
        if (user.getGender() != null) {
            existing.setGender(user.getGender());
        }

        return userMapper.updateById(existing) > 0;
    }

    /**
     * 更新用户头像
     * 
     * @param userId 用户ID
     * @param avatarUrl 头像URL，null表示删除头像
     * @return true 表示成功，false 表示失败
     */
    @Override
    public boolean updateUserAvatar(Long userId, String avatarUrl) {
        try {
            User user = new User();
            user.setId(userId);
            user.setAvatar(avatarUrl);
            return userMapper.updateById(user) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用户注销（逻辑删除）
     * 
     * @param userId 主键 ID
     * @return true 表示成功，false 表示失败
     */
    @Override
    public boolean cancelAccount(Long userId) {
        try {
            return userMapper.deleteById(userId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 收藏店铺
     * 
     * @param userId 用户ID
     * @param storeId 店铺ID
     * @return 是否成功
     */
    @Override
    public boolean favoriteStore(Long userId, Long storeId) {
        if (userMapper.existsFavorite(userId, storeId)) {
            System.out.println("收藏失败：已收藏该店铺");
            return false;
        }
        return userMapper.insertFavorite(userId, storeId) > 0;
    }
    /**
     * 删除收藏
     */
    @Override
    public boolean deleteFavorite(Long userId, Long storeId) {
        return userMapper.deleteFavorite(userId, storeId) > 0;
    }


}