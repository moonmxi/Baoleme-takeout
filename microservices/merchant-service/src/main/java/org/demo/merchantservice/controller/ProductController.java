/**
 * 商品控制器
 * 处理商品相关的HTTP请求，包括创建、查询、更新、删除等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.controller;

import org.demo.merchantservice.common.CommonResponse;
import org.demo.merchantservice.common.ResponseBuilder;
import org.demo.merchantservice.common.UserHolder;
import org.demo.merchantservice.dto.request.product.*;
import org.demo.merchantservice.dto.response.product.*;
import org.demo.merchantservice.pojo.Product;
import org.demo.merchantservice.service.ProductService;
import org.demo.merchantservice.service.StoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品控制器类
 * 提供商品管理相关功能的REST API
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    /**
     * 商品服务接口
     */
    private final ProductService productService;
    
    /**
     * 店铺服务接口
     */
    private final StoreService storeService;

    /**
     * 构造函数
     * 
     * @param productService 商品服务实例
     * @param storeService 店铺服务实例
     */
    @Autowired
    public ProductController(
            ProductService productService,
            StoreService storeService
    ) {
        this.productService = productService;
        this.storeService = storeService;
    }

    /**
     * 创建商品接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 创建商品请求对象
     * @return 创建结果响应
     */
    @PostMapping("/create")
    public CommonResponse createProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductCreateRequest request
    ) {
        System.out.println("=== Create Product Request ===");
        System.out.println("Request Body: " + request);

        Product product = new Product();
        BeanUtils.copyProperties(request, product);

        if (!validateStoreOwnerShip(product)) return ResponseBuilder.fail("商品创建失败，商家没有权限");

        Product createdProduct = productService.createProduct(product);

        if (createdProduct == null) {
            CommonResponse errorResponse = ResponseBuilder.fail("商品创建失败，请检查输入参数");
            System.out.println("Response Body: " + errorResponse);
            return errorResponse;
        }

        ProductCreateResponse response = new ProductCreateResponse();
        response.setProductId(createdProduct.getId());
        System.out.println("Response Body: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 查看商品详情接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 商品查看请求对象
     * @return 商品详情响应
     */
    @PostMapping("/view")
    public CommonResponse getProductById(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductViewRequest request
    ) {
        System.out.println("=== View Product Request ===");
        System.out.println("Request Body: " + request);

        Long productId = request.getProductId();
        Product product = productService.getProductById(productId);

        if (product == null) {
            CommonResponse errorResponse = ResponseBuilder.fail("商品不存在");
            System.out.println("Response Body: " + errorResponse);
            return errorResponse;
        }

        if (!validateStoreOwnerShip(product)) return ResponseBuilder.fail("商品查看失败，商家没有权限");

        ProductViewResponse response = new ProductViewResponse();
        BeanUtils.copyProperties(product, response);
        System.out.println("Response Body: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 获取店铺商品列表接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 商品查看请求对象
     * @return 商品列表响应
     */
    @PostMapping("/store-products")
    public CommonResponse getProductsByStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductViewRequest request
    ) {
        System.out.println("=== Store Products Request ===");
        System.out.println("Request Body: " + request);

        Long storeId = request.getStoreId();
        int currentPage = request.getPage();
        int pageSize = request.getPageSize();

        if (!storeService.validateStoreOwnership(storeId, UserHolder.getId()))
            return ResponseBuilder.fail("商品查询失败，商家没有权限");

        List<Product> products = productService.getProductsByStore(storeId, currentPage, pageSize);

        List<ProductViewResponse> responses = products.stream()
                .map(product -> {
                    ProductViewResponse resp = new ProductViewResponse();
                    BeanUtils.copyProperties(product, resp);
                    return resp;
                })
                .collect(Collectors.toList());

        ProductPageResponse pageResponse = new ProductPageResponse();
        pageResponse.setProducts(responses);
        pageResponse.setCurrentPage(currentPage);

        System.out.println("Response Body: " + pageResponse);
        return ResponseBuilder.ok(pageResponse);
    }

    /**
     * 更新商品信息接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 商品更新请求对象
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public CommonResponse updateProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductUpdateRequest request
    ) {
        System.out.println("=== Update Product Request ===");
        System.out.println("Request Body: " + request);

        Long productId = request.getProductId();
        Product product = new Product();
        product.setId(productId);
        BeanUtils.copyProperties(request, product);

        boolean success = productService.updateProduct(product);
        Product newProduct = productService.getProductById(productId);

        if (!success) {
            CommonResponse errorResponse = ResponseBuilder.fail("更新失败，请检查商品 ID 是否存在");
            System.out.println("Response Body: " + errorResponse);
            return errorResponse;
        }

        ProductUpdateResponse response = new ProductUpdateResponse();
        BeanUtils.copyProperties(newProduct, response);

        System.out.println("Response Body: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 更新商品状态接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 商品状态更新请求对象
     * @return 状态更新结果响应
     */
    @PutMapping("/status")
    public CommonResponse updateProductStatus(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductUpdateRequest request
    ) {
        System.out.println("=== Update Status Request ===");
        System.out.println("Request Body: " + request);

        Long productId = request.getProductId();
        int status = request.getStatus();

        boolean success = productService.updateProductStatus(productId, status);

        CommonResponse response = success ?
                ResponseBuilder.ok("商品状态更新成功") :
                ResponseBuilder.fail("状态更新失败");
        System.out.println("Response Body: " + response);
        return response;
    }

    /**
     * 删除商品接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 商品删除请求对象
     * @return 删除结果响应
     */
    @PostMapping("/delete")
    public CommonResponse deleteProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductDeleteRequest request
    ) {
        System.out.println("=== Delete Product Request ===");
        System.out.println("Request Body: " + request);

        Long productId = request.getId();
        boolean success = productService.deleteProduct(productId);

        CommonResponse response = success ?
                ResponseBuilder.ok("商品删除成功") :
                ResponseBuilder.fail("删除失败，商品可能不存在");
        System.out.println("Response Body: " + response);
        return response;
    }

    /**
     * 验证店铺归属权的私有方法
     * 
     * @param product 商品对象
     * @return 是否属于当前商家
     */
    private boolean validateStoreOwnerShip(Product product) {
        Long merchantId = UserHolder.getId();
        return storeService.validateStoreOwnership(product.getStoreId(), merchantId);
    }
}