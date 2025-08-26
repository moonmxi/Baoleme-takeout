package org.demo.merchantservice.dto.response.store;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.merchantservice.pojo.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StoreViewInfoResponse {
    private Long storeId;
    private String name;
    private String description;
    private String type;
    private String location;
    private BigDecimal rating;
    private Integer status;
    private LocalDateTime createdAt;
    private String image;
}
