// 用户信息
export interface UserInfo {
  id: number
  username: string
  avatar: string
  phone: string
  description: string
  location: string
  gender: string
}
// 骑手信息
export interface RiderInfo {
  id: number
  username: string
  phone: string
  order_status: number
  dispatch_mode: number
  balance: number
  avatar: string
}

//商家信息
export interface MerchantInfo {
  id: number
  username: string
  phone: string
  createdAt: string
  avatar: string
}

// 店铺信息
export interface StoreInfo {
  id: number
  name: string
  logo: string
  description: string
  rating: number
  monthlySales: number
  deliveryTime: number
  deliveryFee: number
  minimumOrder: number
  tags: string[]
  notice: string
  createdAt: string
  updatedAt: string
}

// 商品分类
export interface Category {
  id: number
  name: string
  description: string
  storeId: number
  createdAt: string
  updatedAt: string
}

// 商品信息
export interface Product {
  id: number
  name: string
  image: string
  description: string
  price: number
  stock: number
  rating: number
  category: string
  store_id: number
  status: number // 0: 下架, 1: 上架
  createdAt: string
}

// 购物车项
export interface CartItem {
  id: number
  productId: number
  quantity: number
  product: Product
  createdAt: string
  updatedAt: string
}

// 订单状态
export type OrderStatus = 
  | 'pending'    // 待支付
  | 'paid'       // 已支付
  | 'preparing'  // 备餐中
  | 'delivering' // 配送中
  | 'completed'  // 已完成
  | 'cancelled'  // 已取消

// 订单信息
export interface Order {
  id: number
  user_id: number
  store_id: number
  rider_id: number
  items: Array<{
    productId: number
    quantity: number
    price: number
    product: Product
  }>
  totalPrice: number
  status: OrderStatus
  address: {
    name: string
    phone: string
    address: string
  }
  createdAt: string
  updatedAt: string
}

// API 响应格式
export interface ApiResponse<T = any> {
  success: boolean
  code: number
  message: string
  data: T
} 

export interface SearchResult {
  id: number
  name: string
  logo: string
  location: string
  description: string
  rating: number
  monthlySales: number
  deliveryTime: number
  deliveryFee: number
  minimumOrder: number
  tags: string[]
  notice: string
  createdAt: string
  updatedAt: string
  products: Product[]
}

export interface OrderItem {
  order_id: number
  created_at: string
  ended_at: string | null // 订单结束时间，可能为null
  status: number
  store_name: string
  remark: string | null   // 备注，可能为null
  rider_name: string
  rider_phone: string
}

export interface OrderHistoryResponseData {
  orders: OrderItem[]
}