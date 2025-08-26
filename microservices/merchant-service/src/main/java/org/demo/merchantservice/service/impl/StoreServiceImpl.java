/**
 * 店铺业务服务实现类
 * 实现店铺相关的业务逻辑
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.service.impl;

import org.demo.merchantservice.pojo.Store;
import org.demo.merchantservice.service.StoreService;
import org.demo.merchantservice.mapper.StoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 店铺业务服务实现类
 */
@Service
public class StoreServiceImpl implements StoreService {

    /**
     * 店铺数据访问对象
     */
    @Autowired
    private StoreMapper storeMapper;

    /**
     * 创建店铺
     * 
     * @param store 店铺信息
     * @return 创建成功的店铺对象，失败返回null
     */
    @Override
    public Store createStore(Store store) {
        try {
            // 检查店铺名称是否已存在
            if (storeMapper.existsByName(store.getName())) {
                return null;
            }
            
            store.setCreatedAt(LocalDateTime.now());
            int result = storeMapper.insert(store);
            return result > 0 ? store : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据商家ID获取店铺列表
     * 
     * @param merchantId 商家ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 店铺列表
     */
    @Override
    public List<Store> getStoresByMerchant(Long merchantId, Integer page, Integer pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            return storeMapper.findByMerchantId(merchantId, offset, pageSize);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 根据ID获取店铺信息
     * 
     * @param storeId 店铺ID
     * @return 店铺信息，不存在返回null
     */
    @Override
    public Store getStoreById(Long storeId) {
        try {
            return storeMapper.selectById(storeId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新店铺信息
     * 
     * @param store 要更新的店铺信息
     * @return 是否更新成功
     */
    @Override
    public boolean updateStore(Store store) {
        try {
            return storeMapper.updateById(store) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 切换店铺状态
     * 
     * @param storeId 店铺ID
     * @param status 新状态
     * @return 是否更新成功
     */
    @Override
    public boolean toggleStoreStatus(Long storeId, Integer status) {
        try {
            return storeMapper.updateStatus(storeId, status) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除店铺
     * 
     * @param storeId 店铺ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteStore(Long storeId) {
        try {
            return storeMapper.deleteById(storeId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证店铺归属权
     * 
     * @param storeId 店铺ID
     * @param merchantId 商家ID
     * @return 是否属于该商家
     */
    @Override
    public boolean validateStoreOwnership(Long storeId, Long merchantId) {
        try {
            Store store = storeMapper.selectById(storeId);
            return store != null && store.getMerchantId().equals(merchantId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 更新店铺图片
     * 
     * @param storeId 店铺ID
     * @param imageUrl 图片URL，null表示删除图片
     * @return 是否更新成功
     */
    @Override
    public boolean updateStoreImage(Long storeId, String imageUrl) {
        try {
            Store store = new Store();
            store.setId(storeId);
            store.setImage(imageUrl);
            return storeMapper.updateById(store) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}