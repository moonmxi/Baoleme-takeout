/**
 * 用户当前订单查询请求DTO
 * 用于用户查询当前进行中订单的请求参数
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
package org.demo.gateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户当前订单查询请求类
 * 包含查询当前订单所需的分页参数
 */
@Data
public class UserCurrentOrderRequest {

    /**
     * 页码，默认为1
     */
    private Integer page = 1;

    /**
     * 每页大小，默认为10
     */
    @JsonProperty("page_size")
    private Integer pageSize = 10;
}