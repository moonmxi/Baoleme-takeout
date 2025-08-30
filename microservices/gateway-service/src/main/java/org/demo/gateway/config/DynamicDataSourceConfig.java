/**
 * 动态数据源配置类
 * 实现多数据源的动态路由，根据表名自动选择正确的数据库连接
 * 
 * 主要功能：
 * 1. 配置多个数据库连接池
 * 2. 实现动态数据源路由
 * 3. 提供表名到数据源的映射
 * 4. 支持事务管理
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源配置
 * 支持多数据库的动态切换和路由
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class DynamicDataSourceConfig {

    /**
     * 默认数据源名称
     */
    @Value("${gateway.table-mapping.default-datasource:common}")
    private String defaultDataSource;
    
    /**
     * 表映射配置 - 手动初始化
     */
    private Map<String, String> tableMappingConfig = new HashMap<>();
    
    /**
     * 初始化表映射配置
     */
    private void initTableMappingConfig() {
        // 手动配置表映射关系
        tableMappingConfig.put("user", "user");
        tableMappingConfig.put("favorite", "user");
        tableMappingConfig.put("browse_history", "user");
        
        tableMappingConfig.put("merchant", "merchant");
        tableMappingConfig.put("store", "merchant");
        tableMappingConfig.put("product", "merchant");
        tableMappingConfig.put("sales", "merchant");
        
        tableMappingConfig.put("rider", "rider");
        
        tableMappingConfig.put("admin", "admin");
        
        tableMappingConfig.put("order", "common");
        tableMappingConfig.put("order_item", "common");
        tableMappingConfig.put("review", "common");
        tableMappingConfig.put("cart", "common");
        tableMappingConfig.put("coupon", "common");
        tableMappingConfig.put("user_coupon", "common");
        tableMappingConfig.put("message", "common");
        tableMappingConfig.put("sync_log", "common");
        tableMappingConfig.put("message_log", "common");
        
        log.info("表映射配置初始化完成，共 {} 个映射", tableMappingConfig.size());
    }

    /**
     * 通用数据库数据源（主数据源）
     * 
     * @return DataSource 通用数据库连接
     */
    @Bean(name = "commonDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource commonDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        log.info("通用数据库数据源配置完成");
        return dataSource;
    }

    /**
     * 用户数据库数据源
     * 
     * @return DataSource 用户数据库连接
     */
    @Bean(name = "userDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.user")
    public DataSource userDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        log.info("用户数据库数据源配置完成");
        return dataSource;
    }

    /**
     * 商家数据库数据源
     * 
     * @return DataSource 商家数据库连接
     */
    @Bean(name = "merchantDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.merchant")
    public DataSource merchantDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        log.info("商家数据库数据源配置完成");
        return dataSource;
    }

    /**
     * 骑手数据库数据源
     * 
     * @return DataSource 骑手数据库连接
     */
    @Bean(name = "riderDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.rider")
    public DataSource riderDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        log.info("骑手数据库数据源配置完成");
        return dataSource;
    }

    /**
     * 管理员数据库数据源
     * 
     * @return DataSource 管理员数据库连接
     */
    @Bean(name = "adminDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.admin")
    public DataSource adminDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        log.info("管理员数据库数据源配置完成");
        return dataSource;
    }

    /**
     * 动态数据源路由器
     * 根据当前线程上下文中的数据源标识，动态选择数据源
     * 
     * @param gatewayDataSource 网关数据源
     * @param userDataSource 用户数据源
     * @param merchantDataSource 商家数据源
     * @param riderDataSource 骑手数据源
     * @param adminDataSource 管理员数据源
     * @return DynamicDataSource 动态数据源
     */
    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource(
            DataSource commonDataSource,
            DataSource userDataSource,
            DataSource merchantDataSource,
            DataSource riderDataSource,
            DataSource adminDataSource) {
        
        // 初始化表映射配置
        initTableMappingConfig();
        
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("common", commonDataSource);
        targetDataSources.put("user", userDataSource);
        targetDataSources.put("merchant", merchantDataSource);
        targetDataSources.put("rider", riderDataSource);
        targetDataSources.put("admin", adminDataSource);
        
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(commonDataSource);
        dynamicDataSource.setTableMappingConfig(tableMappingConfig);
        dynamicDataSource.setDefaultDataSource(defaultDataSource);
        
        log.info("动态数据源配置完成，支持数据源: {}", targetDataSources.keySet());
        return dynamicDataSource;
    }

    /**
     * 动态数据源实现类
     * 继承AbstractRoutingDataSource，实现数据源的动态路由
     */
    public static class DynamicDataSource extends AbstractRoutingDataSource {
        
        /**
         * 数据源上下文持有者
         * 使用ThreadLocal确保线程安全
         */
        private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
        
        /**
         * 表映射配置
         */
        private Map<String, String> tableMappingConfig = new HashMap<>();
        
        /**
         * 默认数据源
         */
        private String defaultDataSource = "common";
        
        /**
         * 设置表映射配置
         * 
         * @param tableMappingConfig 表映射配置
         */
        public void setTableMappingConfig(Map<String, String> tableMappingConfig) {
            if (tableMappingConfig != null) {
                this.tableMappingConfig = tableMappingConfig;
                log.info("表映射配置已设置，共 {} 个映射", tableMappingConfig.size());
            }
        }
        
        /**
         * 设置默认数据源
         * 
         * @param defaultDataSource 默认数据源名称
         */
        public void setDefaultDataSource(String defaultDataSource) {
            this.defaultDataSource = defaultDataSource;
        }
        
        /**
         * 根据表名设置数据源
         * 
         * @param tableName 表名
         */
        public static void setDataSourceByTable(String tableName) {
            if (tableName == null || tableName.trim().isEmpty()) {
                log.warn("表名为空，使用默认数据源");
                CONTEXT_HOLDER.set("common");
                return;
            }
            
            // 使用配置映射来确定数据源
            String dataSourceKey = determineDataSourceByTableName(tableName.toLowerCase());
            CONTEXT_HOLDER.set(dataSourceKey);
            log.debug("根据表名 {} 设置数据源: {}", tableName, dataSourceKey);
        }
        
        /**
         * 根据表名确定数据源
         * 
         * @param tableName 表名
         * @return 数据源键名
         */
        private static String determineDataSourceByTableName(String tableName) {
            // 用户相关表
            if (tableName.equals("user") || tableName.equals("favorite") || tableName.equals("browse_history") || tableName.equals("cart")) {
                return "user";
            }
            // 商家相关表
            if (tableName.equals("merchant") || tableName.equals("store") || tableName.equals("product") || tableName.equals("sales")) {
                return "merchant";
            }
            // 骑手相关表
            if (tableName.equals("rider")) {
                return "rider";
            }
            // 管理员相关表
            if (tableName.equals("admin")) {
                return "admin";
            }
            // 默认使用common数据库（网关数据库）
            return "common";
        }
        
        /**
         * 直接设置数据源
         * 
         * @param dataSourceKey 数据源键名
         */
        public static void setDataSource(String dataSourceKey) {
            CONTEXT_HOLDER.set(dataSourceKey);
            log.debug("直接设置数据源: {}", dataSourceKey);
        }
        
        /**
         * 清除数据源设置
         */
        public static void clearDataSource() {
            CONTEXT_HOLDER.remove();
            log.debug("清除数据源设置");
        }
        
        /**
         * 获取当前数据源键名
         * 
         * @return 数据源键名
         */
        public static String getCurrentDataSource() {
            return CONTEXT_HOLDER.get();
        }
        
        /**
         * 确定当前查找键
         * 这是AbstractRoutingDataSource的核心方法
         * 
         * @return 数据源键名
         */
        @Override
        protected Object determineCurrentLookupKey() {
            String dataSourceKey = CONTEXT_HOLDER.get();
            if (dataSourceKey == null) {
                dataSourceKey = defaultDataSource;
                log.debug("未设置数据源，使用默认数据源: {}", dataSourceKey);
            }
            return dataSourceKey;
        }
        
        /**
         * 根据表名获取数据源键名
         * 
         * @param tableName 表名
         * @return 数据源键名
         */
        private String getDataSourceKeyByTable(String tableName) {
            if (tableMappingConfig.containsKey(tableName)) {
                return tableMappingConfig.get(tableName);
            }
            
            // 如果没有找到精确匹配，尝试前缀匹配
            for (Map.Entry<String, String> entry : tableMappingConfig.entrySet()) {
                if (tableName.startsWith(entry.getKey())) {
                    log.debug("表名 {} 通过前缀匹配到数据源: {}", tableName, entry.getValue());
                    return entry.getValue();
                }
            }
            
            log.warn("表名 {} 未找到对应的数据源映射，使用默认数据源: {}", tableName, defaultDataSource);
            return defaultDataSource;
        }
    }
}