/**
 * 用户业务服务实现类
 * 实现用户相关的业务逻辑
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.service.impl;

import org.demo.userservice.dto.response.user.*;
import org.demo.userservice.pojo.User;
import org.demo.userservice.service.UserService;
import org.demo.userservice.mapper.UserMapper;
import org.demo.userservice.dto.request.user.UserReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 用户业务服务实现类
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
     * 获取用户收藏的店铺
     */
    @Override
    public List<UserFavoriteResponse> getFavoriteStores(Long userId, String type, BigDecimal distance, BigDecimal wishPrice, BigDecimal startRating, BigDecimal endRating, Integer page, Integer pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            return userMapper.getFavoriteStores(userId, type, distance, wishPrice, startRating, endRating, page, pageSize, offset);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 删除收藏
     */
    @Override
    public boolean deleteFavorite(Long userId, Long storeId) {
        try {
            return userMapper.deleteFavorite(userId, storeId) > 0;
        } catch (Exception e) {
            return false;
        }
    }



    /**
     * 全局搜索店铺
     */
    @Override
    public List<UserSearchResponse> searchStores(String keyword, BigDecimal distance, BigDecimal wishPrice, BigDecimal startRating, BigDecimal endRating, Integer page, Integer pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            return userMapper.searchStores(keyword, distance, wishPrice, startRating, endRating, page, pageSize, offset);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 提交评价
     */
    @Override
    public UserReviewResponse submitReview(Long userId, UserReviewRequest request) {
        try {
            // 获取图片，如果request没有getImage方法则使用null
            String image = null;
            try {
                // 尝试通过反射获取image字段，如果不存在则使用null
                java.lang.reflect.Field imageField = request.getClass().getDeclaredField("image");
                imageField.setAccessible(true);
                image = (String) imageField.get(request);
            } catch (Exception ignored) {
                // 如果没有image字段，使用null
            }
            
            int result = userMapper.insertReview(userId, request.getStoreId(), request.getProductId(), 
                request.getRating(), request.getComment(), image);
            
            UserReviewResponse response = new UserReviewResponse();
            response.setComment(request.getComment());
            response.setRating(request.getRating());
            // 设置商品名称和店铺名称为空，实际应该从数据库查询
            response.setProductName("");
            response.setStoreName("");
            response.setImages(image != null ? java.util.List.of(image) : java.util.List.of());
            return response;
        } catch (Exception e) {
            UserReviewResponse response = new UserReviewResponse();
            response.setComment("评价提交失败：" + e.getMessage());
            response.setRating(0);
            response.setProductName("");
            response.setStoreName("");
            response.setImages(java.util.List.of());
            return response;
        }
    }

    /**
     * 更新浏览历史
     */
    @Override
    public boolean updateViewHistory(Long userId, Long storeId, LocalDateTime viewTime) {
        try {
            return userMapper.addViewHistory(userId, storeId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取浏览历史
     */
    @Override
    public List<UserViewHistoryResponse> getViewHistory(Long userId, Integer page, Integer pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            return userMapper.getViewHistory(userId, page, pageSize, offset);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}