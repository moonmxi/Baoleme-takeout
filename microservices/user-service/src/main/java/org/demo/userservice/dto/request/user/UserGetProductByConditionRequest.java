package org.demo.userservice.dto.request.user;

import lombok.Data;

@Data
public class UserGetProductByConditionRequest {
    private Long storeId;
    private String category;
    private Integer page;
    private Integer pageSize;
}
