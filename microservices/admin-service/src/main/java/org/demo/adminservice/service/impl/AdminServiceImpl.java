/**
 * 管理员服务实现类
 * 实现AdminService接口，提供管理员认证相关的业务逻辑处理
 * 重构后仅包含管理员自身数据的操作，其他数据通过网关API调用
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.adminservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demo.adminservice.mapper.AdminMapper;
import org.demo.adminservice.pojo.Admin;
import org.demo.adminservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
// 移除BCrypt加密依赖，改为简单字符串匹配
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员服务实现类
 * 实现管理员认证相关的业务逻辑，重构后移除了对其他微服务数据的直接访问
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    /**
     * 管理员数据访问接口
     */
    @Autowired
    private AdminMapper adminMapper;

    // 移除密码加密器，改为简单字符串匹配验证

    /**
     * 管理员登录
     * 
     * @param adminId 管理员ID
     * @param password 密码
     * @return Admin 管理员实体，登录成功返回管理员信息，失败返回null
     */
    @Override
    public Admin login(Long adminId, String password) {
        try {
            if (adminId == null || password == null || password.trim().isEmpty()) {
                return null;
            }

            Admin admin = adminMapper.selectById(adminId);
            if (admin == null || !validatePassword(admin, password)) {
                return null;
            }

            return admin;
        } catch (Exception e) {
            log.error("管理员登录失败: adminId={}", adminId, e);
            return null;
        }
    }

    /**
     * 根据管理员ID获取管理员信息
     * 
     * @param adminId 管理员ID
     * @return Admin 管理员实体
     */
    @Override
    public Admin getAdminById(Long adminId) {
        try {
            if (adminId == null) {
                return null;
            }
            return adminMapper.selectById(adminId);
        } catch (Exception e) {
            log.error("查询管理员信息失败: adminId={}", adminId, e);
            return null;
        }
    }

    /**
     * 验证管理员密码 - 简化为字符串匹配
     * 
     * @param admin 管理员实体
     * @param rawPassword 原始密码
     * @return boolean 密码是否正确
     */
    @Override
    public boolean validatePassword(Admin admin, String rawPassword) {
        try {
            if (admin == null || admin.getPassword() == null || rawPassword == null) {
                return false;
            }
            // 简化为直接字符串匹配
            return admin.getPassword().equals(rawPassword.trim());
        } catch (Exception e) {
            log.error("密码验证失败", e);
            return false;
        }
    }

    /**
     * 更新管理员密码 - 简化为明文存储
     * 
     * @param adminId 管理员ID
     * @param newPassword 新密码
     * @return boolean 更新是否成功
     */
    @Override
    public boolean updatePassword(Long adminId, String newPassword) {
        try {
            if (adminId == null || newPassword == null || newPassword.trim().isEmpty()) {
                return false;
            }

            Admin admin = adminMapper.selectById(adminId);
            if (admin == null) {
                return false;
            }

            // 简化为明文密码存储
            admin.setPassword(newPassword.trim());
            admin.setUpdatedAt(LocalDateTime.now());
            
            return adminMapper.updateById(admin) > 0;
        } catch (Exception e) {
            log.error("更新管理员密码失败: adminId={}", adminId, e);
            return false;
        }
    }

    /**
     * 检查管理员是否存在
     * 
     * @param adminId 管理员ID
     * @return boolean 管理员是否存在
     */
    @Override
    public boolean existsById(Long adminId) {
        try {
            if (adminId == null) {
                return false;
            }
            Admin admin = adminMapper.selectById(adminId);
            return admin != null && (admin.getDeleted() == null || admin.getDeleted() == 0);
        } catch (Exception e) {
            log.error("检查管理员是否存在失败: adminId={}", adminId, e);
            return false;
        }
    }
}