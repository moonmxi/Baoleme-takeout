/**
 * 订单详情响应DTO
 * 包含完整的订单信息以及相关的用户、商家、店铺、商品等详细信息
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.common.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.demo.common.pojo.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单详情响应类
 * 提供完整的订单信息和相关实体详情
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderDetailResponse {
    
    /**
     * 订单基本信息
     */
    private Long orderId;
    private Integer status;
    private String userLocation;
    private String storeLocation;
    private BigDecimal totalPrice;
    private BigDecimal actualPrice;
    private BigDecimal deliveryPrice;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private LocalDateTime endedAt;
    
    /**
     * 用户信息
     */
    private Long userId;
    private String username;
    private String userPhone;
    private String userAvatar;
    
    /**
     * 店铺信息
     */
    private Long storeId;
    private String storeName;
    private String storeDescription;
    private String storeType;
    private BigDecimal storeRating;
    private String storeImage;
    
    /**
     * 商家信息
     */
    private Long merchantId;
    private String merchantName;
    private String merchantPhone;
    private String merchantAvatar;
    
    /**
     * 骑手信息
     */
    private Long riderId;
    private String riderName;
    private String riderPhone;
    private String riderAvatar;
    
    /**
     * 订单项列表（可选）
     */
    private List<OrderItemDetail> orderItems;
    
    /**
     * 价格信息（可选）
     */
    private Map<String, Object> priceInfo;
    
    /**
     * 订单项详情内部类
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OrderItemDetail {
        private Long productId;
        private String productName;
        private String productDescription;
        private String productImage;
        private String productCategory;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal totalPrice;
    }
}