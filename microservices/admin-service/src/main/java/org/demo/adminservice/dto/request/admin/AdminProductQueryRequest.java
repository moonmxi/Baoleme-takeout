package org.demo.adminservice.dto.request.admin;

import lombok.Data;

@Data
public class AdminProductQueryRequest {
    private Long storeId;
    private int page;
    private int pageSize;
}
