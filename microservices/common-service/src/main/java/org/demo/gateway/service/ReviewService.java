/**
 * 评论服务接口
 * 定义评论相关的业务逻辑方法
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.gateway.service;

import org.demo.gateway.pojo.Page;
import org.demo.gateway.pojo.Review;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务接口
 * 提供评论的增删改查和业务逻辑处理
 */
public interface ReviewService {

    /**
     * 创建评论
     * 
     * @param review 评论对象（无需id和createdAt）
     * @return 包含生成的id和时间的评论对象
     * @throws RuntimeException 当创建失败时抛出异常
     */
    Review createReview(Review review);

    /**
     * 根据ID获取评论
     * 
     * @param reviewId 评论ID
     * @return 评论对象或null（如果不存在）
     */
    Review getReviewById(Long reviewId);

    /**
     * 更新评论内容
     * 
     * @param review 需包含ID的评论对象
     * @return 是否成功更新
     */
    boolean updateReview(Review review);

    /**
     * 删除评论
     * 
     * @param reviewId 评论ID
     * @return 是否成功删除
     */
    boolean deleteReview(Long reviewId);

    /**
     * 查询店铺的所有评论
     * 
     * @param storeId 店铺ID
     * @return 评论列表
     */
    List<Review> getReviewsByStore(Long storeId);

    /**
     * 根据评分范围查询评论
     * 
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @return 评论列表
     */
    List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating);

    /**
     * 根据条件分页查询评论（支持筛选）
     * 
     * @param storeId 店铺ID
     * @param minRating 最低评分（可选）
     * @param maxRating 最高评分（可选）
     * @param hasImage 是否有图片（可选）
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Review> getFilteredReviews(
            Long storeId,
            Integer minRating,
            Integer maxRating,
            Boolean hasImage,
            int page,
            int pageSize
    );

    /**
     * 分页查询店铺评论（不进行筛选）
     * 
     * @param storeId 店铺ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    Page<Review> getStoreReviewsPage(Long storeId, int page, int pageSize);

    /**
     * 根据商品ID查询评论
     * 
     * @param productId 商品ID
     * @return 评论列表
     */
    List<Review> getReviewsByProductId(Long productId);
}