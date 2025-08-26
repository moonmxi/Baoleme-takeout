package org.demo.userservice.dto.request.user;

import lombok.Data;

@Data
public class UserGetViewHistoryRequest {
    private Integer page;
    private Integer pageSize;
}
