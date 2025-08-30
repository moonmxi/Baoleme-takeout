/**
 * 用户数据访问接口
 * 重构后仅包含用户自身数据的数据库操作方法
 * 跨数据库操作已移至Controller层通过GatewayApiClient调用
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.userservice.pojo.User;

import java.util.List;

/**
 * 用户数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 * 重构后仅包含用户表相关操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM user WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM user WHERE phone = #{phone}")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} LIMIT 1")
    User findByPhone(@Param("phone") String phone);

    /**
     * 根据用户名删除用户
     * 
     * @param username 用户名
     * @return 影响行数
     */
    @Delete("DELETE FROM user WHERE username = #{username}")
    int deleteByUsername(@Param("username") String username);

    /**
     * 分页查询用户列表
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @return 用户列表
     */
    @Select("SELECT * FROM user ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<User> selectUsersPaged(@Param("page") int page, @Param("pageSize") int pageSize, @Param("offset") int offset);

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} LIMIT 1")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    @Update("UPDATE user SET username = #{username}, phone = #{phone}, email = #{email}, avatar = #{avatar}, updated_at = NOW() WHERE id = #{id}")
    int updateUser(User user);



    /**
     * 根据用户ID更新头像
     * 
     * @param userId 用户ID
     * @param avatar 头像URL
     * @return 影响行数
     */
    @Update("UPDATE user SET avatar = #{avatar}, updated_at = NOW() WHERE id = #{userId}")
    int updateAvatarById(@Param("userId") Long userId, @Param("avatar") String avatar);

    @Select("SELECT COUNT(*) > 0 FROM favorite WHERE user_id = #{userId} AND store_id = #{storeId}")
    boolean existsFavorite(Long userId, Long storeId);

    @Insert("INSERT INTO favorite(user_id, store_id) VALUES(#{userId}, #{storeId})")
    int insertFavorite(Long userId, Long storeId);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND store_id = #{storeId}")
    int deleteFavorite(Long userId, Long storeId);

}