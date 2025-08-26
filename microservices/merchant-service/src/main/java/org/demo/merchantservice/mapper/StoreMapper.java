/**
 * 店铺数据访问接口
 * 定义店铺相关的数据库操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.merchantservice.pojo.Store;

import java.util.List;

/**
 * 店铺数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    /**
     * 根据店铺名称查询是否存在
     * 
     * @param name 店铺名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM store WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

    /**
     * 根据商家ID查询店铺列表
     * 
     * @param merchantId 商家ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 店铺列表
     */
    @Select("SELECT * FROM store WHERE merchant_id = #{merchantId} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Store> findByMerchantId(@Param("merchantId") Long merchantId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 更新店铺状态
     * 
     * @param storeId 店铺ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE store SET status = #{status} WHERE id = #{storeId}")
    int updateStatus(@Param("storeId") Long storeId, @Param("status") Integer status);
}