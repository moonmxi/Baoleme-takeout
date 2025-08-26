/**
 * 店铺业务服务接口
 * 定义店铺相关的业务操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.service;

import org.demo.merchantservice.pojo.Store;

import java.util.List;

/**
 * 店铺业务服务接口
 * 提供店铺创建、查询、更新、删除等业务方法
 */
public interface StoreService {

    /**
     * 创建店铺
     * 
     * @param store 店铺信息
     * @return 创建成功的店铺对象，失败返回null
     */
    Store createStore(Store store);

    /**
     * 根据商家ID获取店铺列表
     * 
     * @param merchantId 商家ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 店铺列表
     */
    List<Store> getStoresByMerchant(Long merchantId, Integer page, Integer pageSize);

    /**
     * 根据ID获取店铺信息
     * 
     * @param storeId 店铺ID
     * @return 店铺信息，不存在返回null
     */
    Store getStoreById(Long storeId);

    /**
     * 更新店铺信息
     * 
     * @param store 要更新的店铺信息
     * @return 是否更新成功
     */
    boolean updateStore(Store store);

    /**
     * 切换店铺状态
     * 
     * @param storeId 店铺ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean toggleStoreStatus(Long storeId, Integer status);

    /**
     * 删除店铺
     * 
     * @param storeId 店铺ID
     * @return 是否删除成功
     */
    boolean deleteStore(Long storeId);

    /**
     * 验证店铺归属权
     * 
     * @param storeId 店铺ID
     * @param merchantId 商家ID
     * @return 是否属于该商家
     */
    boolean validateStoreOwnership(Long storeId, Long merchantId);

    /**
     * 更新店铺图片
     * 
     * @param storeId 店铺ID
     * @param imageUrl 图片URL，null表示删除图片
     * @return 是否更新成功
     */
    boolean updateStoreImage(Long storeId, String imageUrl);
}