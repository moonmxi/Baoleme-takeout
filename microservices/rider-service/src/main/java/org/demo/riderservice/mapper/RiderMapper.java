/**
 * 骑手数据访问接口
 * 定义骑手相关的数据库操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.riderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.riderservice.pojo.Rider;

/**
 * 骑手数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface RiderMapper extends BaseMapper<Rider> {

    /**
     * 根据用户名查询骑手是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM rider WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 根据手机号查询骑手是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM rider WHERE phone = #{phone}")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 根据手机号查询骑手
     * 
     * @param phone 手机号
     * @return 骑手信息
     */
    @Select("SELECT * FROM rider WHERE phone = #{phone} LIMIT 1")
    Rider findByPhone(@Param("phone") String phone);
}