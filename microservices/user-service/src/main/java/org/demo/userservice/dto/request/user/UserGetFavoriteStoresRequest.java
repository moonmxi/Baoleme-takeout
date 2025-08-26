package org.demo.userservice.dto.request.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserGetFavoriteStoresRequest {
    private String type;
    private BigDecimal distance;
    private BigDecimal wishPrice;
    private BigDecimal startRating;
    private BigDecimal endRating;
    private Integer page;
    private Integer pageSize;
}
