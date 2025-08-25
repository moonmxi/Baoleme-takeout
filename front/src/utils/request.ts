import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: '/api', // API 的基础URL
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 判断当前路由或 pinia 状态
    const path = window.location.pathname
    let token = ''
    if (path.startsWith('/user')) {
      token = localStorage.getItem('token') || ''
    } else if (path.startsWith('/rider')) {
      token = localStorage.getItem('rider_token') || ''
    } else if (path.startsWith('/merchant')) {
      token = localStorage.getItem('merchant_token') || ''
    } else if (path.startsWith('/admin')) {
      token = localStorage.getItem('admin_token') || ''
    }
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, message, data } = response.data
    
    if (code === 200) {
      return response.data
    }
    
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    // 处理 HTTP 错误状态
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 删除这里的自动跳转逻辑，只提示错误
          ElMessage.error(error.response.data.message || '请先登录')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error('网络错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

// 封装请求方法
const request = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, config)
  },
  
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config)
  },
  
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config)
  },
  
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, config)
  }
}

export default request 