/**
 * 购物车查看响应DTO
 * 用于封装购物车的完整信息，包括商品列表和总价
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.dto.response.cart;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车查看响应数据传输对象
 */
@Data
public class CartViewResponse {
    
    /**
     * 购物车商品列表
     */
    private List<CartResponse> items;
    
    /**
     * 购物车总价
     */
    private BigDecimal totalPrice;
    
    /**
     * 商品总数量
     */
    private Integer totalQuantity;
    
    /**
     * 购物车是否为空
     */
    private Boolean isEmpty;
}