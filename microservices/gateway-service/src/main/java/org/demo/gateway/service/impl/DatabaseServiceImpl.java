/**
 * 数据库操作服务实现类
 * 实现统一的跨数据库操作功能，通过动态数据源实现对不同微服务数据库的访问
 * 
 * 实现特性：
 * 1. 自动数据源路由 - 根据表名自动选择正确的数据库连接
 * 2. 统一CRUD操作 - 提供标准化的数据库操作接口
 * 3. 事务管理 - 支持单数据源和跨数据源事务
 * 4. 批量操作 - 高效的批量数据处理
 * 5. 健康检查 - 数据库连接状态监控
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demo.gateway.config.DynamicDataSource;
import org.demo.gateway.mapper.*;
import org.demo.gateway.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

/**
 * 数据库操作服务实现类
 * 提供高效可靠的跨数据库操作功能
 */
@Slf4j
@Service
public class DatabaseServiceImpl implements DatabaseService {

    /**
     * 各个Mapper接口
     */
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private MerchantMapper merchantMapper;
    
    @Autowired
    private StoreMapper storeMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private RiderMapper riderMapper;
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private SalesMapper salesMapper;
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;
    
    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private CouponMapper couponMapper;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 各个数据源的JdbcTemplate
     */
    @Autowired
    @Qualifier("userJdbcTemplate")
    private JdbcTemplate userJdbcTemplate;
    
    @Autowired
    @Qualifier("merchantJdbcTemplate")
    private JdbcTemplate merchantJdbcTemplate;
    
    @Autowired
    @Qualifier("riderJdbcTemplate")
    private JdbcTemplate riderJdbcTemplate;
    
    @Autowired
    @Qualifier("adminJdbcTemplate")
    private JdbcTemplate adminJdbcTemplate;
    
    @Autowired
    @Qualifier("gatewayJdbcTemplate")
    private JdbcTemplate gatewayJdbcTemplate;

    /**
     * 动态数据源
     */
    @Autowired
    @Qualifier("dynamicDataSource")
    private DynamicDataSource dynamicDataSource;

    /**
     * 根据ID查询单条记录
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @return Map<String, Object> 查询结果
     * @throws IllegalArgumentException 当表名或ID为空时抛出
     */
    @Override
    public Map<String, Object> selectById(String tableName, Long id) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (id == null) {
            throw new IllegalArgumentException("ID不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.selectById(id);
                    case "merchant":
                        return merchantMapper.selectById(id);
                    case "store":
                        return storeMapper.selectById(id);
                    case "product":
                        return productMapper.selectById(id);
                    case "rider":
                        return riderMapper.selectById(id);
                    case "admin":
                        return adminMapper.selectById(id);
                    case "order":
                        return orderMapper.selectById(id);
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
            });
        } catch (Exception e) {
            log.error("根据ID查询记录失败 - 表: {}, ID: {}, 错误: {}", tableName, id, e.getMessage(), e);
            throw new RuntimeException("查询记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据条件查询多条记录
     * 
     * @param tableName 表名
     * @param condition 查询条件Map，key为字段名，value为字段值
     * @return List<Map<String, Object>> 查询结果列表
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    @Override
    public List<Map<String, Object>> selectByConditions(String tableName, Map<String, Object> condition) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (condition == null) {
            condition = new HashMap<>();
        }

        // 转换条件格式，支持智能字段类型判断
        Map<String, Object> processedConditions = processConditionsWithFieldTypes(tableName, condition);

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.selectByConditions(processedConditions);
                    case "merchant":
                        return merchantMapper.selectByConditions(processedConditions);
                    case "store":
                        return storeMapper.selectByConditions(processedConditions);
                    case "product":
                        return productMapper.selectByConditions(processedConditions);
                    case "rider":
                        return riderMapper.selectByConditions(processedConditions);
                    case "admin":
                        return adminMapper.selectByConditions(processedConditions);
                    case "order":
                        return orderMapper.selectByConditions(processedConditions);
                    case "sales":
                        return salesMapper.selectByConditions(processedConditions);
                    case "favorite":
                        return favoriteMapper.selectByConditions(processedConditions);
                    case "browse_history":
                        return browseHistoryMapper.selectByConditions(processedConditions);
                    case "cart":
                        return cartMapper.selectByConditions(processedConditions);
                    case "coupon":
                        return couponMapper.selectByConditions(processedConditions);
                    case "message":
                        return messageMapper.selectByConditions(processedConditions);
                    case "review":
                        return reviewMapper.selectByConditions(processedConditions);
                    case "order_item":
                        return orderItemMapper.selectByConditions(processedConditions);
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
            });
        } catch (Exception e) {
            log.error("根据条件查询记录失败 - 表: {}, 条件: {}, 错误: {}", tableName, condition, e.getMessage(), e);
            throw new RuntimeException("查询记录失败: " + e.getMessage(), e);
        }
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
    @Override
    public List<Map<String, Object>> selectAll(String tableName, int page, int pageSize) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (page < 1) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (pageSize < 1 || pageSize > 1000) {
            throw new IllegalArgumentException("每页大小必须在1-1000之间");
        }

        int offset = (page - 1) * pageSize;

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.selectAll(offset, pageSize);
                    case "merchant":
                        return merchantMapper.selectAll(offset, pageSize);
                    case "store":
                        return storeMapper.selectAll(offset, pageSize);
                    case "product":
                        return productMapper.selectAll(offset, pageSize);
                    case "rider":
                        return riderMapper.selectAll(offset, pageSize);
                    case "admin":
                        return adminMapper.selectAll(offset, pageSize);
                    case "order":
                        return orderMapper.selectAll(offset, pageSize);
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
            });
        } catch (Exception e) {
            log.error("分页查询记录失败 - 表: {}, 页码: {}, 每页大小: {}, 错误: {}", tableName, page, pageSize, e.getMessage(), e);
            throw new RuntimeException("查询记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 插入单条记录
     * 
     * @param tableName 表名
     * @param data 插入数据
     * @return Long 插入记录的ID
     * @throws IllegalArgumentException 当表名或数据为空时抛出
     */
    @Override
    @Transactional
    public Long insert(String tableName, Map<String, Object> data) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("插入数据不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                // 只插入非空字段，避免将空值插入数据库
                Map<String, Object> filteredData = filterNonNullFields(data);
                if (filteredData.isEmpty()) {
                    log.warn("没有有效的插入字段 - 表: {}", tableName);
                    return null;
                }
                
                // 验证字段有效性
                Map<String, Object> validatedData = new HashMap<>();
                for (Map.Entry<String, Object> entry : filteredData.entrySet()) {
                    String fieldName = entry.getKey();
                    if (isValidField(tableName, fieldName)) {
                        validatedData.put(fieldName, entry.getValue());
                    } else {
                        log.warn("无效的插入字段被忽略 - 表: {}, 字段: {}", tableName, fieldName);
                    }
                }
                
                if (validatedData.isEmpty()) {
                    log.warn("没有有效的插入字段 - 表: {}", tableName);
                    return null;
                }
                
                // 添加创建时间（如果表支持且未提供）
                if (isValidField(tableName, "created_at") && !validatedData.containsKey("created_at")) {
                    validatedData.put("created_at", LocalDateTime.now());
                }
                
                log.info("准备插入记录 - 表: {}, 字段: {}", tableName, validatedData.keySet());
                
                int result;
                switch (tableName.toLowerCase()) {
                    case "user":
                        result = userMapper.insert(validatedData);
                        break;
                    case "merchant":
                        result = merchantMapper.insert(validatedData);
                        break;
                    case "store":
                        result = storeMapper.insert(validatedData);
                        break;
                    case "product":
                        result = productMapper.insert(validatedData);
                        break;
                    case "rider":
                        result = riderMapper.insert(validatedData);
                        break;
                    case "admin":
                        result = adminMapper.insert(validatedData);
                        break;
                    case "order":
                        result = orderMapper.insert(validatedData);
                        break;
                    case "sales":
                        result = salesMapper.insert(validatedData);
                        break;
                    case "favorite":
                        result = favoriteMapper.insert(validatedData);
                        break;
                    case "browse_history":
                        result = browseHistoryMapper.insert(validatedData);
                        break;
                    case "cart":
                        result = cartMapper.insert(validatedData);
                        break;
                    case "coupon":
                        result = couponMapper.insert(validatedData);
                        break;
                    case "message":
                        result = messageMapper.insert(validatedData);
                        break;
                    case "review":
                        result = reviewMapper.insert(validatedData);
                        break;
                    case "order_item":
                        result = orderItemMapper.insert(validatedData);
                        break;
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
                
                if (result > 0 && validatedData.containsKey("id")) {
                    return ((Number) validatedData.get("id")).longValue();
                }
                return null;
            });
        } catch (Exception e) {
            log.error("插入记录失败 - 表: {}, 数据: {}, 错误: {}", tableName, data, e.getMessage(), e);
            throw new RuntimeException("插入记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量插入记录
     * 只插入非空字段，避免将空值错误地插入数据库
     * 
     * @param tableName 表名
     * @param dataList 插入数据列表
     * @return int 插入成功的记录数
     * @throws IllegalArgumentException 当表名或数据列表为空时抛出
     */
    @Override
    @Transactional
    public int batchInsert(String tableName, List<Map<String, Object>> dataList) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("插入数据列表不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                // 过滤和验证每条记录的字段
                List<Map<String, Object>> validatedDataList = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
                
                for (Map<String, Object> data : dataList) {
                    // 只插入非空字段，避免将空值插入数据库
                    Map<String, Object> filteredData = filterNonNullFields(data);
                    if (filteredData.isEmpty()) {
                        log.warn("跳过空记录 - 表: {}", tableName);
                        continue;
                    }
                    
                    // 验证字段有效性
                    Map<String, Object> validatedData = new HashMap<>();
                    for (Map.Entry<String, Object> entry : filteredData.entrySet()) {
                        String fieldName = entry.getKey();
                        if (isValidField(tableName, fieldName)) {
                            validatedData.put(fieldName, entry.getValue());
                        } else {
                            log.warn("无效的批量插入字段被忽略 - 表: {}, 字段: {}", tableName, fieldName);
                        }
                    }
                    
                    if (!validatedData.isEmpty()) {
                        // 添加创建时间（如果表支持且未提供）
                        if (isValidField(tableName, "created_at") && !validatedData.containsKey("created_at")) {
                            validatedData.put("created_at", now);
                        }
                        validatedDataList.add(validatedData);
                    }
                }
                
                if (validatedDataList.isEmpty()) {
                    log.warn("没有有效的批量插入记录 - 表: {}", tableName);
                    return 0;
                }
                
                log.info("准备批量插入记录 - 表: {}, 有效记录数: {}/{}", tableName, validatedDataList.size(), dataList.size());
                
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.batchInsert(validatedDataList);
                    case "merchant":
                        return merchantMapper.batchInsert(validatedDataList);
                    case "store":
                        return storeMapper.batchInsert(validatedDataList);
                    case "product":
                        return productMapper.batchInsert(validatedDataList);
                    case "rider":
                        return riderMapper.batchInsert(validatedDataList);
                    case "admin":
                        return adminMapper.batchInsert(validatedDataList);
                    case "order":
                        return orderMapper.batchInsert(validatedDataList);
                    case "sales":
                        return salesMapper.batchInsert(validatedDataList);
                    case "favorite":
                        return favoriteMapper.batchInsert(validatedDataList);
                    case "browse_history":
                        return browseHistoryMapper.batchInsert(validatedDataList);
                    case "cart":
                        return cartMapper.batchInsert(validatedDataList);
                    case "coupon":
                        return couponMapper.batchInsert(validatedDataList);
                    case "message":
                        return messageMapper.batchInsert(validatedDataList);
                    case "review":
                        return reviewMapper.batchInsert(validatedDataList);
                    case "order_item":
                        return orderItemMapper.batchInsert(validatedDataList);
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
            });
        } catch (Exception e) {
            log.error("批量插入记录失败 - 表: {}, 数据量: {}, 错误: {}", tableName, dataList.size(), e.getMessage(), e);
            throw new RuntimeException("批量插入记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID更新记录
     * 只更新非空字段，避免将空值错误地插入数据库
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @param data 更新数据
     * @return int 更新影响的行数
     * @throws IllegalArgumentException 当表名、ID或数据为空时抛出
     */
    @Override
    @Transactional
    public int updateById(String tableName, Long id, Map<String, Object> data) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (id == null) {
            throw new IllegalArgumentException("ID不能为空");
        }
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("更新数据不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                // 首先查询现有记录
                Map<String, Object> existing = selectById(tableName, id);
                if (existing == null || existing.isEmpty()) {
                    log.warn("要更新的记录不存在 - 表: {}, ID: {}", tableName, id);
                    return 0;
                }
                
                // 只更新非空字段，避免将空值插入数据库
                Map<String, Object> filteredData = filterNonNullFields(data);
                if (filteredData.isEmpty()) {
                    log.warn("没有有效的更新字段 - 表: {}, ID: {}", tableName, id);
                    return 0;
                }
                
                // 验证字段有效性，防止SQL注入
                Map<String, Object> validatedData = new HashMap<>();
                for (Map.Entry<String, Object> entry : filteredData.entrySet()) {
                    String fieldName = entry.getKey();
                    if (isValidField(tableName, fieldName)) {
                        validatedData.put(fieldName, entry.getValue());
                    } else {
                        log.warn("无效字段被忽略 - 表: {}, 字段: {}", tableName, fieldName);
                    }
                }
                
                if (validatedData.isEmpty()) {
                    log.warn("没有有效的更新字段 - 表: {}, ID: {}", tableName, id);
                    return 0;
                }
                
                // 添加更新时间（如果表支持）
                if (isValidField(tableName, "updated_at")) {
                    validatedData.put("updated_at", LocalDateTime.now());
                }
                
                log.info("准备更新记录 - 表: {}, ID: {}, 字段: {}", tableName, id, validatedData.keySet());
                
                // 使用验证后的数据进行更新
                filteredData = validatedData;
                
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.updateById(id, filteredData);
                    case "merchant":
                        return merchantMapper.updateById(id, filteredData);
                    case "store":
                        return storeMapper.updateById(id, filteredData);
                    case "product":
                        return productMapper.updateById(id, filteredData);
                    case "rider":
                        return riderMapper.updateById(id, filteredData);
                    case "admin":
                        return adminMapper.updateById(id, filteredData);
                    case "order":
                        return orderMapper.updateById(id, filteredData);
                    case "sales":
                        return salesMapper.updateById(id, filteredData);
                    case "favorite":
                        // favorite表没有单独的ID字段，不支持按ID更新
                        throw new IllegalArgumentException("favorite表不支持按ID更新");
                    case "browse_history":
                        // browse_history表没有单独的ID字段，不支持按ID更新
                        throw new IllegalArgumentException("browse_history表不支持按ID更新");
                    case "cart":
                        // cart表没有单独的ID字段，不支持按ID更新
                        throw new IllegalArgumentException("cart表不支持按ID更新");
                    case "coupon":
                        return couponMapper.updateById(id, filteredData);
                    case "message":
                        return messageMapper.updateContent(id, (String) filteredData.get("content"));
                    case "review":
                        return reviewMapper.updateById(id, filteredData);
                    case "order_item":
                        // order_item表没有单独的ID字段，不支持按ID更新
                        throw new IllegalArgumentException("order_item表不支持按ID更新");
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
            });
        } catch (Exception e) {
            log.error("根据ID更新记录失败 - 表: {}, ID: {}, 数据: {}, 错误: {}", tableName, id, data, e.getMessage(), e);
            throw new RuntimeException("更新记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据条件更新记录
     * 只更新非空字段，避免将空值错误地插入数据库
     * 
     * @param tableName 表名
     * @param condition 更新条件Map，key为字段名，value为字段值
     * @param data 更新数据
     * @return int 更新影响的行数
     * @throws IllegalArgumentException 当表名、条件或数据为空时抛出
     */
    @Override
    @Transactional
    public int updateByConditions(String tableName, Map<String, Object> condition, Map<String, Object> data) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (condition == null || condition.isEmpty()) {
            throw new IllegalArgumentException("更新条件不能为空");
        }
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("更新数据不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                // 只更新非空字段，避免将空值插入数据库
                Map<String, Object> filteredData = filterNonNullFields(data);
                if (filteredData.isEmpty()) {
                    log.warn("没有有效的更新字段 - 表: {}", tableName);
                    return 0;
                }
                
                // 验证更新字段的有效性
                Map<String, Object> validatedData = new HashMap<>();
                for (Map.Entry<String, Object> entry : filteredData.entrySet()) {
                    String fieldName = entry.getKey();
                    if (isValidField(tableName, fieldName)) {
                        validatedData.put(fieldName, entry.getValue());
                    } else {
                        log.warn("无效的更新字段被忽略 - 表: {}, 字段: {}", tableName, fieldName);
                    }
                }
                
                if (validatedData.isEmpty()) {
                    log.warn("没有有效的更新字段 - 表: {}", tableName);
                    return 0;
                }
                
                // 验证条件字段的有效性
                Map<String, Object> validatedCondition = new HashMap<>();
                for (Map.Entry<String, Object> entry : condition.entrySet()) {
                    String fieldName = entry.getKey();
                    if (isValidField(tableName, fieldName)) {
                        validatedCondition.put(fieldName, entry.getValue());
                    } else {
                        log.warn("无效的条件字段被忽略 - 表: {}, 字段: {}", tableName, fieldName);
                    }
                }
                
                if (validatedCondition.isEmpty()) {
                    log.warn("没有有效的条件字段 - 表: {}", tableName);
                    return 0;
                }
                
                // 添加更新时间（如果表支持）
                if (isValidField(tableName, "updated_at")) {
                    validatedData.put("updated_at", LocalDateTime.now());
                }
                
                // 使用JdbcTemplate执行条件更新
                JdbcTemplate jdbcTemplate = getJdbcTemplateByTable(tableName);
                
                StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
                List<Object> params = new ArrayList<>();
                
                // 构建SET子句
                boolean first = true;
                for (Map.Entry<String, Object> entry : validatedData.entrySet()) {
                    if (!first) sql.append(", ");
                    sql.append(entry.getKey()).append(" = ?");
                    params.add(entry.getValue());
                    first = false;
                }
                
                // 构建WHERE子句，支持智能字段类型判断
                sql.append(" WHERE 1=1");
                Set<String> stringFields = getStringFieldsByTable(tableName);
                
                for (Map.Entry<String, Object> entry : validatedCondition.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    
                    if (stringFields.contains(fieldName.toLowerCase())) {
                        // 字符串字段使用LIKE模糊匹配
                        sql.append(" AND ").append(fieldName).append(" LIKE ?");
                        params.add("%" + fieldValue + "%");
                    } else {
                        // 非字符串字段使用精确匹配
                        sql.append(" AND ").append(fieldName).append(" = ?");
                        params.add(fieldValue);
                    }
                }
                
                log.info("执行条件更新 - 表: {}, 更新字段: {}, 条件字段: {}", tableName, validatedData.keySet(), validatedCondition.keySet());
                
                return jdbcTemplate.update(sql.toString(), params.toArray());
            });
        } catch (Exception e) {
            log.error("根据条件更新记录失败 - 表: {}, 条件: {}, 数据: {}, 错误: {}", tableName, condition, data, e.getMessage(), e);
            throw new RuntimeException("更新记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量更新记录
     * 
     * @param tableName 表名
     * @param dataList 更新数据列表（必须包含ID字段）
     * @return int 更新影响的行数
     * @throws IllegalArgumentException 当表名或数据列表为空时抛出
     */
    @Override
    @Transactional
    public int batchUpdate(String tableName, List<Map<String, Object>> dataList) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("更新数据列表不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.batchUpdate(dataList);
                    case "product":
                        return productMapper.batchUpdateStock(dataList);
                    default:
                        // 对于不支持批量更新的表，逐条更新
                        int totalUpdated = 0;
                        for (Map<String, Object> data : dataList) {
                            if (data.containsKey("id")) {
                                Long id = ((Number) data.get("id")).longValue();
                                data.remove("id");
                                totalUpdated += updateById(tableName, id, data);
                            }
                        }
                        return totalUpdated;
                }
            });
        } catch (Exception e) {
            log.error("批量更新记录失败 - 表: {}, 数据量: {}, 错误: {}", tableName, dataList.size(), e.getMessage(), e);
            throw new RuntimeException("批量更新记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID删除记录
     * 
     * @param tableName 表名
     * @param id 记录ID
     * @return int 删除影响的行数
     * @throws IllegalArgumentException 当表名或ID为空时抛出
     */
    @Override
    @Transactional
    public int deleteById(String tableName, Long id) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (id == null) {
            throw new IllegalArgumentException("ID不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.deleteById(id);
                    case "merchant":
                        return merchantMapper.deleteById(id);
                    case "store":
                        return storeMapper.deleteById(id);
                    case "product":
                        return productMapper.deleteById(id);
                    case "rider":
                        return riderMapper.deleteById(id);
                    case "admin":
                        return adminMapper.deleteById(id);
                    case "order":
                        return orderMapper.deleteById(id);
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
            });
        } catch (Exception e) {
            log.error("根据ID删除记录失败 - 表: {}, ID: {}, 错误: {}", tableName, id, e.getMessage(), e);
            throw new RuntimeException("删除记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据条件删除记录
     * 
     * @param tableName 表名
     * @param condition 删除条件Map，key为字段名，value为字段值
     * @return int 删除影响的行数
     * @throws IllegalArgumentException 当表名或条件为空时抛出
     */
    @Override
    @Transactional
    public int deleteByConditions(String tableName, Map<String, Object> condition) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (condition == null || condition.isEmpty()) {
            throw new IllegalArgumentException("删除条件不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                JdbcTemplate jdbcTemplate = getJdbcTemplateByTable(tableName);
                
                StringBuilder sql = new StringBuilder("DELETE FROM " + tableName + " WHERE 1=1");
                List<Object> params = new ArrayList<>();
                Set<String> stringFields = getStringFieldsByTable(tableName);
                
                for (Map.Entry<String, Object> entry : condition.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    
                    if (stringFields.contains(fieldName.toLowerCase())) {
                        // 字符串字段使用LIKE模糊匹配
                        sql.append(" AND ").append(fieldName).append(" LIKE ?");
                        params.add("%" + fieldValue + "%");
                    } else {
                        // 非字符串字段使用精确匹配
                        sql.append(" AND ").append(fieldName).append(" = ?");
                        params.add(fieldValue);
                    }
                }
                
                return jdbcTemplate.update(sql.toString(), params.toArray());
            });
        } catch (Exception e) {
            log.error("根据条件删除记录失败 - 表: {}, 条件: {}, 错误: {}", tableName, condition, e.getMessage(), e);
            throw new RuntimeException("删除记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量删除记录
     * 
     * @param tableName 表名
     * @param ids ID列表
     * @return int 删除影响的行数
     * @throws IllegalArgumentException 当表名或ID列表为空时抛出
     */
    @Override
    @Transactional
    public int batchDelete(String tableName, List<Long> ids) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID列表不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                JdbcTemplate jdbcTemplate = getJdbcTemplateByTable(tableName);
                
                StringBuilder sql = new StringBuilder("DELETE FROM " + tableName + " WHERE id IN (");
                for (int i = 0; i < ids.size(); i++) {
                    if (i > 0) sql.append(", ");
                    sql.append("?");
                }
                sql.append(")");
                
                return jdbcTemplate.update(sql.toString(), ids.toArray());
            });
        } catch (Exception e) {
            log.error("批量删除记录失败 - 表: {}, ID数量: {}, 错误: {}", tableName, ids.size(), e.getMessage(), e);
            throw new RuntimeException("批量删除记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 统计记录总数
     * 
     * @param tableName 表名
     * @return long 记录总数
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    @Override
    public long count(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.count();
                    case "merchant":
                        return merchantMapper.count();
                    case "store":
                        return storeMapper.count();
                    case "product":
                        return productMapper.count();
                    case "rider":
                        return riderMapper.count();
                    case "admin":
                        return adminMapper.count();
                    case "order":
                        return orderMapper.count();
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
            });
        } catch (Exception e) {
            log.error("统计记录总数失败 - 表: {}, 错误: {}", tableName, e.getMessage(), e);
            throw new RuntimeException("统计记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据条件统计记录数
     * 
     * @param tableName 表名
     * @param condition 统计条件Map，key为字段名，value为字段值
     * @return long 记录数
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    @Override
    public long countByConditions(String tableName, Map<String, Object> condition) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (condition == null) {
            condition = new HashMap<>();
        }

        // 转换条件格式，支持智能字段类型判断
        Map<String, Object> processedConditions = processConditionsWithFieldTypes(tableName, condition);

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                switch (tableName.toLowerCase()) {
                    case "user":
                        return userMapper.countByConditions(processedConditions);
                    case "merchant":
                        return merchantMapper.countByConditions(processedConditions);
                    case "store":
                        return storeMapper.countByConditions(processedConditions);
                    case "product":
                        return productMapper.countByConditions(processedConditions);
                    case "rider":
                        return riderMapper.countByConditions(processedConditions);
                    case "admin":
                        return adminMapper.countByConditions(processedConditions);
                    case "order":
                        return orderMapper.countByConditions(processedConditions);
                    case "sales":
                        return salesMapper.countByConditions(processedConditions);
                    case "favorite":
                        return favoriteMapper.countByConditions(processedConditions);
                    case "browse_history":
                        return browseHistoryMapper.countByConditions(processedConditions);
                    case "cart":
                        return cartMapper.countByConditions(processedConditions);
                    case "coupon":
                        return couponMapper.countByConditions(processedConditions);
                    case "message":
                        return messageMapper.countByConditions(processedConditions);
                    case "review":
                        return reviewMapper.countByConditions(processedConditions);
                    case "order_item":
                        return orderItemMapper.countByConditions(processedConditions);
                    default:
                        throw new IllegalArgumentException("不支持的表名: " + tableName);
                }
            });
        } catch (Exception e) {
            log.error("根据条件统计记录数失败 - 表: {}, 条件: {}, 错误: {}", tableName, condition, e.getMessage(), e);
            throw new RuntimeException("统计记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行自定义SQL查询
     * 
     * @param sql SQL语句
     * @param params 参数
     * @return List<Map<String, Object>> 查询结果
     * @throws IllegalArgumentException 当SQL为空时抛出
     */
    @Override
    public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL语句不能为空");
        }
        if (params == null) {
            params = new HashMap<>();
        }

        try {
            // 使用默认的网关数据源执行自定义SQL
            return gatewayJdbcTemplate.queryForList(sql, params);
        } catch (Exception e) {
            log.error("执行自定义SQL查询失败 - SQL: {}, 参数: {}, 错误: {}", sql, params, e.getMessage(), e);
            throw new RuntimeException("执行SQL查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行自定义SQL更新
     * 
     * @param sql SQL语句
     * @param params 参数
     * @return int 影响行数
     * @throws IllegalArgumentException 当SQL为空时抛出
     */
    @Override
    @Transactional
    public int executeUpdate(String sql, Map<String, Object> params) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL语句不能为空");
        }
        if (params == null) {
            params = new HashMap<>();
        }

        try {
            // 使用默认的网关数据源执行自定义SQL
            return gatewayJdbcTemplate.update(sql, params);
        } catch (Exception e) {
            log.error("执行自定义SQL更新失败 - SQL: {}, 参数: {}, 错误: {}", sql, params, e.getMessage(), e);
            throw new RuntimeException("执行SQL更新失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查表是否存在
     * 
     * @param tableName 表名
     * @return boolean 表是否存在
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    @Override
    public boolean tableExists(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                JdbcTemplate jdbcTemplate = getJdbcTemplateByTable(tableName);
                String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
                return count != null && count > 0;
            });
        } catch (Exception e) {
            log.error("检查表是否存在失败 - 表: {}, 错误: {}", tableName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取表结构信息
     * 
     * @param tableName 表名
     * @return List<Map<String, Object>> 表结构信息
     * @throws IllegalArgumentException 当表名为空时抛出
     */
    @Override
    public List<Map<String, Object>> getTableStructure(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }

        try {
            return DynamicDataSource.DataSourceContextManager.executeWithTable(tableName, () -> {
                JdbcTemplate jdbcTemplate = getJdbcTemplateByTable(tableName);
                String sql = "SELECT column_name, data_type, is_nullable, column_default, column_comment " +
                           "FROM information_schema.columns WHERE table_name = ? ORDER BY ordinal_position";
                return jdbcTemplate.queryForList(sql, tableName);
            });
        } catch (Exception e) {
            log.error("获取表结构信息失败 - 表: {}, 错误: {}", tableName, e.getMessage(), e);
            throw new RuntimeException("获取表结构失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取数据库连接状态
     * 
     * @param dataSourceKey 数据源键名
     * @return boolean 连接是否正常
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    @Override
    public boolean isConnectionHealthy(String dataSourceKey) {
        if (dataSourceKey == null || dataSourceKey.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源键名不能为空");
        }

        return dynamicDataSource.isDataSourceHealthy(dataSourceKey);
    }

    /**
     * 获取所有数据源的健康状态
     * 
     * @return Map<String, Boolean> 数据源健康状态映射
     */
    @Override
    public Map<String, Boolean> getAllDataSourceHealth() {
        return dynamicDataSource.getAllDataSourceHealth();
    }

    /**
     * 开始事务
     * 
     * @param dataSourceKey 数据源键名
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    @Override
    public void beginTransaction(String dataSourceKey) {
        if (dataSourceKey == null || dataSourceKey.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源键名不能为空");
        }
        
        DynamicDataSource.setDataSourceKey(dataSourceKey);
        log.debug("开始事务 - 数据源: {}", dataSourceKey);
    }

    /**
     * 提交事务
     * 
     * @param dataSourceKey 数据源键名
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    @Override
    public void commitTransaction(String dataSourceKey) {
        if (dataSourceKey == null || dataSourceKey.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源键名不能为空");
        }
        
        log.debug("提交事务 - 数据源: {}", dataSourceKey);
        DynamicDataSource.clearDataSourceKey();
    }

    /**
     * 回滚事务
     * 
     * @param dataSourceKey 数据源键名
     * @throws IllegalArgumentException 当数据源键名为空时抛出
     */
    @Override
    public void rollbackTransaction(String dataSourceKey) {
        if (dataSourceKey == null || dataSourceKey.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源键名不能为空");
        }
        
        log.debug("回滚事务 - 数据源: {}", dataSourceKey);
        DynamicDataSource.clearDataSourceKey();
    }

    /**
     * 执行跨数据库事务操作
     * 
     * @param operation 事务操作
     * @param <T> 返回值类型
     * @return T 操作结果
     * @throws Exception 操作异常
     */
    @Override
    @Transactional
    public <T> T executeInTransaction(TransactionOperation<T> operation) throws Exception {
        if (operation == null) {
            throw new IllegalArgumentException("事务操作不能为空");
        }
        
        try {
            return operation.execute();
        } catch (Exception e) {
            log.error("执行事务操作失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据表名获取对应的JdbcTemplate
     * 
     * @param tableName 表名
     * @return JdbcTemplate 对应的JdbcTemplate
     */
    private JdbcTemplate getJdbcTemplateByTable(String tableName) {
        switch (tableName.toLowerCase()) {
            case "user":
            case "favorite":
            case "browse_history":
                return userJdbcTemplate;
            case "merchant":
            case "store":
            case "product":
            case "sales":
                return merchantJdbcTemplate;
            case "rider":
                return riderJdbcTemplate;
            case "admin":
                return adminJdbcTemplate;
            default:
                return gatewayJdbcTemplate;
        }
    }

    /**
     * 处理条件查询参数，根据字段类型智能选择匹配方式
     * 
     * @param tableName 表名
     * @param condition 原始条件
     * @return Map<String, Object> 处理后的条件
     */
    private Map<String, Object> processConditionsWithFieldTypes(String tableName, Map<String, Object> condition) {
        Map<String, Object> processedConditions = new HashMap<>();
        
        // 获取表的字符串字段映射
        Set<String> stringFields = getStringFieldsByTable(tableName);
        
        for (Map.Entry<String, Object> entry : condition.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            
            if (fieldValue == null) {
                processedConditions.put(fieldName, null);
                continue;
            }
            
            // 判断字段类型并处理
            if (stringFields.contains(fieldName.toLowerCase())) {
                // 字符串字段使用模糊匹配，在SQL中会被处理为LIKE
                processedConditions.put(fieldName, fieldValue);
                processedConditions.put(fieldName + "_like", true); // 标记为模糊匹配
            } else {
                // 非字符串字段使用精确匹配
                processedConditions.put(fieldName, fieldValue);
            }
        }
        
        return processedConditions;
    }

    /**
     * 获取表的字符串类型字段集合
     * 
     * @param tableName 表名
     * @return Set<String> 字符串字段集合
     */
    private Set<String> getStringFieldsByTable(String tableName) {
        Set<String> stringFields = new HashSet<>();
        
        switch (tableName.toLowerCase()) {
            case "user":
                stringFields.addAll(Arrays.asList("username", "password", "description", "location", "gender", "phone", "avatar"));
                break;
            case "merchant":
                stringFields.addAll(Arrays.asList("username", "password", "phone", "avatar"));
                break;
            case "store":
                stringFields.addAll(Arrays.asList("name", "type", "description", "location", "image"));
                break;
            case "product":
                stringFields.addAll(Arrays.asList("name", "description", "category", "image"));
                break;
            case "rider":
                stringFields.addAll(Arrays.asList("username", "password", "phone", "avatar"));
                break;
            case "admin":
                stringFields.addAll(Arrays.asList("password"));
                break;
            case "order":
                stringFields.addAll(Arrays.asList("user_location", "store_location", "remark"));
                break;
            case "sales":
                stringFields.addAll(Arrays.asList("payment_method"));
                break;
            case "cart":
                // cart表没有字符串字段
                break;
            case "coupon":
                // coupon表没有字符串字段（type是int）
                break;
            case "message":
                stringFields.addAll(Arrays.asList("content", "sender_role", "receiver_role"));
                break;
            case "review":
                stringFields.addAll(Arrays.asList("comment", "image"));
                break;
            case "order_item":
                // order_item表没有字符串字段
                break;
            case "favorite":
                // favorite表没有字符串字段
                break;
            case "browse_history":
                // browse_history表没有字符串字段
                break;
            default:
                log.warn("未知表名: {}，无法确定字符串字段", tableName);
                break;
        }
        
        return stringFields;
    }

    /**
     * 过滤非空字段，避免将空值插入数据库
     * 参考rider-service的实现方式，只更新有效的非空字段
     * 
     * @param data 原始数据
     * @return Map<String, Object> 过滤后的数据
     */
    private Map<String, Object> filterNonNullFields(Map<String, Object> data) {
        Map<String, Object> filteredData = new HashMap<>();
        
        if (data == null) {
            return filteredData;
        }
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // 过滤空值
            if (value != null) {
                // 对于字符串类型，还要检查是否为空字符串
                if (value instanceof String) {
                    String stringValue = (String) value;
                    if (!stringValue.trim().isEmpty()) {
                        filteredData.put(key, stringValue.trim());
                    }
                } else {
                    // 非字符串类型直接添加
                    filteredData.put(key, value);
                }
            }
        }
        
        return filteredData;
    }

    /**
     * 验证更新字段的有效性
     * 检查字段是否为表的有效字段，避免SQL注入风险
     * 
     * @param tableName 表名
     * @param fieldName 字段名
     * @return boolean 是否为有效字段
     */
    private boolean isValidField(String tableName, String fieldName) {
        Set<String> validFields = getValidFieldsByTable(tableName);
        return validFields.contains(fieldName.toLowerCase());
    }

    /**
     * 获取表的有效字段集合
     * 
     * @param tableName 表名
     * @return Set<String> 有效字段集合
     */
    private Set<String> getValidFieldsByTable(String tableName) {
        Set<String> validFields = new HashSet<>();
        
        switch (tableName.toLowerCase()) {
            case "user":
                validFields.addAll(Arrays.asList("id", "username", "password", "description", "location", "gender", "phone", "avatar", "created_at", "updated_at"));
                break;
            case "merchant":
                validFields.addAll(Arrays.asList("id", "username", "password", "phone", "avatar", "created_at", "updated_at"));
                break;
            case "store":
                validFields.addAll(Arrays.asList("id", "merchant_id", "name", "type", "description", "location", "distance", "rating", "status", "avg_price", "created_at", "updated_at", "image"));
                break;
            case "product":
                validFields.addAll(Arrays.asList("id", "store_id", "name", "description", "price", "category", "stock", "rating", "status", "created_at", "updated_at", "image"));
                break;
            case "rider":
                validFields.addAll(Arrays.asList("id", "username", "password", "order_status", "dispatch_mode", "phone", "balance", "avatar", "created_at", "updated_at"));
                break;
            case "admin":
                validFields.addAll(Arrays.asList("id", "password", "created_at", "updated_at"));
                break;
            case "order":
                validFields.addAll(Arrays.asList("id", "user_id", "store_id", "rider_id", "status", "user_location", "store_location", "total_price", "actual_price", "remark", "delivery_price", "created_at", "updated_at", "deadline", "ended_at"));
                break;
            case "sales":
                validFields.addAll(Arrays.asList("id", "product_id", "store_id", "sale_date", "quantity", "unit_price", "total_amount", "payment_method", "customer_id", "created_at", "updated_at"));
                break;
            case "coupon":
                validFields.addAll(Arrays.asList("id", "user_id", "store_id", "type", "discount", "expiration_date", "created_at", "updated_at", "is_used", "full_amount", "reduce_amount"));
                break;
            case "message":
                validFields.addAll(Arrays.asList("id", "content", "sender_id", "receiver_id", "sender_role", "receiver_role", "created_at", "updated_at"));
                break;
            case "review":
                validFields.addAll(Arrays.asList("id", "user_id", "store_id", "product_id", "rating", "comment", "image", "created_at", "updated_at"));
                break;
            default:
                log.warn("未知表名: {}，无法确定有效字段", tableName);
                break;
        }
        
        return validFields;
    }
}