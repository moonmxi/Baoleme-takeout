/**
 * 购物车商品响应DTO
 * 用于封装购物车中单个商品的信息
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.dto.response.cart;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车商品响应数据传输对象
 */
@Data
public class CartResponse {
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品数量
     */
    private Integer quantity;
    
    /**
     * 商品单价
     */
    private BigDecimal price;
    
    /**
     * 商品图片URL
     */
    private String imageUrl;
    
    /**
     * 店铺ID
     */
    private Long storeId;
    
    /**
     * 店铺名称
     */
    private String storeName;
}