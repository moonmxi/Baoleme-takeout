/**
 * 商家业务服务实现类
 * 实现商家相关的业务逻辑
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.service.impl;

import org.demo.merchantservice.pojo.Merchant;
import org.demo.merchantservice.service.MerchantService;
import org.demo.merchantservice.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 商家业务服务实现类
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    /**
     * 商家数据访问对象
     */
    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 密码加密器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 创建商家
     * 
     * @param merchant 商家信息
     * @return 创建成功的商家对象，失败返回null
     */
    @Override
    public Merchant createMerchant(Merchant merchant) {
        try {
            // 检查用户名和手机号是否已存在
            if (merchantMapper.existsByUsername(merchant.getUsername()) || 
                merchantMapper.existsByPhone(merchant.getPhone())) {
                return null;
            }
            
            // 加密密码
            merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));
            merchant.setCreatedAt(LocalDateTime.now());
            
            int result = merchantMapper.insert(merchant);
            return result > 0 ? merchant : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据手机号获取商家信息
     * 
     * @param phone 手机号
     * @return 商家信息，不存在返回null
     */
    @Override
    public Merchant getMerchantByPhone(String phone) {
        try {
            return merchantMapper.findByPhone(phone);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据ID获取商家信息
     * 
     * @param id 商家ID
     * @return 商家信息，不存在返回null
     */
    @Override
    public Merchant getMerchantById(Long id) {
        try {
            return merchantMapper.selectById(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新商家信息
     * 
     * @param merchant 要更新的商家信息
     * @return 更新后的商家信息，失败返回null
     */
    @Override
    public Merchant updateInfo(Merchant merchant) {
        try {
            // 如果密码不为空，需要加密
            if (merchant.getPassword() != null && !merchant.getPassword().isEmpty()) {
                merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));
            }
            
            int result = merchantMapper.updateById(merchant);
            return result > 0 ? merchantMapper.selectById(merchant.getId()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除商家
     * 
     * @param id 商家ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteMerchant(Long id) {
        try {
            return merchantMapper.deleteById(id) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 更新商家头像
     * 
     * @param merchantId 商家ID
     * @param avatarUrl 头像URL，null表示删除头像
     * @return 是否更新成功
     */
    @Override
    public boolean updateMerchantAvatar(Long merchantId, String avatarUrl) {
        try {
            Merchant merchant = new Merchant();
            merchant.setId(merchantId);
            merchant.setAvatar(avatarUrl);
            return merchantMapper.updateById(merchant) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}