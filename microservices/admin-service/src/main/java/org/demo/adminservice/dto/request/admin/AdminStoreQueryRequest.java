package org.demo.adminservice.dto.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 管理员店铺查询请求
 * 修正版本，避免Jackson反序列化问题
 */
public class AdminStoreQueryRequest {
    private String keyword;
    private String type;
    private BigDecimal startRating;
    private BigDecimal endRating;
    private Integer status;
    private Long merchantId;
    private Integer page = 1;
    private Integer pageSize = 10;
    
    // 无参构造函数
    public AdminStoreQueryRequest() {
        this.page = 1;
        this.pageSize = 10;
    }
    
    // Getter和Setter方法
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getStartRating() {
        return startRating;
    }
    
    public void setStartRating(BigDecimal startRating) {
        this.startRating = startRating;
    }
    
    public BigDecimal getEndRating() {
        return endRating;
    }
    
    public void setEndRating(BigDecimal endRating) {
        this.endRating = endRating;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Long getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
    
    public Integer getPage() {
        return page != null ? page : 1;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getPageSize() {
        return pageSize != null ? pageSize : 10;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}