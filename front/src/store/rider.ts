import { defineStore } from 'pinia'
import { ref } from 'vue'
import  {riderApi} from '@/api/rider'
import type { UserInfo } from '@/types'
import type { RiderInfo } from '@/types'

export const useRiderStore = defineStore('rider', () => {
  const token = ref<string>(localStorage.getItem('rider_token') || '')
  const riderInfo = ref<RiderInfo | null>(null)
  const isLoggedIn = ref<boolean>(!!token.value)

  // 设置 token
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('rider_token', newToken)
    isLoggedIn.value = true
  }

  // 清除 token
  function clearToken() {
    token.value = ''
    localStorage.removeItem('rider_token')
    isLoggedIn.value = false
    riderInfo.value = null
  }


  // 获取骑手信息
// 获取骑手信息
async function getRiderInfo() {
  try {
    const res = await riderApi.getInfo()
    if (res.success) {
      // 数据库rider表有 balance 字段，接口返回也有 balance 字段
      // riderApi.getInfo() 返回的数据结构如下（见接口文档3.3.5）：
      // {
      //   "user_id": 30000005,
      //   "username": "1234",
      //   "phone": "4445",
      //   "order_status": 1,
      //   "dispatch_mode": 1,
      //   "balance": 0,
      //   "avatar": null
      // }
      riderInfo.value = {
        id: res.data.user_id,
        username: res.data.username,
        phone: res.data.phone,
        order_status: res.data.order_status,
        dispatch_mode: res.data.dispatch_mode,
        balance: res.data.balance ?? 0,
        avatar: res.data.avatar ?? ''
      }
      return res
    } else {
      throw new Error(res.message || '获取骑手信息失败')
    }
  } catch (error) {
    clearToken()
    throw error
  }
}

  async function clearRiderInfo() {
    riderInfo.value = null
  }

  // 更新骑手信息
  async function updateInfo(data: Partial<UserInfo>) {
    try {
      const res = await riderApi.updateInfo(data)
      if (res.success) {
        await getRiderInfo()
      }
      return res
    } catch (error) {
      throw error
    }
  }

  // 登出
  function logout() {
    clearToken()
  }

  return {
    token,
    riderInfo,
    isLoggedIn,
    getInfo: getRiderInfo,
    clearRiderInfo,
    updateInfo,
    logout,
    setToken,
    clearToken
  }
})