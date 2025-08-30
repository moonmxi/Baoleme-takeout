/**
 * 数据库操作服务实现类
 * 实现统一的数据库操作接口，支持跨数据库的CRUD操作
 * 
 * 主要功能：
 * 1. 基于JdbcTemplate的数据库操作
 * 2. 动态数据源路由
 * 3. SQL语句动态生成
 * 4. 异常处理和日志记录
 * 5. 数据源健康检查
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.config.DynamicDataSourceConfig.DynamicDataSource;
import org.demo.gateway.service.DatabaseOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库操作服务实现
 * 提供统一的数据库操作功能
 */
@Slf4j
@Service
public class DatabaseOperationServiceImpl implements DatabaseOperationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dynamicDataSource;

    /**
     * 根据ID查询单条记录
     */
    @Override
    public Map<String, Object> selectById(String tableName, Long id) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            log.debug("执行查询SQL: {}, 参数: {}", sql, id);
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, id);
            return results.isEmpty() ? null : results.get(0);
            
        } catch (DataAccessException e) {
            log.error("根据ID查询记录失败: 表={}, ID={}", tableName, id, e);
            throw new RuntimeException("查询记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 根据条件查询记录列表
     * 支持多种查询操作符：
     * - 字段名: 等值查询
     * - 字段名_gte: 大于等于
     * - 字段名_lte: 小于等于
     * - 字段名_gt: 大于
     * - 字段名_lt: 小于
     * - 字段名_like: 模糊查询（LIKE %value%）
     */
    @Override
    public List<Map<String, Object>> selectByConditions(String tableName, Map<String, Object> conditions) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            StringBuilder sql = new StringBuilder("SELECT * FROM `" + tableName + "`");
            List<Object> params = new ArrayList<>();
            
            if (conditions != null && !conditions.isEmpty()) {
                sql.append(" WHERE ");
                List<String> whereClauses = new ArrayList<>();
                
                for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    // 处理不同的查询操作符
                    if (key.endsWith("_gte")) {
                        // 大于等于
                        String fieldName = key.substring(0, key.length() - 4);
                        whereClauses.add(fieldName + " >= ?");
                        params.add(value);
                    } else if (key.endsWith("_lte")) {
                        // 小于等于
                        String fieldName = key.substring(0, key.length() - 4);
                        whereClauses.add(fieldName + " <= ?");
                        params.add(value);
                    } else if (key.endsWith("_like")) {
                        // 模糊查询
                        String fieldName = key.substring(0, key.length() - 5);
                        whereClauses.add(fieldName + " LIKE ?");
                        params.add("%" + value + "%");
                    } else if (key.endsWith("_gt")) {
                        // 大于
                        String fieldName = key.substring(0, key.length() - 3);
                        whereClauses.add(fieldName + " > ?");
                        params.add(value);
                    } else if (key.endsWith("_lt")) {
                        // 小于
                        String fieldName = key.substring(0, key.length() - 3);
                        whereClauses.add(fieldName + " < ?");
                        params.add(value);
                    } else {
                        // 等值查询
                        whereClauses.add(key + " = ?");
                        params.add(value);
                    }
                }
                
                sql.append(String.join(" AND ", whereClauses));
            }
            
            log.debug("执行查询SQL: {}, 参数: {}", sql.toString(), params);
            return jdbcTemplate.queryForList(sql.toString(), params.toArray());
            
        } catch (DataAccessException e) {
            log.error("根据条件查询记录失败: 表={}, 条件={}", tableName, conditions, e);
            throw new RuntimeException("查询记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 分页查询记录列表
     * 支持多种查询操作符（与selectByConditions保持一致）
     * 包括模糊查询支持
     */
    @Override
    public List<Map<String, Object>> selectByPage(String tableName, Map<String, Object> conditions, int page, int pageSize) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            StringBuilder sql = new StringBuilder("SELECT * FROM `" + tableName + "`");
            List<Object> params = new ArrayList<>();
            
            if (conditions != null && !conditions.isEmpty()) {
                sql.append(" WHERE ");
                List<String> whereClauses = new ArrayList<>();
                
                for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    // 处理不同的查询操作符（与selectByConditions保持一致）
                    if (key.endsWith("_gte")) {
                        // 大于等于
                        String fieldName = key.substring(0, key.length() - 4);
                        whereClauses.add(fieldName + " >= ?");
                        params.add(value);
                    } else if (key.endsWith("_lte")) {
                        // 小于等于
                        String fieldName = key.substring(0, key.length() - 4);
                        whereClauses.add(fieldName + " <= ?");
                        params.add(value);
                    } else if (key.endsWith("_like")) {
                        // 模糊查询
                        String fieldName = key.substring(0, key.length() - 5);
                        whereClauses.add(fieldName + " LIKE ?");
                        params.add("%" + value + "%");
                    } else if (key.endsWith("_gt")) {
                        // 大于
                        String fieldName = key.substring(0, key.length() - 3);
                        whereClauses.add(fieldName + " > ?");
                        params.add(value);
                    } else if (key.endsWith("_lt")) {
                        // 小于
                        String fieldName = key.substring(0, key.length() - 3);
                        whereClauses.add(fieldName + " < ?");
                        params.add(value);
                    } else {
                        // 等值查询
                        whereClauses.add(key + " = ?");
                        params.add(value);
                    }
                }
                
                sql.append(String.join(" AND ", whereClauses));
            }
            
            // 添加分页
            sql.append(" LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((page - 1) * pageSize);
            
            log.debug("执行分页查询SQL: {}, 参数: {}", sql.toString(), params);
            return jdbcTemplate.queryForList(sql.toString(), params.toArray());
            
        } catch (DataAccessException e) {
            log.error("分页查询记录失败: 表={}, 条件={}, 页码={}, 页大小={}", tableName, conditions, page, pageSize, e);
            throw new RuntimeException("分页查询失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 查询记录总数
     * 支持多种查询操作符（与selectByConditions保持一致）
     */
    @Override
    public long countByConditions(String tableName, Map<String, Object> conditions) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `" + tableName + "`");
            List<Object> params = new ArrayList<>();
            
            if (conditions != null && !conditions.isEmpty()) {
                sql.append(" WHERE ");
                List<String> whereClauses = new ArrayList<>();
                
                for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    // 处理不同的查询操作符（与selectByConditions保持一致）
                    if (key.endsWith("_gte")) {
                        // 大于等于
                        String fieldName = key.substring(0, key.length() - 4);
                        whereClauses.add(fieldName + " >= ?");
                        params.add(value);
                    } else if (key.endsWith("_lte")) {
                        // 小于等于
                        String fieldName = key.substring(0, key.length() - 4);
                        whereClauses.add(fieldName + " <= ?");
                        params.add(value);
                    } else if (key.endsWith("_like")) {
                        // 模糊查询
                        String fieldName = key.substring(0, key.length() - 5);
                        whereClauses.add(fieldName + " LIKE ?");
                        params.add("%" + value + "%");
                    } else if (key.endsWith("_gt")) {
                        // 大于
                        String fieldName = key.substring(0, key.length() - 3);
                        whereClauses.add(fieldName + " > ?");
                        params.add(value);
                    } else if (key.endsWith("_lt")) {
                        // 小于
                        String fieldName = key.substring(0, key.length() - 3);
                        whereClauses.add(fieldName + " < ?");
                        params.add(value);
                    } else {
                        // 等值查询
                        whereClauses.add(key + " = ?");
                        params.add(value);
                    }
                }
                
                sql.append(String.join(" AND ", whereClauses));
            }
            
            log.debug("执行计数SQL: {}, 参数: {}", sql.toString(), params);
            Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
            return count != null ? count : 0L;
            
        } catch (DataAccessException e) {
            log.error("查询记录总数失败: 表={}, 条件={}", tableName, conditions, e);
            throw new RuntimeException("查询记录总数失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 插入单条记录
     */
    @Override
    @Transactional
    public int insert(String tableName, Map<String, Object> data) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            if (data == null || data.isEmpty()) {
                throw new IllegalArgumentException("插入数据不能为空");
            }
            
            // 添加创建时间
            data.put("created_at", LocalDateTime.now());
            data.put("updated_at", LocalDateTime.now());
            
            List<String> columns = new ArrayList<>(data.keySet());
            List<Object> values = new ArrayList<>(data.values());
            
            String sql = "INSERT INTO " + tableName + " (" + 
                        String.join(", ", columns) + ") VALUES (" + 
                        columns.stream().map(c -> "?").collect(Collectors.joining(", ")) + ")";
            
            log.debug("执行插入SQL: {}, 参数: {}", sql, values);
            return jdbcTemplate.update(sql, values.toArray());
            
        } catch (DataAccessException e) {
            log.error("插入记录失败: 表={}, 数据={}", tableName, data, e);
            throw new RuntimeException("插入记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 批量插入记录
     */
    @Override
    @Transactional
    public int batchInsert(String tableName, List<Map<String, Object>> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }
        
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            int totalInserted = 0;
            for (Map<String, Object> data : dataList) {
                totalInserted += insert(tableName, data);
            }
            
            log.info("批量插入完成: 表={}, 插入记录数={}", tableName, totalInserted);
            return totalInserted;
            
        } catch (Exception e) {
            log.error("批量插入记录失败: 表={}, 数据量={}", tableName, dataList.size(), e);
            throw new RuntimeException("批量插入记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 根据ID更新记录
     */
    @Override
    @Transactional
    public int updateById(String tableName, Long id, Map<String, Object> data) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            if (data == null || data.isEmpty()) {
                throw new IllegalArgumentException("更新数据不能为空");
            }
            
            // 添加更新时间
            data.put("updated_at", LocalDateTime.now());
            
            List<String> setClauses = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                setClauses.add(entry.getKey() + " = ?");
                params.add(entry.getValue());
            }
            
            params.add(id);
            
            String sql = "UPDATE " + tableName + " SET " + String.join(", ", setClauses) + " WHERE id = ?";
            
            log.debug("执行更新SQL: {}, 参数: {}", sql, params);
            return jdbcTemplate.update(sql, params.toArray());
            
        } catch (DataAccessException e) {
            log.error("根据ID更新记录失败: 表={}, ID={}, 数据={}", tableName, id, data, e);
            throw new RuntimeException("更新记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 根据条件更新记录
     */
    @Override
    @Transactional
    public int updateByConditions(String tableName, Map<String, Object> conditions, Map<String, Object> data) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            if (data == null || data.isEmpty()) {
                throw new IllegalArgumentException("更新数据不能为空");
            }
            
            if (conditions == null || conditions.isEmpty()) {
                throw new IllegalArgumentException("更新条件不能为空");
            }
            
            // 添加更新时间
            data.put("updated_at", LocalDateTime.now());
            
            List<String> setClauses = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                setClauses.add(entry.getKey() + " = ?");
                params.add(entry.getValue());
            }
            
            List<String> whereClauses = new ArrayList<>();
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                whereClauses.add(entry.getKey() + " = ?");
                params.add(entry.getValue());
            }
            
            String sql = "UPDATE " + tableName + " SET " + String.join(", ", setClauses) + 
                        " WHERE " + String.join(" AND ", whereClauses);
            
            log.debug("执行条件更新SQL: {}, 参数: {}", sql, params);
            return jdbcTemplate.update(sql, params.toArray());
            
        } catch (DataAccessException e) {
            log.error("根据条件更新记录失败: 表={}, 条件={}, 数据={}", tableName, conditions, data, e);
            throw new RuntimeException("更新记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 根据ID删除记录
     */
    @Override
    public int deleteById(String tableName, Long id) {
        try {
            log.debug("开始删除操作: 表={}, ID={}", tableName, id);
            DynamicDataSource.setDataSourceByTable(tableName);
            log.debug("已设置数据源: {}", DynamicDataSource.getCurrentDataSource());
            
            String sql = "DELETE FROM `" + tableName + "` WHERE id = ?";
            
            log.debug("执行删除SQL: {}, 参数: {}", sql, id);
            int result = jdbcTemplate.update(sql, id);
            log.debug("删除操作完成，影响行数: {}", result);
            return result;
            
        } catch (DataAccessException e) {
            log.error("根据ID删除记录失败: 表={}, ID={}", tableName, id, e);
            throw new RuntimeException("删除记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 根据条件删除记录
     */
    @Override
    @Transactional
    public int deleteByConditions(String tableName, Map<String, Object> conditions) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            if (conditions == null || conditions.isEmpty()) {
                throw new IllegalArgumentException("删除条件不能为空");
            }
            
            List<String> whereClauses = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                whereClauses.add(entry.getKey() + " = ?");
                params.add(entry.getValue());
            }
            
            String sql = "DELETE FROM " + tableName + " WHERE " + String.join(" AND ", whereClauses);
            
            log.debug("执行条件删除SQL: {}, 参数: {}", sql, params);
            return jdbcTemplate.update(sql, params.toArray());
            
        } catch (DataAccessException e) {
            log.error("根据条件删除记录失败: 表={}, 条件={}", tableName, conditions, e);
            throw new RuntimeException("删除记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 批量删除记录
     */
    @Override
    @Transactional
    public int batchDeleteByIds(String tableName, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(", "));
            String sql = "DELETE FROM " + tableName + " WHERE id IN (" + placeholders + ")";
            
            log.debug("执行批量删除SQL: {}, 参数: {}", sql, ids);
            return jdbcTemplate.update(sql, ids.toArray());
            
        } catch (DataAccessException e) {
            log.error("批量删除记录失败: 表={}, IDs={}", tableName, ids, e);
            throw new RuntimeException("批量删除记录失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 执行自定义SQL查询
     */
    @Override
    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        try {
            log.debug("执行自定义查询SQL: {}, 参数: {}", sql, Arrays.toString(params));
            return jdbcTemplate.queryForList(sql, params);
            
        } catch (DataAccessException e) {
            log.error("执行自定义查询失败: SQL={}, 参数={}", sql, Arrays.toString(params), e);
            throw new RuntimeException("执行查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行自定义SQL更新
     */
    @Override
    @Transactional
    public int executeUpdate(String sql, Object... params) {
        try {
            log.debug("执行自定义更新SQL: {}, 参数: {}", sql, Arrays.toString(params));
            return jdbcTemplate.update(sql, params);
            
        } catch (DataAccessException e) {
            log.error("执行自定义更新失败: SQL={}, 参数={}", sql, Arrays.toString(params), e);
            throw new RuntimeException("执行更新失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查表是否存在
     */
    @Override
    public boolean tableExists(String tableName) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ? AND table_schema = DATABASE()";
            
            log.debug("检查表是否存在: {}", tableName);
            Long count = jdbcTemplate.queryForObject(sql, Long.class, tableName);
            return count != null && count > 0;
            
        } catch (DataAccessException e) {
            log.error("检查表是否存在失败: 表={}", tableName, e);
            return false;
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 获取表结构信息
     */
    @Override
    public List<Map<String, Object>> getTableStructure(String tableName) {
        try {
            DynamicDataSource.setDataSourceByTable(tableName);
            
            String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT " +
                        "FROM information_schema.columns WHERE table_name = ? AND table_schema = DATABASE() " +
                        "ORDER BY ORDINAL_POSITION";
            
            log.debug("获取表结构信息: {}", tableName);
            return jdbcTemplate.queryForList(sql, tableName);
            
        } catch (DataAccessException e) {
            log.error("获取表结构信息失败: 表={}", tableName, e);
            throw new RuntimeException("获取表结构信息失败: " + e.getMessage(), e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }

    /**
     * 获取数据源健康状态
     */
    @Override
    public Map<String, Object> getDataSourceHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try (Connection connection = dynamicDataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            health.put("status", "UP");
            health.put("database", metaData.getDatabaseProductName());
            health.put("version", metaData.getDatabaseProductVersion());
            health.put("driver", metaData.getDriverName());
            health.put("driverVersion", metaData.getDriverVersion());
            health.put("url", metaData.getURL());
            health.put("username", metaData.getUserName());
            health.put("timestamp", LocalDateTime.now());
            
            log.debug("数据源健康检查通过");
            
        } catch (SQLException e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());
            
            log.error("数据源健康检查失败", e);
        }
        
        return health;
    }
}