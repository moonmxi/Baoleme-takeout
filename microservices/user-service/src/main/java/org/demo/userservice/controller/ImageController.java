/**
 * 图片上传控制器
 * 处理用户头像上传相关的HTTP请求
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.userservice.controller;

import org.demo.userservice.common.CommonResponse;
import org.demo.userservice.common.ResponseBuilder;
import org.demo.userservice.common.UserHolder;
import org.demo.userservice.service.UserService;
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
 * 提供用户头像上传功能的REST API
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    /**
     * 用户服务接口
     */
    @Autowired
    private UserService userService;

    /**
     * 文件上传根目录
     */
    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadDir;

    /**
     * 用户头像上传接口
     * 
     * @param file 上传的图片文件
     * @return 上传结果响应，包含图片访问路径
     */
    @PostMapping("/upload-user-avatar")
    public CommonResponse uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        // 权限检查
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

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
            String subFolder = "user/avatar/" + dateFolder;
            Path folderPath = Paths.get(uploadDir, subFolder);

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
            String relativePath = subFolder + "/" + filename;
            String accessUrl = "/uploads/" + relativePath;

            // 更新用户头像信息
            Long userId = UserHolder.getId();
            boolean updated = userService.updateUserAvatar(userId, accessUrl);
            
            if (!updated) {
                return ResponseBuilder.fail("更新用户头像失败");
            }

            return ResponseBuilder.ok(Map.of(
                    "message", "头像上传成功",
                    "avatar_url", accessUrl,
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
     * 删除用户头像接口
     * 
     * @return 删除结果响应
     */
    @DeleteMapping("/delete-user-avatar")
    public CommonResponse deleteUserAvatar() {
        // 权限检查
        String role = UserHolder.getRole();
        if (!"user".equals(role)) {
            return ResponseBuilder.fail("无权限访问，仅用户可操作");
        }

        try {
            Long userId = UserHolder.getId();
            boolean deleted = userService.updateUserAvatar(userId, null);
            
            if (!deleted) {
                return ResponseBuilder.fail("删除用户头像失败");
            }

            return ResponseBuilder.ok("头像删除成功");

        } catch (Exception e) {
            return ResponseBuilder.fail("删除过程中发生错误: " + e.getMessage());
        }
    }
}