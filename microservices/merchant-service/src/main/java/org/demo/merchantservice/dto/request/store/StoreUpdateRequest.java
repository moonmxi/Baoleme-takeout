package org.demo.merchantservice.dto.request.store;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.demo.merchantservice.pojo.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StoreUpdateRequest {
    @NotBlank(message = "店铺id不能为空")
    private Long id;

    private String name;
    private String description;
    private String location;
    private Integer status;
    private String image;
}
