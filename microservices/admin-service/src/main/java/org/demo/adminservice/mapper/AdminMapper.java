/**
 * 管理员数据访问接口
 * 提供管理员相关的数据库操作方法，重构后仅包含管理员自身数据的访问
 * 移除了对其他微服务数据表的直接访问
 * 
 * @author Baoleme Team
 * @version 2.0
 * @since 2025-01-25
 */
package org.demo.adminservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.adminservice.pojo.Admin;

import java.util.List;

/**
 * 管理员数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础的CRUD操作
 * 重构后仅定义管理员表相关的数据访问方法
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据管理员ID查询管理员信息（包含逻辑删除检查）
     * 
     * @param adminId 管理员ID
     * @return Admin 管理员实体
     */
    @Select("SELECT * FROM admin WHERE id = #{adminId} AND (deleted IS NULL OR deleted = 0)")
    Admin selectByIdWithDeleted(@Param("adminId") Long adminId);

    /**
     * 根据管理员ID更新密码
     * 
     * @param adminId 管理员ID
     * @param password 加密后的密码
     * @param updatedAt 更新时间
     * @return int 更新结果
     */
    @Update("UPDATE admin SET password = #{password}, updated_at = #{updatedAt} WHERE id = #{adminId} AND (deleted IS NULL OR deleted = 0)")
    int updatePasswordById(@Param("adminId") Long adminId, @Param("password") String password, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    /**
     * 检查管理员是否存在（排除已删除的）
     * 
     * @param adminId 管理员ID
     * @return int 存在数量
     */
    @Select("SELECT COUNT(*) FROM admin WHERE id = #{adminId} AND (deleted IS NULL OR deleted = 0)")
    int countByIdWithDeleted(@Param("adminId") Long adminId);

    /**
     * 获取所有管理员列表（排除已删除的）
     * 
     * @return List<Admin> 管理员列表
     */
    @Select("SELECT * FROM admin WHERE (deleted IS NULL OR deleted = 0) ORDER BY created_at DESC")
    List<Admin> selectAllActive();

    /**
     * 逻辑删除管理员
     * 
     * @param adminId 管理员ID
     * @param updatedAt 更新时间
     * @return int 删除结果
     */
    @Update("UPDATE admin SET deleted = 1, updated_at = #{updatedAt} WHERE id = #{adminId}")
    int logicalDeleteById(@Param("adminId") Long adminId, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    /**
     * 恢复已删除的管理员
     * 
     * @param adminId 管理员ID
     * @param updatedAt 更新时间
     * @return int 恢复结果
     */
    @Update("UPDATE admin SET deleted = 0, updated_at = #{updatedAt} WHERE id = #{adminId}")
    int restoreById(@Param("adminId") Long adminId, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    /**
     * 根据创建时间范围查询管理员
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List<Admin> 管理员列表
     */
    @Select("SELECT * FROM admin WHERE (deleted IS NULL OR deleted = 0) " +
            "AND created_at >= #{startTime} AND created_at <= #{endTime} " +
            "ORDER BY created_at DESC")
    List<Admin> selectByCreatedTimeRange(@Param("startTime") java.time.LocalDateTime startTime, 
                                         @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 统计活跃管理员数量
     * 
     * @return int 活跃管理员数量
     */
    @Select("SELECT COUNT(*) FROM admin WHERE (deleted IS NULL OR deleted = 0)")
    int countActiveAdmins();

    /**
     * 批量查询管理员信息
     * 
     * @param adminIds 管理员ID列表
     * @return List<Admin> 管理员列表
     */
    @Select("<script>" +
            "SELECT * FROM admin WHERE (deleted IS NULL OR deleted = 0) " +
            "AND id IN " +
            "<foreach collection='adminIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "ORDER BY created_at DESC" +
            "</script>")
    List<Admin> selectByIds(@Param("adminIds") List<Long> adminIds);
}