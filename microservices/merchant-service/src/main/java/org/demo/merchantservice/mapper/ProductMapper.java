/**
 * 商品数据访问接口
 * 定义商品相关的数据库操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.merchantservice.pojo.Product;

import java.util.List;

/**
 * 商品数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据店铺ID查询商品列表
     * 
     * @param storeId 店铺ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM product WHERE store_id = #{storeId} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Product> findByStoreId(@Param("storeId") Long storeId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 更新商品状态
     * 
     * @param productId 商品ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE product SET status = #{status} WHERE id = #{productId}")
    int updateStatus(@Param("productId") Long productId, @Param("status") Integer status);
}