package org.demo.riderservice.dto.request;

import lombok.Data;

@Data
public class RiderDispatchModeRequest {
    private Integer dispatchMode; // 0 = 手动，1 = 自动
}