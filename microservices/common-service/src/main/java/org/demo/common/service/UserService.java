/**
 * 用户服务接口
 * 提供用户相关的业务逻辑方法
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.service;

import org.demo.common.dto.request.UserReviewRequest;
import org.demo.common.dto.response.UserReviewResponse;

/**
 * 用户服务接口
 * 定义用户相关的业务方法
 */
public interface UserService {
    
    /**
     * 提交评价
     * 
     * @param userId 用户ID
     * @param request 评价请求
     * @return 评价响应结果
     * @throws IllegalArgumentException 当店铺ID或商品ID无效时抛出异常
     */
    UserReviewResponse submitReview(Long userId, UserReviewRequest request);
}