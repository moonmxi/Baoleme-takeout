/**
 * 评论服务实现类
 * 实现评论相关的业务逻辑
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.common.mapper.ReviewMapper;
import org.demo.common.pojo.Page;
import org.demo.common.pojo.Review;
import org.demo.common.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务实现类
 * 提供评论相关业务逻辑的具体实现
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    /**
     * 构造函数注入ReviewMapper
     * 
     * @param reviewMapper 评论数据访问层
     */
    public ReviewServiceImpl(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    /**
     * 创建评论
     * 
     * @param review 评论对象（无需id和createdAt）
     * @return 包含生成的id和时间的评论对象
     * @throws RuntimeException 当创建失败时抛出异常
     */
    @Override
    @Transactional
    public Review createReview(Review review) {
        review.setId(null); // 确保ID由数据库生成
        review.setCreatedAt(null); // 由自动填充处理
        int result = reviewMapper.insert(review);
        if (result <= 0) {
            throw new RuntimeException("评论创建失败");
        }
        return review;
    }

    /**
     * 根据ID获取评论
     * 
     * @param reviewId 评论ID
     * @return 评论对象或null（如果不存在）
     */
    @Override
    public Review getReviewById(Long reviewId) {
        if (reviewId == null) {
            return null;
        }
        return reviewMapper.selectById(reviewId);
    }

    /**
     * 更新评论内容
     * 
     * @param review 需包含ID的评论对象
     * @return 是否成功更新
     */
    @Override
    @Transactional
    public boolean updateReview(Review review) {
        if (review == null || review.getId() == null) {
            return false;
        }
        int result = reviewMapper.updateById(review);
        return result > 0;
    }

    /**
     * 删除评论
     * 
     * @param reviewId 评论ID
     * @return 是否成功删除
     */
    @Override
    @Transactional
    public boolean deleteReview(Long reviewId) {
        if (reviewId == null) {
            return false;
        }
        int result = reviewMapper.deleteById(reviewId);
        return result > 0;
    }

    /**
     * 查询店铺的所有评论
     * 
     * @param storeId 店铺ID
     * @return 评论列表
     */
    @Override
    public List<Review> getReviewsByStore(Long storeId) {
        if (storeId == null) {
            return List.of();
        }
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getStoreId, storeId)
               .orderByDesc(Review::getCreatedAt);
        return reviewMapper.selectList(wrapper);
    }

    /**
     * 根据评分范围查询评论
     * 
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @return 评论列表
     */
    @Override
    public List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (minRating != null) {
            wrapper.ge(Review::getRating, minRating);
        }
        if (maxRating != null) {
            wrapper.le(Review::getRating, maxRating);
        }
        wrapper.orderByDesc(Review::getCreatedAt);
        return reviewMapper.selectList(wrapper);
    }

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
    @Override
    public Page<Review> getFilteredReviews(
            Long storeId,
            Integer minRating,
            Integer maxRating,
            Boolean hasImage,
            int page,
            int pageSize
    ) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 查询数据
        List<Review> reviews = reviewMapper.selectFilteredReviews(
                storeId, minRating, maxRating, hasImage, offset, pageSize
        );
        
        // 统计总数
        int totalCount = reviewMapper.countFilteredReviews(
                storeId, minRating, maxRating, hasImage
        );
        
        // 构建分页结果
        return buildPageResult(reviews, page, pageSize, totalCount);
    }

    /**
     * 分页查询店铺评论（不进行筛选）
     * 
     * @param storeId 店铺ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Review> getStoreReviewsPage(Long storeId, int page, int pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 查询数据
        List<Review> reviews = reviewMapper.selectByStoreIdWithPage(storeId, offset, pageSize);
        
        // 统计总数
        int totalCount = reviewMapper.countByStoreId(storeId);
        
        // 构建分页结果
        return buildPageResult(reviews, page, pageSize, totalCount);
    }

    /**
     * 根据商品ID查询评论
     * 
     * @param productId 商品ID
     * @return 评论列表
     */
    @Override
    public List<Review> getReviewsByProductId(Long productId) {
        if (productId == null) {
            return List.of();
        }
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId)
               .orderByDesc(Review::getCreatedAt);
        return reviewMapper.selectList(wrapper);
    }

    /**
     * 构建分页结果的辅助方法
     * 
     * @param reviews 查询结果列表
     * @param page 当前页码
     * @param pageSize 每页数量
     * @param totalCount 总记录数
     * @return 分页结果对象
     */
    private Page<Review> buildPageResult(List<Review> reviews, int page, int pageSize, int totalCount) {
        Page<Review> pageResult = new Page<>();
        pageResult.setList(reviews);
        pageResult.setCurrPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setCount(totalCount);
        
        // 计算总页数
        int totalPages = (totalCount + pageSize - 1) / pageSize;
        pageResult.setPageCount(totalPages);
        
        // 计算上一页和下一页
        pageResult.setPrePage(page > 1 ? page - 1 : null);
        pageResult.setNextPage(page < totalPages ? page + 1 : null);
        
        return pageResult;
    }
}