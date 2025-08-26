/**
 * 商家数据访问接口
 * 定义商家相关的数据库操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.merchantservice.pojo.Merchant;

/**
 * 商家数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    /**
     * 根据用户名查询商家是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM merchant WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 根据手机号查询商家是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM merchant WHERE phone = #{phone}")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 根据手机号查询商家
     * 
     * @param phone 手机号
     * @return 商家信息
     */
    @Select("SELECT * FROM merchant WHERE phone = #{phone} LIMIT 1")
    Merchant findByPhone(@Param("phone") String phone);
}