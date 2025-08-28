/**
 * 用户上下文持有者
 * 使用ThreadLocal存储当前请求的用户信息
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.common;

/**
 * 用户上下文持有者类
 * 在请求处理过程中存储和获取当前用户信息
 */
public class UserHolder {

    /**
     * 存储用户ID的ThreadLocal
     */
    private static final ThreadLocal<Long> idHolder = new ThreadLocal<>();
    
    /**
     * 存储用户角色的ThreadLocal
     */
    private static final ThreadLocal<String> roleHolder = new ThreadLocal<>();

    /**
     * 设置当前用户信息
     * 
     * @param id 用户ID
     * @param role 用户角色
     */
    public static void set(Long id, String role) {
        idHolder.set(id);
        roleHolder.set(role);
    }

    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    public static Long getId() {
        return idHolder.get();
    }

    /**
     * 获取当前用户角色
     * 
     * @return 用户角色
     */
    public static String getRole() {
        return roleHolder.get();
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        idHolder.remove();
        roleHolder.remove();
    }
}