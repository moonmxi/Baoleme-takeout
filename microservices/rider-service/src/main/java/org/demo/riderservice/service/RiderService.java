/**
 * 骑手业务服务接口
 * 定义骑手相关的业务操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.riderservice.service;

import org.demo.riderservice.pojo.Rider;

/**
 * 骑手业务服务接口
 * 提供骑手注册、登录、信息管理、接单等业务方法
 */
public interface RiderService {

    /**
     * 骑手注册
     * 
     * @param rider 骑手信息
     * @return 注册成功的骑手对象，失败返回null
     */
    Rider register(Rider rider);

    /**
     * 骑手登录验证
     * 
     * @param phone 手机号
     * @param password 密码明文
     * @return 验证成功返回Rider，失败返回null
     */
    Rider login(String phone, String password);

    /**
     * 删除骑手
     * 
     * @param riderId 骑手ID
     * @return 是否删除成功
     */
    boolean delete(Long riderId);

    /**
     * 获取骑手信息
     * 
     * @param riderId 骑手ID
     * @return 骑手信息，不存在返回null
     */
    Rider getInfo(Long riderId);

    /**
     * 更新骑手信息
     * 
     * @param rider 要更新的骑手信息
     * @return 是否更新成功
     */
    boolean updateInfo(Rider rider);

    /**
     * 随机派单给骑手
     * 
     * @param riderId 骑手ID
     * @return 是否派单成功
     */
    boolean randomSendOrder(Long riderId);
}