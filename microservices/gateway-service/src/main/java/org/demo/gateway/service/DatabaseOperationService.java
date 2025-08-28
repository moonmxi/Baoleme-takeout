/**
 * 数据库操作服务接口
 * 提供统一的数据库操作接口，支持跨数据库的CRUD操作
 * 
 * 主要功能：
 * 1. 统一的CRUD操作接口
 * 2. 支持复杂查询和条件查询
 * 3. 支持批量操作
 * 4. 支持事务管理
 * 5. 自动数据源路由
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.service;

import java.util.List;
import java.util.Map;

/**
 * 数据库操作服务接口
 * 定义统一的数据库操作规范
 */
public interface DatabaseOperationService {

    /**
     * 根据ID查询单条记录
     * 
     * @param tableName 表名
     * @param id 主键ID
     * @return 查询结果，如果不存在返回null
     * @throws RuntimeException 查询异常
     */
    Map<String, Object> selectById(String tableName, Long id);

    /**
     * 根据条件查询记录列表
     * 
     * @param tableName 表名
     * @param conditions 查询条件
     * @return 查询结果列表
     * @throws RuntimeException 查询异常
     */
    List<Map<String, Object>> selectByConditions(String tableName, Map<String, Object> conditions);

    /**
     * 分页查询记录列表
     * 
     * @param tableName 表名
     * @param conditions 查询条件
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 查询结果列表
     * @throws RuntimeException 查询异常
     */
    List<Map<String, Object>> selectByPage(String tableName, Map<String, Object> conditions, int page, int pageSize);

    /**
     * 查询记录总数
     * 
     * @param tableName 表名
     * @param conditions 查询条件
     * @return 记录总数
     * @throws RuntimeException 查询异常
     */
    long countByConditions(String tableName, Map<String, Object> conditions);

    /**
     * 插入单条记录
     * 
     * @param tableName 表名
     * @param data 插入数据
     * @return 插入成功的记录数
     * @throws RuntimeException 插入异常
     */
    int insert(String tableName, Map<String, Object> data);

    /**
     * 批量插入记录
     * 
     * @param tableName 表名
     * @param dataList 插入数据列表
     * @return 插入成功的记录数
     * @throws RuntimeException 插入异常
     */
    int batchInsert(String tableName, List<Map<String, Object>> dataList);

    /**
     * 根据ID更新记录
     * 
     * @param tableName 表名
     * @param id 主键ID
     * @param data 更新数据
     * @return 更新成功的记录数
     * @throws RuntimeException 更新异常
     */
    int updateById(String tableName, Long id, Map<String, Object> data);

    /**
     * 根据条件更新记录
     * 
     * @param tableName 表名
     * @param conditions 更新条件
     * @param data 更新数据
     * @return 更新成功的记录数
     * @throws RuntimeException 更新异常
     */
    int updateByConditions(String tableName, Map<String, Object> conditions, Map<String, Object> data);

    /**
     * 根据ID删除记录
     * 
     * @param tableName 表名
     * @param id 主键ID
     * @return 删除成功的记录数
     * @throws RuntimeException 删除异常
     */
    int deleteById(String tableName, Long id);

    /**
     * 根据条件删除记录
     * 
     * @param tableName 表名
     * @param conditions 删除条件
     * @return 删除成功的记录数
     * @throws RuntimeException 删除异常
     */
    int deleteByConditions(String tableName, Map<String, Object> conditions);

    /**
     * 批量删除记录
     * 
     * @param tableName 表名
     * @param ids 主键ID列表
     * @return 删除成功的记录数
     * @throws RuntimeException 删除异常
     */
    int batchDeleteByIds(String tableName, List<Long> ids);

    /**
     * 执行自定义SQL查询
     * 
     * @param sql SQL语句
     * @param params 参数
     * @return 查询结果列表
     * @throws RuntimeException 查询异常
     */
    List<Map<String, Object>> executeQuery(String sql, Object... params);

    /**
     * 执行自定义SQL更新
     * 
     * @param sql SQL语句
     * @param params 参数
     * @return 影响的记录数
     * @throws RuntimeException 更新异常
     */
    int executeUpdate(String sql, Object... params);

    /**
     * 检查表是否存在
     * 
     * @param tableName 表名
     * @return 是否存在
     * @throws RuntimeException 检查异常
     */
    boolean tableExists(String tableName);

    /**
     * 获取表结构信息
     * 
     * @param tableName 表名
     * @return 表结构信息
     * @throws RuntimeException 查询异常
     */
    List<Map<String, Object>> getTableStructure(String tableName);

    /**
     * 获取数据源健康状态
     * 
     * @return 健康状态信息
     */
    Map<String, Object> getDataSourceHealth();
}