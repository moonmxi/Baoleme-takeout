/**
 * 更新购物车请求DTO
 * 用于封装更新购物车商品数量的请求参数
 * 从common-service迁移而来
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新购物车请求数据传输对象
 */
@Data
public class UpdateCartRequest {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 修改后的数量
     */
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
}