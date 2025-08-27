package org.demo.userservice.dto.request.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserFavoriteRequest {
    @JsonProperty("store_id")
    private Long storeId;
}
