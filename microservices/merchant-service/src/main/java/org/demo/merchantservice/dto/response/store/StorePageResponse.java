package org.demo.merchantservice.dto.response.store;

import lombok.Data;
import org.demo.merchantservice.pojo.Store;

import java.util.List;

@Data
public class StorePageResponse {
    private List<StoreViewInfoResponse> stores;
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;
}
