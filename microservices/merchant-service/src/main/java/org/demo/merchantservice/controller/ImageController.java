/**
 * 图片上传控制器
 * 处理商家头像、店铺图片、商品图片上传相关的HTTP请求
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.controller;

import org.demo.merchantservice.common.CommonResponse;
import org.demo.merchantservice.common.ResponseBuilder;
import org.demo.merchantservice.common.UserHolder;
import org.demo.merchantservice.service.MerchantService;
import org.demo.merchantservice.service.StoreService;
import org.demo.merchantservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * 图片上传控制器类
 * 提供商家头像、店铺图片、商品图片上传功能的REST API
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    /**
     * 商家服务接口
     */
    @Autowired
    private MerchantService merchantService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 商品服务接口
     */
    @Autowired
    private ProductService productService;

    /**
     * 文件上传根目录
     */
    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadDir;

    /**
     * 商家头像上传接口
     * 
     * @param file 上传的图片文件
     * @return 上传结果响应，包含图片访问路径
     */
    @PostMapping("/upload-merchant-avatar")
    public CommonResponse uploadMerchantAvatar(@RequestParam("file") MultipartFile file) {
        // 权限检查
        String role = UserHolder.getRole();
        if (!"merchant".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }

        return uploadImage(file, "merchant/avatar", (url) -> {
            Long merchantId = UserHolder.getId();
            return merchantService.updateMerchantAvatar(merchantId, url);
        });
    }

    /**
     * 店铺图片上传接口
     * 
     * @param file 上传的图片文件
     * @param storeId 店铺ID
     * @return 上传结果响应，包含图片访问路径
     */
    @PostMapping("/upload-store-image")
    public CommonResponse uploadStoreImage(@RequestParam("file") MultipartFile file,
                                          @RequestParam("store_id") Long storeId) {
        // 权限检查
        String role = UserHolder.getRole();
        if (!"merchant".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }

        // 验证店铺归属权
        Long merchantId = UserHolder.getId();
        if (!storeService.validateStoreOwnership(storeId, merchantId)) {
            return ResponseBuilder.fail("店铺不属于您，无权限操作");
        }

        return uploadImage(file, "store/image", (url) -> {
            return storeService.updateStoreImage(storeId, url);
        });
    }

    /**
     * 商品图片上传接口
     * 
     * @param file 上传的图片文件
     * @param productId 商品ID
     * @return 上传结果响应，包含图片访问路径
     */
    @PostMapping("/upload-product-image")
    public CommonResponse uploadProductImage(@RequestParam("file") MultipartFile file,
                                            @RequestParam("product_id") Long productId) {
        // 权限检查
        String role = UserHolder.getRole();
        if (!"merchant".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }

        // 验证商品归属权
        Long merchantId = UserHolder.getId();
        if (!productService.validateProductOwnership(productId, merchantId)) {
            return ResponseBuilder.fail("商品不属于您，无权限操作");
        }

        return uploadImage(file, "product/image", (url) -> {
            return productService.updateProductImage(productId, url);
        });
    }

    /**
     * 删除商家头像接口
     * 
     * @return 删除结果响应
     */
    @DeleteMapping("/delete-merchant-avatar")
    public CommonResponse deleteMerchantAvatar() {
        // 权限检查
        String role = UserHolder.getRole();
        if (!"merchant".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅商家可操作");
        }

        try {
            Long merchantId = UserHolder.getId();
            boolean deleted = merchantService.updateMerchantAvatar(merchantId, null);
            
            if (!deleted) {
                return ResponseBuilder.fail("删除商家头像失败");
            }

            return ResponseBuilder.ok("头像删除成功");

        } catch (Exception e) {
            return ResponseBuilder.fail("删除过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 通用图片上传方法
     * 
     * @param file 上传的文件
     * @param subPath 子路径
     * @param updateCallback 更新回调函数
     * @return 上传结果响应
     */
    private CommonResponse uploadImage(MultipartFile file, String subPath, UpdateCallback updateCallback) {
        // 文件校验
        if (file.isEmpty()) {
            return ResponseBuilder.fail("上传文件不能为空");
        }

        // 文件类型校验
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseBuilder.fail("只能上传图片文件");
        }

        // 文件大小校验（限制5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseBuilder.fail("文件大小不能超过5MB");
        }

        try {
            // 创建上传目录
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fullSubPath = subPath + "/" + dateFolder;
            Path folderPath = Paths.get(uploadDir, fullSubPath);

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + ext;
            Path filePath = folderPath.resolve(filename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 构建访问路径
            String relativePath = fullSubPath + "/" + filename;
            String accessUrl = "/uploads/" + relativePath;

            // 更新数据库
            boolean updated = updateCallback.update(accessUrl);
            
            if (!updated) {
                return ResponseBuilder.fail("更新图片信息失败");
            }

            return ResponseBuilder.ok(Map.of(
                    "message", "图片上传成功",
                    "image_url", accessUrl,
                    "file_size", file.getSize(),
                    "upload_time", LocalDateTime.now().toString()
            ));

        } catch (IOException e) {
            return ResponseBuilder.fail("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.fail("上传过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 更新回调接口
     */
    @FunctionalInterface
    private interface UpdateCallback {
        boolean update(String url);
    }
}