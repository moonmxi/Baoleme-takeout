/**
 * 骑手业务服务实现类
 * 实现骑手相关的业务逻辑
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.riderservice.service.impl;

import org.demo.riderservice.pojo.Rider;
import org.demo.riderservice.service.RiderService;
import org.demo.riderservice.mapper.RiderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 骑手业务服务实现类
 */
@Service
public class RiderServiceImpl implements RiderService {

    /**
     * 骑手数据访问对象
     */
    @Autowired
    private RiderMapper riderMapper;

    /**
     * 密码加密器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 骑手注册
     * 
     * @param rider 骑手信息
     * @return 注册成功的骑手对象，失败返回null
     */
    @Override
    public Rider register(Rider rider) {
        try {
            // 检查用户名和手机号是否已存在
            if (riderMapper.existsByUsername(rider.getUsername()) || 
                riderMapper.existsByPhone(rider.getPhone())) {
                return null;
            }
            
            // 加密密码
            rider.setPassword(passwordEncoder.encode(rider.getPassword()));
            rider.setCreatedAt(LocalDateTime.now());
            rider.setOrderStatus(1); // 默认在线状态
            rider.setDispatchMode(1); // 默认自动接单
            rider.setBalance(0L); // 初始余额为0
            
            int result = riderMapper.insert(rider);
            return result > 0 ? rider : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 骑手登录验证
     * 
     * @param phone 手机号
     * @param password 密码明文
     * @return 验证成功返回Rider，失败返回null
     */
    @Override
    public Rider login(String phone, String password) {
        try {
            Rider rider = riderMapper.findByPhone(phone);
            if (rider != null && passwordEncoder.matches(password, rider.getPassword())) {
                return rider;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除骑手
     * 
     * @param riderId 骑手ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(Long riderId) {
        try {
            return riderMapper.deleteById(riderId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取骑手信息
     * 
     * @param riderId 骑手ID
     * @return 骑手信息，不存在返回null
     */
    @Override
    public Rider getInfo(Long riderId) {
        try {
            return riderMapper.selectById(riderId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新骑手信息
     * 
     * @param rider 要更新的骑手信息
     * @return 是否更新成功
     */
    @Override
    public boolean updateInfo(Rider rider) {
        if (rider == null || rider.getId() == null) return false;

        Rider existing = riderMapper.selectById(rider.getId());
        if (existing == null) return false;

        // 只更新非空字段
        if (rider.getUsername() != null && !rider.getUsername().isEmpty()) {
            existing.setUsername(rider.getUsername());
        }
        if (rider.getPassword() != null && !rider.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(rider.getPassword()));
        }
        if (rider.getPhone() != null && !rider.getPhone().isEmpty()) {
            existing.setPhone(rider.getPhone());
        }
        if (rider.getDispatchMode() != null) {
            existing.setDispatchMode(rider.getDispatchMode());
        }
        if (rider.getOrderStatus() != null) {
            existing.setOrderStatus(rider.getOrderStatus());
        }
        if (rider.getAvatar() != null) {
            existing.setAvatar(rider.getAvatar());
        }

        return riderMapper.updateById(existing) > 0;
    }

    /**
     * 随机派单给骑手
     * 
     * @param riderId 骑手ID
     * @return 是否派单成功
     */
    @Override
    public boolean randomSendOrder(Long riderId) {
        try {
            // 简化实现：检查骑手状态并模拟派单
            Rider rider = riderMapper.selectById(riderId);
            if (rider != null && rider.getOrderStatus() == 1 && rider.getDispatchMode() == 1) {
                // 这里应该实现具体的派单逻辑
                // 目前返回false表示暂无可派订单
                return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 更新骑手头像
     * 
     * @param riderId 骑手ID
     * @param avatarUrl 头像URL，null表示删除头像
     * @return 是否更新成功
     */
    @Override
    public boolean updateRiderAvatar(Long riderId, String avatarUrl) {
        try {
            Rider rider = new Rider();
            rider.setId(riderId);
            rider.setAvatar(avatarUrl);
            return riderMapper.updateById(rider) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}