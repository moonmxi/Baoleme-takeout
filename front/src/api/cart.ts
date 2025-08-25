import request from '@/utils/request'
import type { ApiResponse, CartItem } from '@/types'

export const cartApi = {
  // 获取购物车
  getCart() {
    return request.get<ApiResponse<CartItem[]>>('/api/cart')
  },

  // 添加商品到购物车
  addToCart(data: { productId: number; quantity: number }) {
    return request.post<ApiResponse<CartItem>>('/api/cart', data)
  },

  // 更新购物车商品数量
  updateCartItem(itemId: number, data: { quantity: number }) {
    return request.put<ApiResponse<CartItem>>(`/api/cart/${itemId}`, data)
  },

  // 删除购物车商品
  removeCartItem(itemId: number) {
    return request.delete<ApiResponse<void>>(`/api/cart/${itemId}`)
  },

  // 清空购物车
  clearCart() {
    return request.delete<ApiResponse<void>>('/api/cart')
  }
} 