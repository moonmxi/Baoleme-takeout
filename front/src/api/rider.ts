import request from '@/utils/request';
import type { ApiResponse } from '@/types';

export const riderApi = {
  // 骑手注册
  register(data: { username: string; phone: string; password: string }) {
    return request.post<ApiResponse<any>>('/rider/register', data);
  },

  // 骑手登录
  login(data: { phone: string; password: string }) {
    return request.post<ApiResponse<{ token: string; username: string; user_id: number }>>('/rider/login', data);
  },

  // 骑手登出
  logout() {
    return request.post<ApiResponse<any>>('/rider/logout');
  },

  // 骑手注销
  delete() {
    return request.delete<ApiResponse<any>>('/rider/delete');
  },

  // 获取当前骑手信息
  getInfo() {
    return request.get<ApiResponse<{
        user_id: number;
        username: string;
        phone: string;
        order_status: number;
        dispatch_mode: number;
        balance: number;
        avatar: string | null;
        }>>('/rider/info');
  },

  // 修改当前骑手信息
  updateInfo(data: {
    username?: string;
    password?: string;
    phone?: string;
    order_status?: number;
    dispatch_mode?: number;
  }) {
    return request.put<ApiResponse<{
      token?: string;
      username?: string;
      user_id?: number;
    }>>('/rider/update', data);
  },

  // 上传骑手头像
uploadAvatar(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  const token = localStorage.getItem('rider_token') || '';
  return request.post<ApiResponse<string>>(
    '/api/image/upload-rider-avatar',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${token}`
      }
    }
  );
},
  // 查询可抢订单(少参数)！！！！！！！！！！！！！！！！！！
  getAvailableOrders(param: { page: number; page_size: number }) {
    return request.get<ApiResponse<{ orders: any[] }>>('/orders/available',{ params: param });
  },

  // 抢单
  grabOrder(order_id: number) {
    return request.put<ApiResponse<any>>('/orders/grab', { order_id });
  },
  //自动抢单
  autoGrabOrder(){
    return request.post<ApiResponse<any>>('/rider/auto-order-taking')
  },
  // 取消订单配送
  cancelOrder(order_id: number) {
    return request.put<ApiResponse<any>>('/orders/cancel', { order_id });
  },

  // 更新订单状态
  updateOrderStatus(order_id: number, target_status: number) {
    return request.post<ApiResponse<any>>('/orders/rider-update-status', { order_id, target_status });
  },

  // 查询骑手订单历史记录
  getOrderHistory(params: {
    status?: number;
    start_time?: string;
    end_time?: string;
    page: number;
    page_size: number;
  }) {
    return request.post<ApiResponse<any>>('/orders/rider-history-query', params);
  },

  // 获取收入统计
  getIncomeStats() {
  return request.get<ApiResponse<{
    total_earnings: number
    current_month: number
    completed_orders: number
  }>>('/orders/rider-earnings');
}
};
