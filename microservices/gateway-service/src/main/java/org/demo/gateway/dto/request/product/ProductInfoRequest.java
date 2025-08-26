/**
 * 商品信息请求DTO
 * 用于封装获取商品详情的请求参数
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.gateway.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品信息请求数据传输对象
 */
@Data
public class ProductInfoRequest {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long id;
}