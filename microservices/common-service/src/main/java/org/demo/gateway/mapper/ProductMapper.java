/**
 * 商品数据访问层接口
 * 提供商品相关的数据库操作方法
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.demo.gateway.pojo.Product;

/**
 * 商品Mapper接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 继承BaseMapper已提供基础的CRUD操作
    // 如需要自定义查询方法，可在此添加
}