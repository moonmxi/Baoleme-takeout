import request from '@/utils/request'
import type { ApiResponse } from '@/types'

export const adminApi = {
  // 管理员登录
  login(data: { admin_id: number, password: string }) {
    return request.post<ApiResponse<{ id: number, token: string }>>('/admin/login', data)
      .catch(error => {
        console.error('Login Error:', error);
        throw error;
      });
  },

  // 管理员登出
  logout() {
    return request.post<ApiResponse<{ data: string }>>('/admin/logout')
      .catch(error => {
        console.error('Logout Error:', error);
        throw error;
      });
  },

  // 管理员查看用户列表（分页）
  getUserList(params: { page: number; page_size: number }) {
    return request.post<ApiResponse<{ users: Array<{
      id: number
      username: string
      phone: string
      avatar: string | null
      created_at: string
    }> }>>('/admin/userlist',  params )
      .catch(error => {
        console.error('Get User List Error:', error);
        throw error;
      });
  },

  // 修改为GET请求来获取店铺列表（分页）
  getStoreList(params: { page: number; page_size: number }) {
    return request.post<ApiResponse<{ stores: Array<{
      id: number
      name: string
      description: string
      location: string
      rating: number
      balance: number
      status: number
      created_at: string
      image: string | null
    }> }>>('/admin/storelist',  params )
      .catch(error => {
        console.error('Get Store List Error:', error);
        throw error;
      });
  },

  // 管理员查看骑手列表（分页）
  getRiderList(params: { page: number; page_size: number }) {
    return request.post<ApiResponse<{ riders: Array<{
      id: number
      username: string
      phone: string
      order_status: number
      dispatch_mode: number
      balance: number
      avatar: string | null
      created_at: string
    }> }>>('/admin/riderlist',  params )
      .catch(error => {
        console.error('Get Rider List Error:', error);
        throw error;
      });
  },

  // 管理员查看商家列表（分页）
  getMerchantList(params: { page: number; page_size: number }) {
    return request.post<ApiResponse<{ merchants: Array<{
      id: number
      username: string
      phone: string
      avatar: string | null
      created_at: string
    }> }>>('/admin/merchantlist',  params )
      .catch(error => {
        console.error('Get Merchant List Error:', error);
        throw error;
      });
  },

  // 管理员删除（下架店铺/商品/用户/骑手/商家）
delete(data: {
  store_name?: string
  product_name?: string
  user_name?: string
  rider_name?: string
  merchant_name?: string
}) {
  return request.delete<ApiResponse<{ data: string }>>('/admin/delete', { data })
    .catch(error => {
      console.error('Delete Error:', error);
      throw error;
    });
},

  // 管理员按条件查看所有订单 !（可以考虑增加订单号搜索
  getOrderList(data: {
    user_id?: number
    store_id?: number
    rider_id?: number
    status?: number
    create_at?: string
    end_at?: string
    page?: number
    page_size?: number
  }) {
    return request.post<ApiResponse<{ orders: Array<any> }>>('/admin/orderlist', data)
      .catch(error => {
        console.error('Get Order List Error:', error);
        throw error;
      });
  },

  // 管理员按条件查看所有评价 （可以考虑增加评价号搜索
  getReviewList(data: {
    user_id?: number
    store_id?: number
    product_id?: number
    start_time?: string
    end_time?: string
    page?: number
    page_size?: number
    start_rating?: number
    end_rating?: number
  }) {
    return request.post<ApiResponse<{ reviews: Array<any> }>>('/admin/reviewlist', data)
      .catch(error => {
        console.error('Get Review List Error:', error);
        throw error;
      });
  },
  //管理员实现查看店铺商品
  getStoreProducts(data: { store_id: number, page: number, page_size: number }) {
    return request.post<ApiResponse<{ products: Array<any> }>>('/admin/productlist', data)
      .catch(error => {
        console.error('Get Store Products Error:', error);
        throw error;
      });
  },
  // 管理员搜索 !!
  search(data: { key_word: string }) {
    return request.post<ApiResponse<{ results: Array<any> }>>('/admin/search', data)
      .catch(error => {
        console.error('Search Error:', error);
        throw error;
      });
  }
}
