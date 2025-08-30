package org.demo.adminservice.dto.request.admin;

/**
 * 管理员订单分页查询请求
 * 简化版本，避免Jackson反序列化问题
 */
public class AdminOrderQueryRequest {

    private Integer page;
    private Integer pageSize;
    private Long userId;
    private Long storeId;
    private Long riderId;
    private Integer status;
    private String createdAt; // 改为String类型避免LocalDateTime解析问题
    private String endedAt;   // 改为String类型避免LocalDateTime解析问题
    
    // 无参构造函数
    public AdminOrderQueryRequest() {
    }
    
    // Getter和Setter方法
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getStoreId() {
        return storeId;
    }
    
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
    
    public Long getRiderId() {
        return riderId;
    }
    
    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getEndedAt() {
        return endedAt;
    }
    
    public void setEndedAt(String endedAt) {
        this.endedAt = endedAt;
    }
}