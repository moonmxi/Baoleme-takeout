/**
 * 店铺控制器
 * 处理店铺相关的HTTP请求，包括创建、查询、更新、删除等功能
 * 
 * @author Baoleme Team
 * @version 1.0
 * @since 2025-01-25
 */
package org.demo.merchantservice.controller;

import org.demo.merchantservice.common.*;
import org.demo.merchantservice.dto.request.store.*;
import org.demo.merchantservice.dto.response.store.*;
import org.demo.merchantservice.pojo.Store;
import org.demo.merchantservice.service.StoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 店铺控制器类
 * 提供店铺管理相关功能的REST API
 */
@RestController
@RequestMapping("/store")
public class StoreController {

    /**
     * 店铺服务接口
     */
    private final StoreService storeService;

    /**
     * 构造函数
     * 
     * @param storeService 店铺服务实例
     */
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * 创建店铺接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 创建店铺请求对象
     * @return 创建结果响应
     */
    @PostMapping("/create")
    public CommonResponse createStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreCreateRequest request
    ) {
        System.out.println("收到创建请求: " + request);

        Long merchantId = UserHolder.getId();

        Store store = new Store();
        store.setMerchantId(merchantId);
        BeanUtils.copyProperties(request, store);

        Store createdStore = storeService.createStore(store);

        if (createdStore == null) {
            System.out.println("创建失败，店铺名称: " + request.getName());
            return ResponseBuilder.fail("店铺创建失败，参数校验不通过");
        }

        StoreCreateResponse response = new StoreCreateResponse();
        response.setId(createdStore.getId());

        System.out.println("创建成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 获取店铺列表接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 店铺列表请求对象
     * @return 店铺列表响应
     */
    @PostMapping("/list")
    public CommonResponse listStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreListRequest request
    ){
        System.out.println("收到查询请求: " + request);

        List<Store> stores = storeService.getStoresByMerchant(
                UserHolder.getId(),
                request.getPage(),
                request.getPageSize()
        );

        List<StoreViewInfoResponse> storeViewInfoResponses = stores.stream()
                .map(store -> {
                    StoreViewInfoResponse resp = new StoreViewInfoResponse();
                    BeanUtils.copyProperties(store, resp);
                    resp.setStoreId(store.getId());
                    return resp;
                })
                .collect(Collectors.toList());

        StorePageResponse response = new StorePageResponse();
        response.setStores(storeViewInfoResponses);
        response.setCurrentPage(request.getPage());

        System.out.println("[INFO] 已应答");
        return ResponseBuilder.ok(response);
    }

    /**
     * 查看店铺详情接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 店铺查看请求对象
     * @return 店铺详情响应
     */
    @PostMapping("/view")
    public CommonResponse getStoreById(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreViewInfoRequest request
    ) {
        System.out.println("收到查询请求: " + request);

        Long storeId = request.getStoreId();

        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        Store store = storeService.getStoreById(storeId);

        if (store == null) {
            System.out.println("查询失败，无效店铺ID: " + storeId);
            return ResponseBuilder.fail("店铺ID不存在");
        }

        StoreViewInfoResponse response = new StoreViewInfoResponse();
        BeanUtils.copyProperties(store, response);

        System.out.println("查询成功，店铺信息: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 更新店铺信息接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 店铺更新请求对象
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public CommonResponse updateStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreUpdateRequest request
    ) {
        System.out.println("收到更新请求: " + request);

        Long storeId = request.getId();
        Store store = new Store();
        store.setId(storeId);

        if (!validateStoreOwnership(store.getId())) return ResponseBuilder.fail("店铺不属于您");

        BeanUtils.copyProperties(request, store);

        boolean success = storeService.updateStore(store);
        Store newStore = storeService.getStoreById(storeId);

        if (!success) {
            System.out.println("更新失败，店铺ID: " + request.getId());
            return ResponseBuilder.fail("店铺信息更新失败");
        }

        StoreUpdateResponse response = new StoreUpdateResponse();
        BeanUtils.copyProperties(newStore, response);

        System.out.println("更新成功，响应: " + response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 切换店铺状态接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 店铺状态更新请求对象
     * @return 状态更新结果响应
     */
    @PutMapping("/status")
    public CommonResponse toggleStoreStatus(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreUpdateRequest request
    ) {
        System.out.println("收到状态修改请求: " + request);

        Long storeId = request.getId();
        int status = request.getStatus();

        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        if (status < 0 || status > 1) {
            System.out.println("非法状态值: " + status);
            return ResponseBuilder.fail("状态值必须是0或1");
        }

        boolean success = storeService.toggleStoreStatus(storeId, status);

        System.out.println(success ? "状态修改成功" : "状态修改失败");
        return success ?
                ResponseBuilder.ok("店铺状态更新成功") :
                ResponseBuilder.fail("状态更新失败，店铺可能不存在");
    }

    /**
     * 删除店铺接口
     * 
     * @param tokenHeader 请求头中的Authorization token
     * @param request 店铺删除请求对象
     * @return 删除结果响应
     */
    @DeleteMapping("/delete")
    public CommonResponse deleteStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StoreDeleteRequest request
    ) {
        System.out.println("收到删除请求: " + request);

        Long storeId = request.getStoreId();

        if (!validateStoreOwnership(storeId)) return ResponseBuilder.fail("店铺不属于您");

        boolean success = storeService.deleteStore(storeId);

        System.out.println(success ? "删除成功" : "删除失败");
        return success ?
                ResponseBuilder.ok("店铺数据已删除") :
                ResponseBuilder.fail("删除操作失败，店铺可能不存在");
    }

    /**
     * 获取店铺信息接口
     * 
     * @param request 店铺信息请求对象
     * @return 店铺信息响应
     */
    @PostMapping("/storeInfo")
    public CommonResponse getStoreInfo(@RequestBody StoreInfoRequest request) {
        Long id = request.getId();
        Store store = storeService.getStoreById(id);
        StoreInfoResponse response = new StoreInfoResponse();
        BeanUtils.copyProperties(store, response);
        return ResponseBuilder.ok(response);
    }

    /**
     * 验证店铺归属权的私有方法
     * 
     * @param storeId 店铺ID
     * @return 是否属于当前商家
     */
    private boolean validateStoreOwnership(Long storeId) {
        Long merchantId = UserHolder.getId();
        return storeService.validateStoreOwnership(storeId, merchantId);
    }
}