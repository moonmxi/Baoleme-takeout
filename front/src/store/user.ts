import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types'
import { userApi } from '@/api/user'
export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = ref<boolean>(!!token.value)

  // 设置 token
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
    isLoggedIn.value = true
  }

  // 清除 token
  function clearToken() {
    token.value = ''
    localStorage.removeItem('token')
    isLoggedIn.value = false
    userInfo.value = null
  }
  // 登录
  async function login(phone: string, password: string) {
    try {
      const res = await userApi.login({ phone, password })
      if (res.data?.token) {
        setToken(res.data.token)
        await getUserInfo()
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
  async function register(data: { username: string; phone: string; password: string }) {
    return await userApi.register(data)
  }
  // 获取用户信息
  async function getUserInfo() {
    try {
      const res = await userApi.getUserInfo()
      if (res.success) {
        userInfo.value = {
          id: res.data.user_id,
          username: res.data.username,
          phone: res.data.phone,

          description: res.data.description || '',
          location: res.data.location || '',
          gender: res.data.gender || '',
          avatar: res.data.avatar || '' // Provide a default or map accordingly
        }
        return res
      } else {
        throw new Error(res.message || '获取用户信息失败')
      }
    } catch (error) {
      clearToken()
      throw error
    }
  }
  // 更新用户信息
  async function updateUserInfo(data: { username?: string; user_id?: number;}) {
    try {
      const res = await userApi.updateUserInfo(data)
      if (res.success) {
        await getUserInfo()
      }
      return res
    } catch (error) {
      throw error
    }
  }
  // 登出
  async function logout() {
    try {
      await userApi.logout()
    } finally {
      clearToken()
    }
  }

  async function initializeStore() {
      if (token.value && !userInfo.value) {
        try {
          const res = await userApi.getUserInfo()
          if (res.success) {
            userInfo.value = {
              id: res.data.user_id,
              username: res.data.username,
              phone: res.data.phone,
              description: res.data.description || '',
              location: res.data.location || '',
              gender: res.data.gender || '',
              avatar: res.data.avatar || ''
            }
            isLoggedIn.value = true
          } else {
            clearToken()
          }
        } catch (error) {
          clearToken()
        }
      }
    }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    clearToken,
    login,
    register,
    getUserInfo,
    updateUserInfo,
    logout, 
    initializeStore
  }
}
)