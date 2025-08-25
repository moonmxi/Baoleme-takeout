import { defineStore } from 'pinia'
import { ref } from 'vue'
import { merchantApi } from '@/api/merchant'
import type { StoreInfo, MerchantInfo } from '@/types'

export const useMerchantStore = defineStore('merchant', () => {
  const token = ref<string>(localStorage.getItem('merchant_token') || '')
  const merchantInfo = ref<MerchantInfo | null>(null)
  const isLoggedIn = ref<boolean>(!!token.value)

  // 设置 token
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('merchant_token', newToken)
    isLoggedIn.value = true
  }

  // 清除 token
  function clearToken() {
    token.value = ''
    localStorage.removeItem('merchant_token')
    isLoggedIn.value = false
    merchantInfo.value = null
  }

  async function login(username: string, password: string) {
    try {
      const res = await merchantApi.login({ username, password })
      if (res.data?.token) {
        setToken(res.data.token)
        await getMerchantInfo()
        return res
      } else {
        throw new Error(res.message || '登录失败')
      }
    } catch (error) {
      clearToken()
      throw error
    }
  }

  // 注册
  async function register(data: { username: string; password: string; phone?: string }) {
    return await merchantApi.register(data)
  }

  // 获取商家信息
  async function getMerchantInfo() {
    try {
      const res = await merchantApi.getMerchantInfo()
      merchantInfo.value = {
        id: res.data.user_id,
        username: res.data.username,
        phone: res.data.phone ?? '',
        createdAt: res.data.created_at,
        avatar: res.data.avatar ?? ''
      }
      return res
    } catch (error) {
      clearToken()
      throw error
    }
  }
  // 更新商家信息
async function updateMerchantInfo(data: { username?: string; phone?: string; password?: string; avatar?: string }) {
    const res = await merchantApi.updateMerchantInfo(data)
    if (merchantInfo.value) {
      merchantInfo.value = { ...merchantInfo.value, ...data }
    }
    return res
  }

  // 登出
  async function logout() {
    try {
      await merchantApi.logout()
    } finally {
      clearToken()
    }
  }


  return {
    token,
    merchantInfo: merchantInfo,
    isLoggedIn,
    setToken,
    clearToken,
    login,
    register,
    getMerchantInfo: getMerchantInfo,
    updateMerchantInfo: updateMerchantInfo,
    logout,
  }
})