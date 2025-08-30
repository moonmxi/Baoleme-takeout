package org.demo.adminservice.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 管理员删除请求体（所有字段可选，但至少传一个）
 */
@Data
public class AdminDeleteRequest {

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("rider_name")
    private String riderName;

    @JsonProperty("merchant_name")
    private String merchantName;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("product_name")
    private String productName;
}