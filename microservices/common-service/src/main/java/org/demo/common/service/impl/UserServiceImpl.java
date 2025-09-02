/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.service.impl;

import org.demo.common.mapper.ReviewMapper;
import org.demo.common.dto.request.UserReviewRequest;
import org.demo.common.dto.response.UserReviewResponse;
import org.demo.common.pojo.Review;
import org.demo.common.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 提供用户相关业务逻辑的具体实现
 */
@Service
public class UserServiceImpl implements UserService {

    private final ReviewMapper reviewMapper;

    /**
     * 构造函数注入依赖
     * 
     * @param reviewMapper 评论数据访问层
     */
    public UserServiceImpl(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    /**
     * 提交评价
     * 
     * @param userId 用户ID
     * @param request 评价请求
     * @return 评价响应结果
     * @throws RuntimeException 当评论提交失败时抛出异常
     */
    @Override
    @Transactional
    public UserReviewResponse submitReview(Long userId, UserReviewRequest request) {
        UserReviewResponse response = new UserReviewResponse();

        Long storeId = request.getStoreId();
        Long productId = request.getProductId();

        response.setComment(request.getComment());
        response.setRating(request.getRating());
        response.setImages(request.getImages());

        // 创建评论对象
        Review review = new Review();
        review.setUserId(userId);
        review.setStoreId(storeId);
        review.setProductId(productId);
        review.setRating(BigDecimal.valueOf(request.getRating()));
        review.setComment(request.getComment());
        
        // 图片处理，将列表转换为逗号分隔的字符串
        String imagesStr = (request.getImages() == null || request.getImages().isEmpty())
                ? null
                : String.join(",", request.getImages());
        review.setImage(imagesStr);
        review.setCreatedAt(LocalDateTime.now());

        // 插入评论
        int result = reviewMapper.insert(review);
        if (result <= 0) {
            throw new RuntimeException("评论提交失败");
        }

        return response;
    }
}