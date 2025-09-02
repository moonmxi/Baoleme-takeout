package org.demo.merchantservice.dto.request.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreDeleteRequest {
    @JsonProperty("store_id")
    private Long storeId;
}
