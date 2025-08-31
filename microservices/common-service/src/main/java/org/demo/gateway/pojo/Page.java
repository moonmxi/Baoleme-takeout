/**
 * 分页实体类
 * 用于封装分页查询结果
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.gateway.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分页实体类
 * 封装分页查询的所有相关信息
 * 
 * @param <E> 泛型，表示分页数据的类型
 */
@Data
@Component
public class Page<E> {
    
    /**
     * 当前页码
     */
    private Integer currPage;
    
    /**
     * 上一页页码
     */
    private Integer prePage;
    
    /**
     * 下一页页码
     */
    private Integer nextPage;
    
    /**
     * 查询的数据总条数
     */
    private Integer count;
    
    /**
     * 当前页的数据内容
     */
    private List<E> list;
    
    /**
     * 每页的数据条数
     */
    private Integer pageSize;
    
    /**
     * 总页数
     */
    private Integer pageCount;
    
    /**
     * 过滤条数
     */
    private Integer filterCount;
}