package org.demo.userservice.dto.request.user;

import lombok.Data;

@Data
public class UserCurrentOrderRequest {
    private Integer page;
    private Integer pageSize;
}