/**
 * 用户上下文持有者
 * 用于在请求处理过程中保存和获取当前用户信息
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.common;

/**
 * 用户上下文持有者类
 * 使用ThreadLocal存储当前请求的用户信息
 */
public class UserHolder {

    /**
     * 用户ID的ThreadLocal存储
     */
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    /**
     * 用户角色的ThreadLocal存储
     */
    private static final ThreadLocal<String> USER_ROLE = new ThreadLocal<>();

    /**
     * 店铺ID的ThreadLocal存储
     */
    private static final ThreadLocal<Long> STORE_ID = new ThreadLocal<>();

    /**
     * 设置用户ID
     * 
     * @param userId 用户ID
     */
    public static void setId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取用户ID
     * 
     * @return Long 用户ID
     */
    public static Long getId() {
        return USER_ID.get();
    }

    /**
     * 设置用户角色
     * 
     * @param role 用户角色
     */
    public static void setRole(String role) {
        USER_ROLE.set(role);
    }

    /**
     * 获取用户角色
     * 
     * @return String 用户角色
     */
    public static String getRole() {
        return USER_ROLE.get();
    }

    /**
     * 设置店铺ID
     * 
     * @param storeId 店铺ID
     */
    public static void setStoreId(Long storeId) {
        STORE_ID.set(storeId);
    }

    /**
     * 获取店铺ID
     * 
     * @return Long 店铺ID
     */
    public static Long getStoreId() {
        return STORE_ID.get();
    }

    /**
     * 清除当前线程的用户信息
     */
    public static void clear() {
        USER_ID.remove();
        USER_ROLE.remove();
        STORE_ID.remove();
    }
}