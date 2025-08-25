import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminApi } from '@/api/admin'
import type { ApiResponse } from '@/types'

interface AdminInfo {
  id: number
  token: string
}

export const useAdminStore = defineStore('admin', () => {
  const token = ref<string>(localStorage.getItem('admin_token') || '')
  const adminInfo = ref<AdminInfo | null>(null)
  const isLoggedIn = ref<boolean>(!!token.value)

  // 设置token
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('admin_token', newToken)
    isLoggedIn.value = true
  }
  // 清除token
  function clearToken() {
    token.value = ''
    localStorage.removeItem('admin_token')
    isLoggedIn.value = false
    adminInfo.value = null
  }


  // 管理员登出
  async function logout() {
    try {
      await adminApi.logout()
    } finally {
      clearToken()
    }
  }

  // 管理员获取用户列表
  async function getUserList(params: { page: number; page_size: number }) {
    return await adminApi.getUserList(params)
  }

  // 管理员获取店铺列表
  async function getStoreList(params: { page: number; page_size: number }) {
    return await adminApi.getStoreList(params)
  }

  // 管理员获取骑手列表
  async function getRiderList(params: { page: number; page_size: number }) {
    return await adminApi.getRiderList(params)
  }

  // 管理员获取商家列表
  async function getMerchantList(params: { page: number; page_size: number }) {
    return await adminApi.getMerchantList(params)
  }

  // 管理员删除（下架）
  async function deleteItem(data: {
    store_name?: string
    product_name?: string
    user_name?: string
    rider_name?: string
    merchant_name?: string
  }) {
    return await adminApi.delete(data)
  }

  // 管理员查订单
  async function getOrderList(data: any) {
    return await adminApi.getOrderList(data)
  }

  // 管理员查评价
  async function getReviewList(data: any) {
    return await adminApi.getReviewList(data)
  }

  // 管理员搜索
  async function search(data: { key_word: string }) {
    return await adminApi.search(data)
  }

  return {
    token,
    adminInfo,
    isLoggedIn,
    setToken,
    clearToken,
    logout,
    getUserList,
    getStoreList,
    getRiderList,
    getMerchantList,
    deleteItem,
    getOrderList,
    getReviewList,
    search,
  }
})