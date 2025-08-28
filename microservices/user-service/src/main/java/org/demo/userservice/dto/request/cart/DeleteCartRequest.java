/**
 * 删除购物车商品请求DTO
 * 用于封装删除购物车中指定商品的请求参数
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.dto.request.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 删除购物车商品请求数据传输对象
 */
@Data
public class DeleteCartRequest {
    
    /**
     * 要删除的商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
}