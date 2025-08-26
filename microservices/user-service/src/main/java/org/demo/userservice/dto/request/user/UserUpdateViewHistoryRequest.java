package org.demo.userservice.dto.request.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserUpdateViewHistoryRequest {
    private Long storeId;
    private LocalDateTime viewTime;
}
