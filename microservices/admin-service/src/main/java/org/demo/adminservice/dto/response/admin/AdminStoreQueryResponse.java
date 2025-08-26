package org.demo.adminservice.dto.response.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminStoreQueryResponse {
    private Long id;
    private Long merchantId;
    private String name;
    private String description;
    private String location;
    private String type;
    private BigDecimal rating;
    private Integer status;
    private LocalDateTime createdAt;
    private String image;
}