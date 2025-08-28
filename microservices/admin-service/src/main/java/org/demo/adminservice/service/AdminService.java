/**
 * 管理员服务接口
 * 定义管理员相关的业务操作方法，重构后仅包含管理员认证相关功能
 * 其他数据操作通过网关API调用相应微服务
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.adminservice.service;

import org.demo.adminservice.pojo.Admin;

/**
 * 管理员服务接口
 * 提供管理员认证相关的业务逻辑处理方法
 * 重构后移除了对其他微服务数据的直接访问
 */
public interface AdminService {

    /**
     * 管理员登录
     * 
     * @param adminId 管理员ID
     * @param password 密码
     * @return Admin 管理员实体，登录成功返回管理员信息，失败返回null
     */
    Admin login(Long adminId, String password);

    /**
     * 根据管理员ID获取管理员信息
     * 
     * @param adminId 管理员ID
     * @return Admin 管理员实体
     */
    Admin getAdminById(Long adminId);

    /**
     * 验证管理员密码
     * 
     * @param admin 管理员实体
     * @param rawPassword 原始密码
     * @return boolean 密码是否正确
     */
    boolean validatePassword(Admin admin, String rawPassword);

    /**
     * 更新管理员密码
     * 
     * @param adminId 管理员ID
     * @param newPassword 新密码
     * @return boolean 更新是否成功
     */
    boolean updatePassword(Long adminId, String newPassword);

    /**
     * 检查管理员是否存在
     * 
     * @param adminId 管理员ID
     * @return boolean 管理员是否存在
     */
    boolean existsById(Long adminId);
}