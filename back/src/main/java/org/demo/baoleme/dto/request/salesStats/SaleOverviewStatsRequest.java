package org.demo.baoleme.dto.request.salesStats;

import com.baomidou.mybatisplus.annotation.EnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaleOverviewStatsRequest {
    @NotNull(message = "店铺id不能为空")
    private Long storeId;

    private TimeRange timeRange = TimeRange.TODAY;

    public enum TimeRange {
        TODAY(0),
        THIS_WEEK(1),
        THIS_MONTH(2);

        @EnumValue
        private final int code;

        TimeRange(int code) {
            this.code = code;
        }
    }
}
