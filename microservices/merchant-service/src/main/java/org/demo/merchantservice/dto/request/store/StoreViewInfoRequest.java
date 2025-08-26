package org.demo.merchantservice.dto.request.store;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.demo.merchantservice.pojo.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StoreViewInfoRequest {
    @NotBlank(message = "店铺id不能为空")
    private Long storeId;
}
