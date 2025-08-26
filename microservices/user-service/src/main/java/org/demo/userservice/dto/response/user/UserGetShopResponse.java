package org.demo.userservice.dto.response.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.userservice.pojo.Store;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserGetShopResponse {
    private List<Store> data;
    private int total;
    private String type;
    private float minRating;
    private float maxRating;
    private int page;
    private int size;

}