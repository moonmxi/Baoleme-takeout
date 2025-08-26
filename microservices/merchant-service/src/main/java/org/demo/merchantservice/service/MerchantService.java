/**
 * 商家业务服务接口
 * 定义商家相关的业务操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.service;

import org.demo.merchantservice.pojo.Merchant;

/**
 * 商家业务服务接口
 * 提供商家注册、登录、信息管理等业务方法
 */
public interface MerchantService {

    /**
     * 创建商家
     * 
     * @param merchant 商家信息
     * @return 创建成功的商家对象，失败返回null
     */
    Merchant createMerchant(Merchant merchant);

    /**
     * 根据手机号获取商家信息
     * 
     * @param phone 手机号
     * @return 商家信息，不存在返回null
     */
    Merchant getMerchantByPhone(String phone);

    /**
     * 根据ID获取商家信息
     * 
     * @param id 商家ID
     * @return 商家信息，不存在返回null
     */
    Merchant getMerchantById(Long id);

    /**
     * 更新商家信息
     * 
     * @param merchant 要更新的商家信息
     * @return 更新后的商家信息，失败返回null
     */
    Merchant updateInfo(Merchant merchant);

    /**
     * 删除商家
     * 
     * @param id 商家ID
     * @return 是否删除成功
     */
    boolean deleteMerchant(Long id);

    /**
     * 更新商家头像
     * 
     * @param merchantId 商家ID
     * @param avatarUrl 头像URL，null表示删除头像
     * @return 是否更新成功
     */
    boolean updateMerchantAvatar(Long merchantId, String avatarUrl);
}