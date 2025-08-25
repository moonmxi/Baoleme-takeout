import request from "@/utils/request";
import type { ApiResponse } from "@/types";
import type { OrderHistoryResponseData } from "@/types/index";
import { get } from "@/services/http";

export const userApi = {
  // 用户注册
  register(data: { username: string; phone: string; password: string }) {
    return request.post<
      ApiResponse<{ user_id: number; username: string; phone: string }>
    >("/user/register", data);
  },

  // 用户登录
  login(data: { phone: string; password: string }) {
    return request.post<
      ApiResponse<{
        token: string;
        user_id: number;
        username: string;
      }>
    >("/user/login", data);
  },

  // 用户登出
  logout() {
    return request.post<ApiResponse<null>>("/user/logout");
  },

  // 获取当前用户信息
  getUserInfo() {
    return request.get<
      ApiResponse<{
        user_id: number;
        username: string;
        phone: string;
        description: string;
        location: string;
        gender: string;
        avatar?: string;
      }>
    >("/user/info");
  },

  // 修改用户信息
  updateUserInfo(data: {
    user_id?: number;
    username?: string;
    phone?: string;
    password?: string;
    description?: string;
    location?: string;
    avatar?: string;
  }) {
    return request.put<
      ApiResponse<{
        token?: string;
      }>
    >("/user/update", data);
  },
  //用户订单记录
  /**
   * 订单状态
   * 0：待接单  1：准备中  2：配送中  3：完成  4：取消
   */
  getOrderHistory(params: {
    status?: number; // 订单状态 (可选)
    start_time?: string; // 开始时间 (可选, 格式如 'YYYY-MM-DDTHH:mm:ss')
    end_time?: string; // 结束时间 (可选, 格式如 'YYYY-MM-DDTHH:mm:ss')
    page: number; // 当前页码 (必填)
    page_size: number; // 每页数量 (必填)
  }) {
    return request.post<ApiResponse<OrderHistoryResponseData>>(
      "/user/history",
      params
    );
  },

  // 用户收藏店铺
  favoriteStore(params: { store_id: number }) {
    return request.post<ApiResponse<null>>("/user/favorite", params);
  },

  // 取消收藏店铺
  unfavoriteStore(store_id: number) {
    return request.post<ApiResponse<null>>("/user/deleteFavorite", { store_id } );
  },

  // 用户查看收藏店铺信息
  getFavoriteStores(
    params: {
      type?: string;
      distance?: number;
      wishPrice?: number;
      startRating?: number;
      endRating?: number;
      page?: number;
      page_size?: number;
    } = {}
  ) {
    return request.post<ApiResponse<any[]>>("/user/favorite/watch", params);
  },

  // 用户领取优惠券
  claimCoupon(params:{
    id: number
  }) {
    return request.post<ApiResponse<null>>("/user/coupon/claim", params);
  },

  // 用户查看我的优惠券(支付的时候选择)
  getMyCoupons(params:{
    store_id?:number
  } 
  ) {
    return request.post<ApiResponse<any>>("/user/coupon",params);
  },
  //用户查看商家发放的券
  getStoreCoupons(
    params:{
      store_id: number
    }
  ){
    return request.post<ApiResponse<any>>("/user/coupon/view", params)
  },
  // 用户注销
  delete() {
    return request.delete<ApiResponse<null>>("/user/delete");
  },

  // 用户查看当前订单
  getCurrentOrders() {
    return request.get<ApiResponse<{ orders: any[] }>>("/user/current");
  },

  // 用户搜索（全局搜索，支持多条件和分页）
  search(params: {
    keyword: string;
    distance?: number;
    wishPrice?: number;
    startRating?: number;
    endRating?: number;
    page?: number;
    pageSize?: number;
  }) {
    return request.post<ApiResponse<any>>("/user/search", params);
  },

  // 获取推荐店铺列表，实现的时候在网页上选择过滤器再刷新，进去网页时所有可选项为空
  getStoreList(params?: {
    type?: string;
    min_rating?: number;
    max_rating?: number;
    page?: number;
    page_size?: number;
  }) {
    return request.post<ApiResponse<any>>("/store/user-view-stores", params);
  },

  // 获取商品列表
  getProductList(params: { store_id: number; category?: string }) {
    return request.post<ApiResponse<any>>("/store/user-view-products", params);
  },

  // 提交评价，在订单界面获取信息后把店铺id和商品id传入，商品id自选
  submitReview(data: {
    store_id: number;
    product_id?: number;
    rating: number;
    comment?: string;
    images?: string[];
  }) {
    return request.post<ApiResponse<any>>("/user/review", data);
  },
  getOrderDetail(params: { order_id: number }) {
    return request.post<ApiResponse<any>>("/user/searchOrder", params);
  },
  gerOrderItems(params: { order_id: number }) {
    return request.post<ApiResponse<any>>("/user/searchOrderItem", params);
  },
  getStoreDetail(params: { id: number }) {
    return request.post<ApiResponse<any>>(`/store/storeInfo`, params);
  },
  getProductDetail(params: { id: number }) {
    return request.post<ApiResponse<any>>(`/product/productInfo`, params);
  },
  //更新浏览记录
  updateViewHistory(data: { store_id: number; view_time: string }) {
    return request.post<ApiResponse<any>>("/user/updateViewHistory", data);
  },
  //查看浏览记录
  getViewHistory(params: { page: number; page_size: number }) {
    return request.post<ApiResponse<any>>("/user/viewHistory", params);
  },
  
  // 提交订单
  submitOrder(data: {
    items: Array<{
      product_id: number;
      quantity: number;
    }>;
    couponId?: number;
    delivery_price: number;
    store_id: number;
    remark?: string;
  }) {
    return request.post<ApiResponse<any>>("/user/order", data);
  },
};
