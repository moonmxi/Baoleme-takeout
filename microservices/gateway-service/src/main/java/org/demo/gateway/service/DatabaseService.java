/**
 * 数据库操作服务接口
 * 提供统一的跨数据库操作API，支持对不同微服务数据库的CRUD操作
 * 
 * 主要功能：
 * 1. 统一数据库操作接口 - 提供标准化的CRUD操作
 * 2. 自动数据源路由 - 根据表名自动选择正确的数据库
 * 3. 事务管理 - 支持跨数据库事务操作
 * 4. 批量操作 - 支持批量插入、更新、删除
 * 5. 条件查询 - 支持复杂的条件查询
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
 * 定义跨数据库操作的标准方法
 */
public interface DatabaseService {

    /**
     * 根据ID查询单条记录
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @return Map<String, Object> 查询结果
     * @throws IllegalArgumentException 当表名或ID为空时抛出
     */
    Map<String, Object> selectById(String tableName, Long id);

    /**
     * 根据条件查询多条记录
     * 
     * @param tableName 表名
     * @param condition 查询条件Map，key为字段名，value为字段值
     *                  - 非字符串类型字段：使用精确匹配（=）
     *                  - 字符串类型字段（varchar等）：使用LIKE进行模糊匹配
     * @return List<Map<String, Object>> 查询结果列表
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    List<Map<String, Object>> selectByConditions(String tableName, Map<String, Object> condition);

    /**
     * 根据条件查询多条记录（兼容旧版本）
     * 
     * @param tableName 表名
     * @param conditions 查询条件
     * @return List<Map<String, Object>> 查询结果列表
     * @throws IllegalArgumentException 当表名为空时抛出
     * @deprecated 使用 selectByConditions(String, Map<String, Object>) 替代
     */
    @Deprecated
    default List<Map<String, Object>> selectByConditionsOld(String tableName, Map<String, Object> conditions) {
        return selectByConditions(tableName, conditions);
    }

    /**
     * 查询所有记录（分页）
     * 
     * @param tableName 表名
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return List<Map<String, Object>> 查询结果列表
     * @throws IllegalArgumentException 当表名为空或分页参数无效时抛出
     */
    List<Map<String, Object>> selectAll(String tableName, int page, int pageSize);

    /**
     * 插入单条记录
     * 
     * @param tableName 表名
     * @param data 插入数据
     * @return Long 插入记录的ID
     * @throws IllegalArgumentException 当表名或数据为空时抛出
     */
    Long insert(String tableName, Map<String, Object> data);

    /**
     * 批量插入记录
     * 
     * @param tableName 表名
     * @param dataList 插入数据列表
     * @return int 插入成功的记录数
     * @throws IllegalArgumentException 当表名或数据列表为空时抛出
     */
    int batchInsert(String tableName, List<Map<String, Object>> dataList);

    /**
     * 根据ID更新记录
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @param data 更新数据
     * @return int 更新影响的行数
     * @throws IllegalArgumentException 当表名、ID或数据为空时抛出
     */
    int updateById(String tableName, Long id, Map<String, Object> data);

    /**
     * 根据条件更新记录
     * 
     * @param tableName 表名
     * @param condition 更新条件Map，key为字段名，value为字段值
     *                  - 非字符串类型字段：使用精确匹配（=）
     *                  - 字符串类型字段（varchar等）：使用LIKE进行模糊匹配
     * @param data 更新数据
     * @return int 更新影响的行数
     * @throws IllegalArgumentException 当表名、条件或数据为空时抛出
     */
    int updateByConditions(String tableName, Map<String, Object> condition, Map<String, Object> data);

    /**
     * 批量更新记录
     * 
     * @param tableName 表名
     * @param dataList 更新数据列表（必须包含ID字段）
     * @return int 更新影响的行数
     * @throws IllegalArgumentException 当表名或数据列表为空时抛出
     */
    int batchUpdate(String tableName, List<Map<String, Object>> dataList);

    /**
     * 根据ID删除记录
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @return int 删除影响的行数
     * @throws IllegalArgumentException 当表名或ID为空时抛出
     */
    int deleteById(String tableName, Long id);

    /**
     * 根据条件删除记录
     * 
     * @param tableName 表名
     * @param condition 删除条件Map，key为字段名，value为字段值
     *                  - 非字符串类型字段：使用精确匹配（=）
     *                  - 字符串类型字段（varchar等）：使用LIKE进行模糊匹配
     * @return int 删除影响的行数
     * @throws IllegalArgumentException 当表名或条件为空时抛出
     */
    int deleteByConditions(String tableName, Map<String, Object> condition);

    /**
     * 批量删除记录
     * 
     * @param tableName 表名
     * @param ids ID列表
     * @return int 删除影响的行数
     * @throws IllegalArgumentException 当表名或ID列表为空时抛出
     */
    int batchDelete(String tableName, List<Long> ids);

    /**
     * 统计记录总数
     * 
     * @param tableName 表名
     * @return long 记录总数
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    long count(String tableName);

    /**
     * 根据条件统计记录数
     * 
     * @param tableName 表名
     * @param condition 统计条件Map，key为字段名，value为字段值
     *                  - 非字符串类型字段：使用精确匹配（=）
     *                  - 字符串类型字段（varchar等）：使用LIKE进行模糊匹配
     * @return long 记录数
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    long countByConditions(String tableName, Map<String, Object> condition);

    /**
     * 执行自定义SQL查询
     * 
     * @param sql SQL语句
     * @param params 参数
     * @return List<Map<String, Object>> 查询结果
     * @throws IllegalArgumentException 当SQL为空时抛出
     */
    List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params);

    /**
     * 执行自定义SQL更新
     * 
     * @param sql SQL语句
     * @param params 参数
     * @return int 影响行数
     * @throws IllegalArgumentException 当SQL为空时抛出
     */
    int executeUpdate(String sql, Map<String, Object> params);

    /**
     * 检查表是否存在
     * 
     * @param tableName 表名
     * @return boolean 表是否存在
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    boolean tableExists(String tableName);

    /**
     * 获取表结构信息
     * 
     * @param tableName 表名
     * @return List<Map<String, Object>> 表结构信息
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    List<Map<String, Object>> getTableStructure(String tableName);

    /**
     * 获取数据库连接状态
     * 
     * @param dataSourceKey 数据源键名
     * @return boolean 连接是否正常
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    boolean isConnectionHealthy(String dataSourceKey);

    /**
     * 获取所有数据源的健康状态
     * 
     * @return Map<String, Boolean> 数据源健康状态映射
     */
    Map<String, Boolean> getAllDataSourceHealth();

    /**
     * 开始事务
     * 
     * @param dataSourceKey 数据源键名
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    void beginTransaction(String dataSourceKey);

    /**
     * 提交事务
     * 
     * @param dataSourceKey 数据源键名
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    void commitTransaction(String dataSourceKey);

    /**
     * 回滚事务
     * 
     * @param dataSourceKey 数据源键名
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    void rollbackTransaction(String dataSourceKey);

    /**
     * 执行跨数据库事务操作
     * 
     * @param operation 事务操作
     * @param <T> 返回值类型
     * @return T 操作结果
     * @throws Exception 操作异常
     */
    <T> T executeInTransaction(TransactionOperation<T> operation) throws Exception;

    /**
     * 事务操作接口
     * 
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    interface TransactionOperation<T> {
        /**
         * 执行事务操作
         * 
         * @return T 操作结果
         * @throws Exception 操作异常
         */
        T execute() throws Exception;
    }

    /**
     * 数据库操作结果类
     */
    class DatabaseResult {
        private boolean success;
        private String message;
        private Object data;
        private int affectedRows;
        private long timestamp;

        public DatabaseResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public DatabaseResult(boolean success, String message, Object data) {
            this(success, message);
            this.data = data;
        }

        public DatabaseResult(boolean success, String message, int affectedRows) {
            this(success, message);
            this.affectedRows = affectedRows;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public int getAffectedRows() { return affectedRows; }
        public void setAffectedRows(int affectedRows) { this.affectedRows = affectedRows; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}