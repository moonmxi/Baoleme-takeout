/**
 * 多数据源配置类
 * 配置网关服务连接到不同微服务的数据库
 * 
 * 支持的数据源：
 * 1. 用户数据库 (baoleme_user)
 * 2. 商家数据库 (baoleme_merchant)
 * 3. 骑手数据库 (baoleme_rider)
 * 4. 管理员数据库 (baoleme_admin)
 * 5. 网关数据库 (baoleme_gateway)
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置类
 * 配置各个微服务数据库的连接池和事务管理
 */
@Slf4j
@Configuration
@MapperScan(basePackages = "org.demo.gateway.mapper")
public class DataSourceConfig {

    /**
     * 用户数据库数据源
     * 
     * @return DataSource 用户数据库数据源
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
     * @return DataSource 商家数据库数据源
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
     * @return DataSource 骑手数据库数据源
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
     * @return DataSource 管理员数据库数据源
     */
    @Bean(name = "adminDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.admin")
    public DataSource adminDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        log.info("管理员数据库数据源配置完成");
        return dataSource;
    }

    /**
     * 网关数据库数据源（主数据源）
     * 
     * @return DataSource 网关数据库数据源
     */
    @Bean(name = "gatewayDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource gatewayDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        log.info("网关数据库数据源配置完成（主数据源）");
        return dataSource;
    }

    /**
     * 动态数据源路由器
     * 根据上下文切换不同的数据源
     * 
     * @param userDataSource 用户数据源
     * @param merchantDataSource 商家数据源
     * @param riderDataSource 骑手数据源
     * @param adminDataSource 管理员数据源
     * @param gatewayDataSource 网关数据源
     * @return DynamicDataSource 动态数据源
     */
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dynamicDataSource(
            @Qualifier("userDataSource") DataSource userDataSource,
            @Qualifier("merchantDataSource") DataSource merchantDataSource,
            @Qualifier("riderDataSource") DataSource riderDataSource,
            @Qualifier("adminDataSource") DataSource adminDataSource,
            @Qualifier("gatewayDataSource") DataSource gatewayDataSource) {
        
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("user", userDataSource);
        targetDataSources.put("merchant", merchantDataSource);
        targetDataSources.put("rider", riderDataSource);
        targetDataSources.put("admin", adminDataSource);
        targetDataSources.put("gateway", gatewayDataSource);
        
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(gatewayDataSource);
        
        log.info("动态数据源路由器配置完成，支持数据源: {}", targetDataSources.keySet());
        return dynamicDataSource;
    }

    /**
     * SqlSessionFactory配置
     * 使用动态数据源
     * 
     * @param dynamicDataSource 动态数据源
     * @return SqlSessionFactory MyBatis会话工厂
     * @throws Exception 配置异常
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dynamicDataSource);
        
        // 配置MyBatis
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        sessionFactory.setConfiguration(configuration);
        
        log.info("SqlSessionFactory配置完成");
        return sessionFactory.getObject();
    }

    /**
     * 事务管理器配置
     * 
     * @param dynamicDataSource 动态数据源
     * @return PlatformTransactionManager 事务管理器
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dynamicDataSource);
        log.info("事务管理器配置完成");
        return transactionManager;
    }

    /**
     * 用户数据库JdbcTemplate
     * 
     * @param userDataSource 用户数据源
     * @return JdbcTemplate 用户数据库操作模板
     */
    @Bean(name = "userJdbcTemplate")
    public JdbcTemplate userJdbcTemplate(@Qualifier("userDataSource") DataSource userDataSource) {
        return new JdbcTemplate(userDataSource);
    }

    /**
     * 商家数据库JdbcTemplate
     * 
     * @param merchantDataSource 商家数据源
     * @return JdbcTemplate 商家数据库操作模板
     */
    @Bean(name = "merchantJdbcTemplate")
    public JdbcTemplate merchantJdbcTemplate(@Qualifier("merchantDataSource") DataSource merchantDataSource) {
        return new JdbcTemplate(merchantDataSource);
    }

    /**
     * 骑手数据库JdbcTemplate
     * 
     * @param riderDataSource 骑手数据源
     * @return JdbcTemplate 骑手数据库操作模板
     */
    @Bean(name = "riderJdbcTemplate")
    public JdbcTemplate riderJdbcTemplate(@Qualifier("riderDataSource") DataSource riderDataSource) {
        return new JdbcTemplate(riderDataSource);
    }

    /**
     * 管理员数据库JdbcTemplate
     * 
     * @param adminDataSource 管理员数据源
     * @return JdbcTemplate 管理员数据库操作模板
     */
    @Bean(name = "adminJdbcTemplate")
    public JdbcTemplate adminJdbcTemplate(@Qualifier("adminDataSource") DataSource adminDataSource) {
        return new JdbcTemplate(adminDataSource);
    }

    /**
     * 网关数据库JdbcTemplate
     * 
     * @param gatewayDataSource 网关数据源
     * @return JdbcTemplate 网关数据库操作模板
     */
    @Bean(name = "gatewayJdbcTemplate")
    public JdbcTemplate gatewayJdbcTemplate(@Qualifier("gatewayDataSource") DataSource gatewayDataSource) {
        return new JdbcTemplate(gatewayDataSource);
    }
}