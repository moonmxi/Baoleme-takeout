package org.demo.userservice.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserGetProductResponse {
    private Long id;
    private Long storeId;
    private String name;
    private String category;
    private BigDecimal price;
    private String description;
    private String image;
    private Integer stock;
    private BigDecimal rating;
    private Integer status;
    private LocalDateTime createdAt;
}