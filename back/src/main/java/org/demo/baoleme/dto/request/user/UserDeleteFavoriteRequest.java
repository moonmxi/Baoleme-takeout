package org.demo.baoleme.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDeleteFavoriteRequest {
    @JsonProperty("store_id")
    private Long storeId;
}
