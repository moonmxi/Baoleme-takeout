/**
 * 动态数据源类
 * 实现数据源的动态切换，根据上下文选择不同的数据库连接
 * 
 * 功能特性：
 * 1. 支持多数据源动态切换
 * 2. 线程安全的数据源上下文管理
 * 3. 自动数据源路由
 * 4. 连接池监控和管理
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源实现类
 * 继承AbstractRoutingDataSource，实现数据源的动态路由
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 数据源上下文持有者
     * 使用ThreadLocal确保线程安全
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前线程的数据源
     * 
     * @param dataSourceKey 数据源键名
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    public static void setDataSourceKey(String dataSourceKey) {
        if (dataSourceKey == null || dataSourceKey.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源键名不能为空");
        }
        
        CONTEXT_HOLDER.set(dataSourceKey);
        log.debug("切换数据源到: {}", dataSourceKey);
    }

    /**
     * 获取当前线程的数据源键名
     * 
     * @return String 数据源键名
     */
    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除当前线程的数据源设置
     * 释放ThreadLocal资源，防止内存泄漏
     */
    public static void clearDataSourceKey() {
        String currentKey = CONTEXT_HOLDER.get();
        CONTEXT_HOLDER.remove();
        log.debug("清除数据源上下文: {}", currentKey);
    }

    /**
     * 根据表名自动设置数据源
     * 
     * @param tableName 表名
     */
    public static void setDataSourceByTable(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        
        String dataSourceKey = getDataSourceKeyByTable(tableName);
        setDataSourceKey(dataSourceKey);
    }

    /**
     * 根据表名获取对应的数据源键名
     * 
     * @param tableName 表名
     * @return String 数据源键名
     */
    private static String getDataSourceKeyByTable(String tableName) {
        switch (tableName.toLowerCase()) {
            // 用户相关表
            case "user":
            case "favorite":
            case "browse_history":
                return "user";
            
            // 商家相关表
            case "merchant":
            case "store":
            case "product":
            case "sales":
                return "merchant";
            
            // 骑手相关表
            case "rider":
                return "rider";
            
            // 管理员相关表
            case "admin":
                return "admin";
            
            // 网关相关表（订单、购物车、评价等）
            case "order":
            case "order_item":
            case "review":
            case "cart":
            case "coupon":
            case "message":
            case "sync_log":
            case "message_log":
                return "gateway";
            
            default:
                log.warn("未知表名: {}，使用默认数据源", tableName);
                return "gateway";
        }
    }

    /**
     * 确定当前查找键
     * 这是AbstractRoutingDataSource的核心方法，用于确定使用哪个数据源
     * 
     * @return Object 数据源查找键
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceKey = getDataSourceKey();
        
        // 如果没有设置数据源，使用默认的gateway数据源
        if (dataSourceKey == null) {
            dataSourceKey = "gateway";
            log.debug("未设置数据源，使用默认数据源: {}", dataSourceKey);
        }
        
        log.debug("当前使用数据源: {}", dataSourceKey);
        return dataSourceKey;
    }

    /**
     * 数据源上下文管理器
     * 提供便捷的数据源切换和自动清理功能
     */
    public static class DataSourceContextManager {
        
        /**
         * 执行数据库操作并自动管理数据源上下文
         * 
         * @param dataSourceKey 数据源键名
         * @param operation 数据库操作
         * @param <T> 返回值类型
         * @return T 操作结果
         * @throws Exception 操作异常
         */
        public static <T> T executeWithDataSource(String dataSourceKey, DataSourceOperation<T> operation) throws Exception {
            try {
                setDataSourceKey(dataSourceKey);
                return operation.execute();
            } finally {
                clearDataSourceKey();
            }
        }

        /**
         * 根据表名执行数据库操作并自动管理数据源上下文
         * 
         * @param tableName 表名
         * @param operation 数据库操作
         * @param <T> 返回值类型
         * @return T 操作结果
         * @throws Exception 操作异常
         */
        public static <T> T executeWithTable(String tableName, DataSourceOperation<T> operation) throws Exception {
            try {
                setDataSourceByTable(tableName);
                return operation.execute();
            } finally {
                clearDataSourceKey();
            }
        }

        /**
         * 数据源操作接口
         * 
         * @param <T> 返回值类型
         */
        @FunctionalInterface
        public interface DataSourceOperation<T> {
            /**
             * 执行数据库操作
             * 
             * @return T 操作结果
             * @throws Exception 操作异常
             */
            T execute() throws Exception;
        }
    }

    /**
     * 数据源健康检查
     * 
     * @param dataSourceKey 数据源键名
     * @return boolean 数据源是否健康
     */
    public boolean isDataSourceHealthy(String dataSourceKey) {
        try {
            setDataSourceKey(dataSourceKey);
            // 尝试获取连接来检查数据源健康状态
            getConnection().close();
            return true;
        } catch (Exception e) {
            log.error("数据源健康检查失败 - {}: {}", dataSourceKey, e.getMessage());
            return false;
        } finally {
            clearDataSourceKey();
        }
    }

    /**
     * 获取所有数据源的健康状态
     * 
     * @return Map<String, Boolean> 数据源健康状态映射
     */
    public java.util.Map<String, Boolean> getAllDataSourceHealth() {
        java.util.Map<String, Boolean> healthMap = new java.util.HashMap<>();
        
        // 检查所有已配置的数据源
        String[] dataSources = {"user", "merchant", "rider", "admin", "gateway"};
        
        for (String dataSource : dataSources) {
            healthMap.put(dataSource, isDataSourceHealthy(dataSource));
        }
        
        return healthMap;
    }

    /**
     * 获取当前活跃的数据源统计信息
     * 
     * @return Map<String, Object> 统计信息
     */
    public java.util.Map<String, Object> getDataSourceStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        try {
            stats.put("currentDataSource", getDataSourceKey());
            stats.put("defaultDataSource", "gateway");
            stats.put("availableDataSources", java.util.Arrays.asList("user", "merchant", "rider", "admin", "gateway"));
            stats.put("healthStatus", getAllDataSourceHealth());
            stats.put("timestamp", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (Exception e) {
            log.error("获取数据源统计信息失败: {}", e.getMessage(), e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
}