/**
 * 店铺服务接口
 * 提供店铺相关的业务逻辑方法
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.service;

/**
 * 店铺服务接口
 * 定义店铺相关的业务方法
 */
public interface StoreService {
    
    /**
     * 验证店铺所有权
     * 验证指定的店铺是否属于指定的商户
     * 
     * @param storeId 店铺ID
     * @param merchantId 商户ID
     * @return 如果店铺属于该商户返回true，否则返回false
     */
    boolean validateStoreOwnership(Long storeId, Long merchantId);
}