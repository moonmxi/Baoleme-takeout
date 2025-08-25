import request from '@/utils/request'
import type { ApiResponse } from '@/types'

export const merchantApi = {
  // 商家注册
  register(data: { username: string; password: string; phone?: string }) {
    return request.post<ApiResponse<{ user_id: number; username: string; phone: string | null }>>('/merchant/register', data)
  },

  // 商家登录
  login(data: { username?: string; phone?: string; password: string }) {
    return request.post<ApiResponse<{ token: string; user_id: number; expires_in: number }>>('/merchant/login', data)
  },

  // 商家登出
  logout() {
    return request.post<ApiResponse<null>>('/merchant/logout')
  },

  // 商家注销
  delete() {
    return request.delete<ApiResponse<null>>('/merchant/delete')
  },

  // 获取当前商家信息
  getMerchantInfo() {
    return request.get<ApiResponse<{
      user_id: number
      username: string
      phone: string 
      created_at: string
      avatar: string 
    }>>('/merchant/info')
  },

  // 修改商家信息
  updateMerchantInfo(data: { username?: string; phone?: string; password?: string; avatar?: string }) {
    return request.put<ApiResponse<any>>('/merchant/update', data)
  },

  // 创建店铺
  createStore(data: { name: string }) {
    return request.post<ApiResponse<any>>('/store/create', data)
  },

  // 修改店铺信息
  updateStore(data: { id: number; name?: string; desc?: string; location?: string; status?: number; image?: string }) {
    return request.put<ApiResponse<any>>('/store/update', data)
  },

  // 删除店铺
  deleteStore(data: { store_id: number }) {
    return request.delete<ApiResponse<any>>('/store/delete', { data })
  },

  // 查看店铺信息
  getStore(data: { store_id: number }) {
    return request.post<ApiResponse<any>>('/store/view', data)
  },
  // 查看店铺列表
  getStoreList(data:{ page?: number; page_size?: number}) {
    return request.post<ApiResponse<any>>('/store/list', data)
  },
  // 查看店铺评价（全部/筛选）
  getStoreReviews(data: { store_id: number; type?: number; hasImage?: boolean; page?: number; page_size?: number }) {
    return request.post<ApiResponse<any>>('/store/reviews/list', data)
  },
  filterStoreReviews(data: { store_id: number; type?: number; hasImage?: boolean; page?: number; page_size?: number }) {
    return request.post<ApiResponse<any>>('/store/reviews/filter', data)
  },

  // 新增商品
  createProduct(data: { store_id: number; name: string; descripition?: string; price: number; stock?: number; category?: string; image?: string }) {
    return request.post<ApiResponse<{ product_id: number }>>('/product/create', data)
  },

  // 修改商品
  updateProduct(data: { product_id: number; name?: string; descripition?: string; price?: number; stock?: number; category?: string; status?: number; image?: string }) {
    return request.put<ApiResponse<any>>('/product/update', data)
  },

  // 查看商品列表
  getProductList(data: { store_id: number; status?: boolean; category_id?: string; page?: number; page_size?: number }) {
    return request.post<ApiResponse<any>>('/product/store-products', data)
  },

  // 删除商品
  deleteProduct(data: { id: number }) {
    return request.post<ApiResponse<any>>('/product/delete', data)
  },

  // 查看商品信息
  viewProduct(data: { product_id: number }) {
    return request.post<ApiResponse<any>>('/product/view', data)
  },

  // 设置促销活动（接口文档未详细给出，预留）
  createCoupon(data:{
    store_id:number;
    type: number; // 1: 折扣, 2: 满减
    discount?: number; // 折扣率 type=1
    full_amount?: number; // 满多少元可用 type=2
    reduce_amount?: number; // 减多少元 type=2
  }){
    return request.post<ApiResponse<any>>('/coupon/create',data)
  },
  // 订单列表
  getOrderList(data: { store_id: number; status?: string; page?: number; page_size?: number }) {
    return request.post<ApiResponse<any>>('/orders/merchant-list', data)
  },

  // 订单操作
  updateOrderStatus(data: { id: number; new_status: number; store_id: number; cancel_reason?: string }) {
    return request.put<ApiResponse<any>>('/orders/merchant-update', data)
  },

  // 销售统计-概况
  getSalesOverview(params: { store_id: number; time_range?: number }) {
    return request.post<ApiResponse<any>>('/stats-store/overview', params )
  },

  // 销售统计-趋势
  getSalesTrend(params: { store_id: number; type: number; num_of_recent_days?: number }) {
    return request.post<ApiResponse<any>>('/stats-store/trend',  params )
  },

  // 获取店铺信息
  getInfo(merchant_id: number) {
    return request.get<ApiResponse<any>>(`/store/info/${merchant_id}`)
  },

  // 更新店铺信息
  updateInfo(data: {
    name?: string
    description?: string
    address?: string
    phone?: string
    business_hours?: string
    status?: number
  }) {
    return request.put<ApiResponse<any>>('/store/update', data)
  },

  // 获取店铺统计数据
  getStats(store_id: number) {
    return request.get<ApiResponse<any>>(`/store/stats/${store_id}`)
  }
}