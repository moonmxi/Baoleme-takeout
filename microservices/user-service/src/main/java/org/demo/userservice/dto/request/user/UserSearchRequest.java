package org.demo.userservice.dto.request.user;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserSearchRequest {
    private String keyword;
    private BigDecimal distance;
    private BigDecimal wishPrice;
    private BigDecimal startRating;
    private BigDecimal endRating;
    private Integer page = 1;
    private Integer pageSize = 10;
}
