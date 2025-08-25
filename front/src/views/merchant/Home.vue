<template>
  <div class="merchant-home-container">
    <!-- 顶部信息栏 -->
    <div class="merchant-header">
      <div class="merchant-info">
        <el-avatar :size="80" :src="merchantInfo.avatar ? `http://localhost:8080/images/${merchantInfo.avatar}` : defaultAvatar" />
        <div class="info-content">
          <h2>{{ merchantInfo.username }}</h2>
          <p>{{ merchantInfo.phone || '未设置手机号' }}</p>
        </div>
      </div>
      <div class="merchant-actions">
        <el-button type="primary" @click="goToProfile">个人资料</el-button>
        <el-button type="danger" @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <!-- 店铺信息卡片 -->
    <div class="store-select-bar" style="margin-bottom: 16px;">
      <el-select
        v-model="selectedStoreId"
        placeholder="请选择店铺"
        style="width:220px"
        @change="handleStoreChange"
      >
        <el-option
          v-for="store in storeList"
          :key="store.store_id"
          :label="store.name"
          :value="store.id"
        />
      </el-select>
    </div>
    <el-card v-if="storeInfo" class="shop-card">
      <template #header>
        <div class="shop-header">
          <h3>当前店铺信息</h3>
        </div>
      </template>
      <div class="shop-content">
        <div class="shop-basic">
          <h4>店铺名：{{ storeInfo.name }}</h4>
          <p>描述：{{ storeInfo.description || '暂无描述' }}</p>
        </div>
        <div class="shop-stats">
          <div class="stat-item">
            <span class="label">评分：</span>
            <span class="value">{{ storeInfo.rating || '暂无评分' }}</span>
          </div>
        </div>
      </div>
    </el-card>
    <el-empty v-else description="暂无店铺信息，请先创建店铺">
      <el-button type="primary" @click="createStore">创建店铺</el-button>
    </el-empty>

    <!-- 功能导航区 -->
    <div class="feature-grid">
      <el-card v-for="feature in features" :key="feature.id" class="feature-card" @click="navigateTo(feature.route)">
        <el-icon :size="32" :color="feature.color">
          <component :is="feature.icon" />
        </el-icon>
        <span class="feature-name">{{ feature.name }}</span>
      </el-card>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted,onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ShoppingCart,
  Money,
  Star,
  Goods,
  List,
  Promotion,
  DataLine,
  Setting,
  Bell,
  ChatDotRound
} from '@element-plus/icons-vue'
import { ElNotification } from 'element-plus'
import { merchantApi } from '@/api/merchant'
import { useMerchantStore } from '@/store/merchant'
import{computed} from 'vue'
const merchantStore = useMerchantStore()
const router = useRouter()
const defaultAvatar = '/logo.png'
const storeList = ref<any[]>([])
const selectedStoreId = ref<number | null>(null)
// 商家信息
const merchantInfo = ref({
  user_id: 0,
  username: '',
  phone: '',
  avatar: '',
  created_at: ''
})

// 店铺信息
const storeInfo = ref({
  id: 0,
  name: '',
  description: '',
  rating: null,
  balance: null
})
// 所有店铺的状态为0和1的订单
const allOrders = ref<any[]>([])
// 分页
const orderPage = ref(1)
const orderPageSize = ref(5)
const orderTotal = ref(0)

// 分页后的订单
const pagedOrders = computed(() => {
  const start = (orderPage.value - 1) * orderPageSize.value
  return allOrders.value.slice(start, start + orderPageSize.value)
})
// 今日统计数据
const todayStats = ref({
  orderCount: 0,
  totalSales: '0.00'
})

// 功能导航
const features = [
  { id: 1, name: '商品管理', icon: 'Goods', route: '/merchant/products', color: '#409EFF' },
  { id: 2, name: '订单管理', icon: 'List', route: '/merchant/orders', color: '#67C23A' },
  { id: 3, name: '促销活动', icon: 'Promotion', route: '/merchant/promotions', color: '#E6A23C' },
  { id: 4, name: '销售统计', icon: 'DataLine', route: '/merchant/overview', color: '#F56C6C' },
  { id: 5, name: '店铺设置', icon: 'Setting', route: '/merchant/storeSetting', color: '#909399' }
]

// 订单通知
const recentOrders = ref<any[]>([])

// 用户评价
const recentReviews = ref<any[]>([])

const notifiedOrderIds = ref<Set<number>>(new Set())
let orderNotifyTimer: any = null

const startOrderNotifyPolling = () => {
  orderNotifyTimer = setInterval(async () => {
    try {
      // 只查当前选中店铺的待接单和备餐中订单
      if (!selectedStoreId.value) return
      const res = await merchantApi.getOrderList({
        store_id: selectedStoreId.value,
        status: '0', // 查全部，前端筛选
        page: 1,
        page_size: 20
      })
      console.log("轮询获取订单结果:", res)
      if (res.success && Array.isArray(res.data.list)) {
        console.log("轮询获取订单列表:", res.data.list)
        const newOrders = res.data.list.filter(
          (order: any) =>
            (order.status === 0 || order.status === 1) &&
            !notifiedOrderIds.value.has(order.order_id)
        )
        console.log("新订单:", newOrders)
        newOrders.forEach((order: { order_id: number; status: number }) => {
          ElNotification({
            title: '新订单提醒',
            message: `有新订单(订单号：${order.order_id}，状态：${getStatusText(order.status)})`,
            type: 'success',
            duration: 5000
          })
          notifiedOrderIds.value.add(order.order_id)
        })
      }
    } catch (e) {
      console.error('轮询获取订单失败:', e)
    }
  }, 1000) // 每10秒轮询一次
}

const loadStoreList = async () => {
  try {
    const res = await merchantApi.getStoreList({ page: 1, page_size: 100 })
    if (res.success) {
      // 统一id字段
      storeList.value = (res.data.stores || []).map((item: { id: any; store_id: any }) => ({
        ...item,
        id: item.id ?? item.store_id
      }))
      // 按创建时间升序排序
      storeList.value.sort((a, b) => new Date(a.created_at).getTime() - new Date(b.created_at).getTime())
      // 默认选中最早创建的
      if (storeList.value.length > 0) {
        selectedStoreId.value = storeList.value[0].id
        await loadStoreInfo()
      }
    }
  } catch (e) {
    ElMessage.error('获取店铺列表失败')
  }
}

// 获取所有店铺的状态为0和1的订单
const getAllRecentOrders = async () => {
  allOrders.value = []
  try {
    for (const store of storeList.value) {
      // 状态为0
      const res0 = await merchantApi.getOrderList({
        store_id: store.id,
        status: "0",
        page: 1,
        page_size: 100
      })
      if (res0.success && Array.isArray(res0.data.list)) {
        res0.data.list.forEach((order: any) => {
          order.store_name = store.name
        })
        allOrders.value.push(...res0.data.list)
      }
      // 状态为1
      const res1 = await merchantApi.getOrderList({
        store_id: store.id,
        status: "1",
        page: 1,
        page_size: 100
      })
      if (res1.success && Array.isArray(res1.data.list)) {
        res1.data.list.forEach((order: any) => {
          order.store_name = store.name
        })
        allOrders.value.push(...res1.data.list)
      }
    }
    // 按下单时间倒序
    allOrders.value.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    orderTotal.value = allOrders.value.length
  } catch (e) {
    console.error('获取订单失败', e)
  }
}
// 加载当前选中店铺信息
const loadStoreInfo = async () => {
  if (!selectedStoreId.value) {
    return
  }
  try {
    const res = await merchantApi.getStore({ store_id: selectedStoreId.value })
    if (res.success) {
      storeInfo.value = { ...res.data, id: selectedStoreId.value }
    } else {
    }
  } catch (e) {
    console.error('加载店铺信息失败:', e)
  }
}

// 切换店铺
const handleStoreChange = async () => {
  await loadStoreInfo()
}

// 获取商家信息
const getMerchantInfo = async () => {
  try {
    const response = await merchantApi.getMerchantInfo()
    if (response.success) {
      merchantInfo.value = response.data
    } else {
      ElMessage.error(response.message || '获取商家信息失败')
    }
  } catch (error) {
    ElMessage.error('获取商家信息失败')
  }
}

// 获取店铺信息
const getStoreInfo = async () => {
  try {
    const response = await merchantApi.getStore({ store_id: Number(merchantInfo.value.user_id) })
    if (response.success) {
      storeInfo.value = { ...storeInfo.value, ...response.data }
    } else {
      ElMessage.error(response.message || '获取店铺信息失败')
    }
  } catch (error) {
    ElMessage.error('获取店铺信息失败')
  }
}


// 跳转
const editStore = () => {
  router.push('/merchant/store/edit')
}
const createStore = () => {
  router.push('/merchant/store/create')
}
const goToProfile = () => {
  router.push('/merchant/info')
}
const navigateTo = (route: string) => {
  router.push(route)
}
const handleLogout = async () => {
  try {
    await merchantApi.logout()
    localStorage.removeItem('merchant_token')
    ElMessage.success('已退出登录')
    router.push('/merchant/login')
  } catch (error) {
    ElMessage.error('退出登录失败')
  }
}

// 状态映射
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return map[status] || 'info'
}
const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待接单',
    1: '备餐中',
    2: '配送中',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

onMounted(async () => {
   // 1. 如果没有 riderInfo 但有 token，尝试获取骑手信息
  if (!merchantStore.merchantInfo && localStorage.getItem('merchant_token')) {
    try {
      merchantStore.getMerchantInfo()
    } catch (error) {
      console.error('获取商家信息失败:', error)
      merchantStore.clearToken()
      router.push('/')
      return
    }
  }

  // 2. 加载统计数据、待配送订单、收入统计
  try {
      Promise.all([
        await getMerchantInfo(),
        // await getRecentOrders(),
        // await getRecentReviews(),
        loadStoreList(),
        startOrderNotifyPolling()

    ])
  } catch (error) {
    console.error('加载数据失败:', error)
  }

  // 可加定时轮询 getRecentOrders() 实现实时订单通知
})
onUnmounted(() => {
  if (orderNotifyTimer) clearInterval(orderNotifyTimer)
})
</script>

<style scoped lang="scss">
.merchant-home-container {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;
}
.merchant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  .merchant-info {
    display: flex;
    align-items: center;
    gap: 24px;
    .info-content {
      h2 { margin: 0; }
      p { margin: 8px 0 0; color: #888; }
    }
  }
}
.shop-card { margin-bottom: 32px; }
.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
  .feature-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 32px 0;
    cursor: pointer;
    transition: box-shadow 0.2s;
    &:hover { box-shadow: 0 4px 16px rgba(64,158,255,0.12); }
    .feature-name { margin-top: 16px; font-size: 16px; }
  }
}
.stats-section { margin-bottom: 32px; }
.stats-card { border-radius: 12px; }
.order-notify-card, .review-card { margin-bottom: 32px; }
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}
</style>