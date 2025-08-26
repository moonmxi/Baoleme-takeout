/**
 * 商品业务服务接口
 * 定义商品相关的业务操作方法
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.service;

import org.demo.merchantservice.pojo.Product;

import java.util.List;

/**
 * 商品业务服务接口
 * 提供商品创建、查询、更新、删除等业务方法
 */
public interface ProductService {

    /**
     * 创建商品
     * 
     * @param product 商品信息
     * @return 创建成功的商品对象，失败返回null
     */
    Product createProduct(Product product);

    /**
     * 根据ID获取商品信息
     * 
     * @param productId 商品ID
     * @return 商品信息，不存在返回null
     */
    Product getProductById(Long productId);

    /**
     * 根据店铺ID获取商品列表
     * 
     * @param storeId 店铺ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 商品列表
     */
    List<Product> getProductsByStore(Long storeId, Integer page, Integer pageSize);

    /**
     * 更新商品信息
     * 
     * @param product 要更新的商品信息
     * @return 是否更新成功
     */
    boolean updateProduct(Product product);

    /**
     * 更新商品状态
     * 
     * @param productId 商品ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateProductStatus(Long productId, Integer status);

    /**
     * 删除商品
     * 
     * @param productId 商品ID
     * @return 是否删除成功
     */
    boolean deleteProduct(Long productId);

    /**
     * 验证商品归属权
     * 
     * @param productId 商品ID
     * @param merchantId 商家ID
     * @return 是否属于该商家
     */
    boolean validateProductOwnership(Long productId, Long merchantId);

    /**
     * 更新商品图片
     * 
     * @param productId 商品ID
     * @param imageUrl 图片URL，null表示删除图片
     * @return 是否更新成功
     */
    boolean updateProductImage(Long productId, String imageUrl);
}