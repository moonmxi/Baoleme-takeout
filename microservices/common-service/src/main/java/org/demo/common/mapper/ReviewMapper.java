/**
 * 评论数据访问层接口
 * 提供评论相关的数据库操作方法
 * 
 * @author System
 * @version 1.0
 * @since 2025-01-20
 */
package org.demo.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.demo.common.pojo.Review;

import java.util.List;

/**
 * 评论Mapper接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 * 同时定义评论相关的自定义查询方法
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    /**
     * 根据店铺ID分页查询评论
     * 
     * @param storeId 店铺ID
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 评论列表
     */
    @Select("SELECT * FROM review WHERE store_id = #{storeId} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Review> selectByStoreIdWithPage(@Param("storeId") Long storeId, 
                                        @Param("offset") int offset, 
                                        @Param("pageSize") int pageSize);
    
    /**
     * 根据店铺ID统计评论总数
     * 
     * @param storeId 店铺ID
     * @return 评论总数
     */
    @Select("SELECT COUNT(*) FROM review WHERE store_id = #{storeId}")
    int countByStoreId(@Param("storeId") Long storeId);
    
    /**
     * 根据条件分页查询评论
     * 
     * @param storeId 店铺ID
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @param hasImage 是否有图片
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 评论列表
     */
    @Select("""
        SELECT id, user_id, store_id, product_id, rating, comment, image, created_at
        FROM review
        WHERE store_id = #{storeId}
          AND (#{minRating} IS NULL OR rating >= #{minRating})
          AND (#{maxRating} IS NULL OR rating <= #{maxRating})
          AND (#{hasImage} IS NULL OR 
               (#{hasImage} = true AND image IS NOT NULL AND image != '') OR
               (#{hasImage} = false AND (image IS NULL OR image = '')))
        ORDER BY created_at DESC
        LIMIT #{offset}, #{pageSize}
    """)
    List<Review> selectFilteredReviews(@Param("storeId") Long storeId,
                                      @Param("minRating") Integer minRating,
                                      @Param("maxRating") Integer maxRating,
                                      @Param("hasImage") Boolean hasImage,
                                      @Param("offset") int offset,
                                      @Param("pageSize") int pageSize);
    
    /**
     * 根据条件统计评论数量
     * 
     * @param storeId 店铺ID
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @param hasImage 是否有图片
     * @return 评论数量
     */
    @Select("""
        SELECT COUNT(*)
        FROM review
        WHERE store_id = #{storeId}
          AND (#{minRating} IS NULL OR rating >= #{minRating})
          AND (#{maxRating} IS NULL OR rating <= #{maxRating})
          AND (#{hasImage} IS NULL OR 
               (#{hasImage} = true AND image IS NOT NULL AND image != '') OR
               (#{hasImage} = false AND (image IS NULL OR image = '')))
    """)
    int countFilteredReviews(@Param("storeId") Long storeId,
                            @Param("minRating") Integer minRating,
                            @Param("maxRating") Integer maxRating,
                            @Param("hasImage") Boolean hasImage);
}